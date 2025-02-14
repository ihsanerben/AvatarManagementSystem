package com.ihsan.service;

import com.ihsan.entity.FileEntity;
import com.ihsan.entity.UserEntity;
import com.ihsan.exception.*;
import com.ihsan.repository.FileRepository;
import com.ihsan.repository.UserRepository;
import com.ihsan.util.AuthUtil;
import com.ihsan.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheService cacheService;

    private static final long CACHE_EXPIRATION = 10; // Dosya cache süresi (dakika)

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public FileEntity getFileByUsername(String username, String txnId) {

        // Tüm dosyaları al
        List<FileEntity> fileArr = getAllFiles();
        FileEntity theFileEntity = null;

        for (FileEntity currentFileEntity : fileArr) {
            if (currentFileEntity.getUser().getUsername().equals(username)) {
                theFileEntity = currentFileEntity;
                break;
            }
        }

        // Dosya bulunamazsa hata fırlat
        if (theFileEntity == null) {
            loggerService.error(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [ERROR][DB] getFileByUsername() -> Bu kullanıcının dosyası yok - username: " + username);
            throw new FileNotFoundException();
        }
        return theFileEntity;
    }

    public FileEntity getFile(Long fileId, String txnId) {
        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [SERVICE] getFile() Dosya önce rediste aranacak, yoksa DB'de aranacak. fileId: " + fileId);
        // Önce Redis’ten almayı dene
        FileEntity cachedFile = cacheService.get("file", fileId, FileEntity.class, txnId);
        if (cachedFile != null) {
            loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [CACHE] Dosya Redis’ten alındı: " + cachedFile.getFileName());
            return cachedFile;
        }

        // Eğer Redis’te yoksa, DB’den al ve Redis’e ekle
        Optional<FileEntity> optionalFile = fileRepository.findById(fileId);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundException();
        }

        cacheService.put("file", fileId, optionalFile.get(), txnId);
        loggerService.info(txnId, "[CACHE] Dosya Redis’e eklendi: " + optionalFile.get().getFileName());

        return optionalFile.get();
    }


    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }

    public void deleteFile(Long fileId, String txnId) {
        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [SERVICE] deleteFile() başladı - fileId: " + fileId);

        Optional<FileEntity> fileEntityOptional = fileRepository.findById(fileId);

        if (fileEntityOptional.isEmpty()) {
            loggerService.error(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [ERROR][SERVICE] deleteFile() -* file bulunamadı *- fileId: " + fileId);
            throw new FileNotFoundException();
        }

        fileRepository.delete(fileEntityOptional.get());
        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [DB] Dosya DB'ten silindi: fileID: " + fileId);


        // **Redis'ten Sil**
        cacheService.delete("file", fileId, txnId);
        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [CACHE] Dosya Redis'ten silindi: fileID: " + fileId);
        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [SERVICE] deleteFile() tamamlandı - fileId: " + fileId);
    }

    public FileEntity uploadFile(MultipartFile file, String username, String txnId) throws IOException {
        loggerService.info(txnId, "[username: " + username + "] [SERVICE] Dosya DB ve redise eklenecek - fileName: " + file.getOriginalFilename());
        loggerService.info(txnId, "[username: " + username + "] [SERVICE] uploadFile() başladı - fileName: " + file.getOriginalFilename());

        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        if (!isSupportedFileType(fileName)) {
            loggerService.error(txnId, "[username: " + username + "] [ERROR][SERVICE] uploadFile() -* file type sıkıntı *- fileName: " + fileName);
            throw new UnsupportedFileTypeException();
        } else if (file.getSize() > MAX_FILE_SIZE) {
            loggerService.error(txnId, "[username: " + username + "] [ERROR][SERVICE] uploadFile() -* dosya çok büyük *- fileName: " + fileName);
            throw new FileSizeExceededException();
        } else if (fileRepository.existsByFileName(fileName)) {
            loggerService.error(txnId, AuthUtil.getCurrentUsername() + "] [ERROR][SERVICE] uploadFile() -* bu dosya ismi zaten mevcut *- fileName: " + fileName);
            throw new FileNameAlreadyExistsException();
        }

        loggerService.info(txnId, "[username: " + username + "] [SERVICE] uploadFile() - Dosya geçerli DB ve Redis işlemleri başlıyor - fileName: " + fileName);

        // Kullanıcıyı bul
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException();
        }

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(fileName);
        fileEntity.setFileType(fileType);
        fileEntity.setFileData(file.getBytes());

        // Thumbnail oluştur
        byte[] thumbnailData = ImageUtils.generateThumbnail(file.getBytes());
        fileEntity.setThumbnailData(thumbnailData);

        // Kullanıcı ile ilişkilendir
        fileEntity.setUser(optionalUser.get());

        // Veritabanına kaydet
        fileRepository.save(fileEntity);
        loggerService.info(txnId, "[username: " + username + "] [DB] Dosya DB'ye kaydedildi - fileName: " + fileName);

        // **CacheService Kullanarak Redis'e Kaydet**
        cacheService.put("file", fileEntity.getId(), fileEntity, txnId);
        loggerService.info(txnId, "[username: " + username + "] [CACHE] Dosya Redis'e kaydedildi - fileName: " + fileName);

        loggerService.info(txnId, "[username: " + username + "] [SERVICE] uploadFile() tamamlandı - fileName: " + fileName);
        return fileEntity;
    }

    public FileEntity updateFile(String username, MultipartFile file, String txnId) throws IOException {
        loggerService.info(txnId, "[username: " + username + "] [SERVICE] Dosya DB ve rediste güncellenecek - fileName: " + file.getOriginalFilename());
        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [SERVICE] updateFile() başladı - username: " + username);


        FileEntity currentFileEntity = getFileByUsername(username, txnId);

        if (currentFileEntity == null) {
            loggerService.error(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [ERROR][SERVICE] updateFile() -* file bulunamadi *- fileName: " + file.getOriginalFilename());
            throw new FileNotFoundException();
        } else if (file.getSize() > MAX_FILE_SIZE) {
            loggerService.error(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [ERROR][SERVICE] updateFile() -* dosya çok büyük *- fileName:  " + file.getOriginalFilename());
            throw new FileSizeExceededException();
        } else if (!isSupportedFileType(file.getOriginalFilename())) {
            loggerService.error(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [ERROR][SERVICE] updateFile() -* file type sıkıntı *- fileName: " + file.getOriginalFilename());
            throw new UnsupportedFileTypeException();
        }
        loggerService.info(txnId, "[username: " + username + "] [SERVICE] uploadFile() - Dosya geçerli DB ve Redis işlemleri başlıyor - fileName: " + file.getOriginalFilename());

        currentFileEntity.setFileName(file.getOriginalFilename());
        currentFileEntity.setFileType(file.getContentType());
        currentFileEntity.setFileData(file.getBytes());

        // Thumbnail oluştur
        byte[] thumbnailData = ImageUtils.generateThumbnail(currentFileEntity.getFileData());
        currentFileEntity.setThumbnailData(thumbnailData);
        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [SERVICE] updateFile() thumbnailData güncellendi - fileName: " + file.getOriginalFilename());


        fileRepository.save(currentFileEntity);
        loggerService.info(txnId, "[username: " + username + "] [DB] uploadFile() tamamlandı - fileName: " + file.getOriginalFilename());

        cacheService.put("file", currentFileEntity.getId(), currentFileEntity, txnId);
        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [CACHE] file cache'de update edildi - fileName: " + file.getOriginalFilename());

        loggerService.info(txnId, "[username: " + AuthUtil.getCurrentUsername() + "] [SERVICE] updateFile() tamamlandı - fileName: " + file.getOriginalFilename());
        return currentFileEntity;
    }

    private boolean isSupportedFileType(String fileName) {
        String[] allowedExtensions = {"jpg", "jpeg", "png", "tiff", "bmp"};
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return List.of(allowedExtensions).contains(fileExtension.toLowerCase());
    }
}
