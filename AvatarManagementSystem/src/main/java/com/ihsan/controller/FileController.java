package com.ihsan.controller;

import com.ihsan.dto.FilePhotoDTO;
import com.ihsan.entity.FileEntity;
import com.ihsan.entity.FileProcessEntity;
import com.ihsan.exception.TxnIdNotFoundException;
import com.ihsan.service.FileService;
import com.ihsan.service.LoggerService;
import com.ihsan.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private LoggerService loggerService;

    @GetMapping("/getFileByUsername")
    public ResponseEntity<FilePhotoDTO> getFileByUsername(@RequestHeader(value = "TxnId") String txnId) {
        if (txnId == null || txnId.isEmpty()) {
            throw new TxnIdNotFoundException();
        }
        String username = AuthUtil.getCurrentUsername();

        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] getFileByUsername() başladı");
        FilePhotoDTO DTOfile = new FilePhotoDTO();

        FileEntity findedFileEntity = fileService.getFileByUsername(username, txnId);

        DTOfile.setFileData(findedFileEntity.getFileData());
        DTOfile.setThumbnailData(findedFileEntity.getThumbnailData());

        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] getFileByUsername() tamamlandı");

        return ResponseEntity.ok(DTOfile);
    }

    @GetMapping("/getFile")
    public ResponseEntity<FileEntity> getFile(@RequestHeader(value = "TxnId") String txnId) {
        if (txnId == null || txnId.isEmpty()) {
            throw new TxnIdNotFoundException();
        }
        String username = AuthUtil.getCurrentUsername();
        FileEntity fileEntity = fileService.getFileByUsername(username, txnId);
        Long fileId = fileEntity.getId();

        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] getFile() başladı - fileId: " + fileId);
        FileEntity file = fileService.getFile(fileId, txnId);
        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] getFile() tamamlandı - fileId: " + fileId);

        return ResponseEntity.ok(file);
    }

    @GetMapping("/getAllFiles")
    public ResponseEntity<List<FileEntity>> getAllFiles(@RequestHeader(value = "TxnId") String txnId) {
        if (txnId == null || txnId.isEmpty()) {
            throw new TxnIdNotFoundException();
        }
        String username = AuthUtil.getCurrentUsername();
        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] getAllFiles() çağrıldı.");
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    @DeleteMapping("/deleteFile")
    public ResponseEntity<String> deleteFile(@RequestHeader(value = "TxnId") String txnId) {
        if (txnId == null || txnId.isEmpty()) {
            throw new TxnIdNotFoundException();
        }
        String username = AuthUtil.getCurrentUsername();
        FileEntity currentFileEntity = fileService.getFileByUsername(username, txnId);
        Long fileId = currentFileEntity.getId();

        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] deleteFile() başladı - fileId: " + fileId);
        fileService.deleteFile(fileId, txnId);
        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] deleteFile() tamamlandı - fileId: " + fileId);
        return ResponseEntity.ok("FileID: " + fileId + " deleted successfully.");
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<FileProcessEntity> uploadFile(@RequestParam("file") MultipartFile file,
                                                        @RequestHeader(value = "TxnId") String txnId) throws IOException {
        if (txnId == null || txnId.isEmpty()) {
            throw new TxnIdNotFoundException();
        }

        String username = AuthUtil.getCurrentUsername();


        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] uploadFile() başladı - fileName: " + file.getOriginalFilename());

        FileEntity fileEntity = fileService.uploadFile(file, username, txnId);

        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] uploadFile() tamamlandı - fileName: " + file.getOriginalFilename());

        FileProcessEntity processEntity = new FileProcessEntity();
        processEntity.setFile(fileEntity);
        processEntity.setProcess("File upload işlemi başarılı");

        return ResponseEntity.ok(processEntity);
    }

    @PutMapping("/updateFile")
    public ResponseEntity<FileProcessEntity> updateFile(@RequestParam("file") MultipartFile file,
                                             @RequestHeader(value = "TxnId") String txnId) throws IOException {
        if (txnId == null || txnId.isEmpty()) {
            throw new TxnIdNotFoundException();
        }
        String username = AuthUtil.getCurrentUsername();

        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] updateFile() başladı - username: " + username);
        FileEntity fileEntity = fileService.updateFile(username, file, txnId);
        loggerService.info(txnId, "[username: " + username + "] [CONTROLLER] updateFile() tamamlandı - username: " + username);

        FileProcessEntity processEntity = new FileProcessEntity();
        processEntity.setFile(fileEntity);
        processEntity.setProcess("File update işlemi başarılı. Kullanıcının yeni file bilgileri..");

        return ResponseEntity.ok(processEntity);
    }
}
