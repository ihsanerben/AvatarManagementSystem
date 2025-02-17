- - - Avatar Management System - - -

                  1. Proje Tanımı ve Amacı

Avatar Management System, kullanıcıların avatarlarını yönetebileceği bir Spring Boot tabanlı web uygulamasıdır. Bu sistemde kullanıcılar:

Kayıt (Register) olabilir,
Giriş (Login) yapabilir,
Avatarlarını yükleyebilir, güncelleyebilir ve silebilir,
Verilerini güvenli bir şekilde saklayabilir ve hızlı bir erişim sağlayabilir.
Bu projede performans optimizasyonu ve güvenlik en üst düzeyde tutulmuş, özellikle Redis Cache mekanizması ile veritabanı erişim maliyetleri minimize edilmiştir.

                  2. Kullanılan Teknolojiler ve Araçlar

Bu projede modern backend geliştirme prensipleri takip edilerek, performanslı ve ölçeklenebilir bir sistem oluşturulmuştur. Kullanılan başlıca teknolojiler ve araçlar şunlardır:

Backend
Spring Boot: Framework olarak kullanıldı, bağımlılık yönetimi Maven ile yapıldı.
Spring Data JPA: Veritabanı işlemlerini yönetmek için kullanıldı.
Spring Security (Basic Auth): Kullanıcı kimlik doğrulaması için kullanıldı.
Redis (Spring Data Redis): Kullanıcı ve dosya verilerinin cache’lenmesi için kullanıldı.
PostgreSQL: Ana veritabanı olarak kullanıldı.
Hibernate ORM: JPA ile birlikte veritabanı işlemleri için kullanıldı.
Geliştirme ve Test Araçları
DBeaver: PostgreSQL veritabanını yönetmek için kullanıldı.
Postman: API endpointlerini test etmek için kullanıldı.
Docker: Redis’in container içinde çalıştırılması için kullanıldı.
JUnit & Spring Boot Test: Birim testler ve sistem testleri için kullanıldı.
Log4J & LoggerService: Kullanıcı işlemlerini detaylı bir şekilde loglamak için kullanıldı.
      
                  3. Uygulamanın Mimarisi ve Çalışma Prensibi

    3.1 Kullanıcı İş Akışı
Kullanıcı Kayıt Olur → Kullanıcı, username ve password bilgileri ile sisteme kayıt olur. Bu işlem hem PostgreSQL veritabanına hem de Redis Cache'e yazılır.
Kullanıcı Giriş Yapar → Kullanıcı giriş yaptığında önce Redis Cache'de kontrol edilir. Eğer kullanıcı cache’de varsa doğrudan giriş yapılır, yoksa veritabanından çekilir ve tekrar cache’e yazılır.
Kullanıcı CRUD İşlemleri Yapar → Kullanıcı dosya yükleyebilir, silebilir ve güncelleyebilir. Her işlemde:
Önce Redis Cache kontrol edilir.
Eğer veri cache’de varsa doğrudan cache’den çekilir.
Eğer yoksa, veritabanından çekilir ve cache’e yazılır.
Sistem Log Tutma Mekanizması → Kullanıcının yaptığı her işlem Log4J tabanlı LoggerService ile loglanır.
3.2 Redis Kullanım Stratejisi
Redis’in projeye eklenmesiyle birlikte veritabanı sorgularının yükü hafifletildi ve daha hızlı bir veri erişim mekanizması sağlandı. Kullanıcı ve dosya verileri Redis Cache’e yazılır ve aşağıdaki prensiplere göre yönetilir:

  a) Kullanıcı Cache Mekanizması

Kullanıcı register olduğunda Redis’e eklenir.
Kullanıcı login olduğunda önce Redis’e bakılır, eğer yoksa veritabanına gidilir ve ardından Redis’e eklenir.
Kullanıcı logout olduğunda Redis’ten temizlenir.
Avantajı: Kimlik doğrulama işlemleri hızlanır, veritabanı yükü azalır.
  
  b) Dosya (Avatar) Cache Mekanizması

Kullanıcı bir avatar yüklediğinde, hem veritabanına hem de Redis’e kaydedilir.
Kullanıcı bir avatar getirmek istediğinde, öncelikle Redis kontrol edilir.
Kullanıcı bir avatar sildiğinde, Redis’ten de temizlenir.
Avantajı: Kullanıcı avatarları daha hızlı erişilir, gereksiz veritabanı sorguları engellenir.

      4. Optimizasyonlar

Bu sistemin hızlı, güvenli ve ölçeklenebilir olması için aşağıdaki optimizasyonlar yapıldı:

    4.1 Performans İyileştirmeleri
✔ Redis Cache Kullanımı: Veritabanına gereksiz sorgular gitmeden, veriler önce Redis’ten çekiliyor.
✔ Lazy Loading ve Eager Loading Kullanımı: Hibernate ORM’in @OneToMany(fetch = FetchType.LAZY) gibi optimizasyonları kullanıldı.
✔ Asenkron İşlemler: Büyük dosya işlemleri için @Async anotasyonu kullanılarak ana thread’in bloklanması engellendi.

    4.2 Güvenlik Önlemleri
✔ Spring Security (Basic Auth): Yetkilendirme mekanizması sağlandı.
✔ AES-CBC + Base64 ile Şifreleme: Kullanıcı şifreleri AES-CBC ile şifrelenerek PostgreSQL'e kaydedildi.
✔ Global Exception Handling: Bütün hata senaryoları merkezi bir Global Exception Handler üzerinden yönetildi.
✔ Rol Tabanlı Yetkilendirme (Role-Based Authorization): Kullanıcıların belirli işlemleri yapabilmesi için rol kontrolleri getirildi.

      5. Kullanıcı Senaryoları

Aşağıda temel kullanıcı senaryoları açıklanmıştır:

Senaryo 1: Kullanıcı Kayıt Oluyor
POST /api/auth/register endpoint’ine username ve password ile istek atılır.
Kullanıcı verisi PostgreSQL’e ve Redis’e kaydedilir.
Başarılı kayıt işlemi sonucunda JSON formatında kullanıcı bilgileri döndürülür.
Senaryo 2: Kullanıcı Login Oluyor
POST /api/auth/login endpoint’ine kullanıcı adı ve şifre gönderilir.
Redis içinde kullanıcı varsa doğrudan giriş yapılır.
Eğer Redis’te yoksa, PostgreSQL’den çekilir ve Redis’e eklenir.
Senaryo 3: Kullanıcı Avatar Yüklüyor
POST /api/files/uploadFile endpoint’ine Multipart File gönderilir.
Dosya PostgreSQL’e kaydedilir ve Redis Cache’e eklenir.
Senaryo 4: Kullanıcı Avatarını Çekiyor
GET /api/files/getFile endpoint’i çağrılır.
Önce Redis kontrol edilir. Eğer cache’de varsa, doğrudan cache’den döndürülür.
Cache’de yoksa, veritabanından çekilip tekrar Redis’e eklenir.
Senaryo 5: Kullanıcı Avatarını Güncelliyor
PUT /api/files/updateFile endpoint’ine yeni dosya yüklenir.
Dosya hem PostgreSQL’de güncellenir hem de Redis içinde güncellenir.
Senaryo 6: Kullanıcı Avatarını Silme
DELETE /api/files/deleteFile endpoint’i çağrılır.
Dosya PostgreSQL’den silinir ve aynı zamanda Redis’ten de temizlenir.

      6. Sonuç ve Kazanımlar

Bu proje, performans, güvenlik ve ölçeklenebilirlik açısından optimize edilmiş bir sistem sunmaktadır. Spring Boot, Redis ve PostgreSQL’in etkin kullanımıyla, aşağıdaki kazanımlar elde edilmiştir:

✅ Daha hızlı kimlik doğrulama (Redis Cache sayesinde)
✅ Veritabanı yükü azaltıldı (Özellikle sık erişilen veriler için)
✅ Daha güvenli bir sistem (AES şifreleme + Spring Security)
✅ Modüler ve ölçeklenebilir bir mimari (Repository-Service-Controller katmanlı yapı)

Bu proje, Spring Boot ile modern bir backend geliştirme sürecini öğrenmek ve uygulamak için mükemmel bir deneyim sağlamıştır. 🚀
