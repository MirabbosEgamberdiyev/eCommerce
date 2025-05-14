# ? Microservices Architecture – Spring Boot + Spring Cloud

Ushbu loyiha mikroxizmatlar arxitekturasi asosida yaratilgan bo‘lib, har bir xizmat alohida modulda ishlaydi va ular o‘zaro **Eureka Service Discovery**, **API Gateway**, va boshqa servislar orqali bog‘langan.

## ? Loyihadagi xizmatlar:

### 1. ? eureka-server — Xizmatlarni ro‘yxatdan o‘tkazish (Service Discovery)
- Xizmat: Spring Cloud Netflix Eureka Server
- Maqsadi: Barcha xizmatlarni ro‘yxatdan o‘tkazish va topishga yordam beradi.
- Port: `8761`
- Kirish: `http://localhost:8761`

### 2. ? api-gateway — Kirish nuqtasi
- Xizmat: Spring Cloud Gateway
- Maqsadi: Tashqi so‘rovlarni ichki mikroxizmatlarga yo‘naltirish.
- Port: `8080`
- Afzallik: Load balancing, filtering, routing, va security.

### 3. ? auth-service — Foydalanuvchilarni boshqarish
- Xizmat: JWT asosidagi autentifikatsiya va avtorizatsiya.
- Maqsadi: Login, ro‘yxatdan o‘tish, tokenlar bilan ishlash.
- Port: `8081`

### 4. ?? product-service — Mahsulotlar
- Xizmat: Mahsulotlar bilan ishlovchi REST API.
- Funksiyalar: Qo‘shish, o‘chirish, yangilash, olish.
- Port: `8082`

### 5. ? inventory-service — Mahsulot zaxirasi
- Xizmat: Ombordagi mavjud mahsulotlar haqida ma’lumot.
- Port: `8083`

### 6. ? order-service — Buyurtmalar
- Xizmat: Buyurtmalarni boshqarish.
- Funksiyalar: Buyurtma yaratish, ko‘rish, bekor qilish.
- Port: `8084`

### 7. ? payment-service — To‘lovlar
- Xizmat: To‘lov tizimi bilan integratsiya.
- Maqsadi: To‘lovni qabul qilish, tekshirish.
- Port: `8085`

### 8. ? notification-service — Email yoki SMS
- Xizmat: Foydalanuvchilarga email yoki sms yuborish.
- Texnologiyalar: MailSender / Twilio (tanlovga qarab).
- Port: `8086`

### 9. ?? config-server (ixtiyoriy) — Konfiguratsiya markazi
- Xizmat: Barcha mikroxizmatlar uchun markazlashtirilgan konfiguratsiya.
- Maqsadi: application.yml fayllarni GitHub yoki lokal papkadan o‘qish.
- Port: `8888`

---

## ? Texnologiyalar

- `Java 21`
- `Spring Boot 3.x`
- `Spring Cloud 2022.x`
- `Eureka Server`
- `Spring Cloud Gateway`
- `Spring Security (JWT)`
- `OpenFeign`
- `Spring Data JPA / MongoDB`
- `Spring Mail / Twilio`
- `Lombok`
- `MapStruct` (optional)
- `Docker` (optional)
- `Actuator` (monitoring uchun)

---

## ?? Ishga tushirish ketma-ketligi

1. `eureka-server` ni ishga tushiring:
   ```bash
   cd eureka-server
   mvn spring-boot:run



?? Umumiy Arxitektura
Xizmatlar:
API Gateway – Tashqi so‘rovlar uchun bitta kirish nuqtasi

Auth-Service – Foydalanuvchi ro‘yxatdan o‘tishi, kirishi va JWT asosida autentifikatsiya

Product-Service – Mahsulotlar ro‘yxati, qidiruv, qo‘shish va yangilash

Order-Service – Buyurtma yaratish, holatini kuzatish

Payment-Service – To‘lovlar va tranzaksiyalarni boshqarish

Notification-Service – Email yoki SMS yuborish (masalan RabbitMQ orqali)

Inventory-Service (optional) – Ombordagi mahsulot sonini boshqarish

Shipping-Service (optional) – Yetkazib berish jarayonini boshqarish

?? Foydalaniladigan Texnologiyalar
Bo‘lim	Texnologiya	Maqsadi
Backend Framework	Spring Boot	Mikroxizmatlar yaratish
API Security	Spring Security + JWT	Avtorizatsiya va autentifikatsiya
API dizayn	RESTful API + OpenAPI (Swagger)	API yaratish va hujjatlashtirish
Xizmatlararo aloqa	RabbitMQ	Asinxron aloqa, Event-driven arxitektura
Monitoring	Grafana + Prometheus	Monitoring va tizimni kuzatish
Deployment	Docker + Docker Compose	Konteynerlash va xizmatlarni boshqarish
Xizmat kashfoti	Eureka Discovery Server	Service registry va auto-discovery
API kirish nuqtasi	Spring Cloud Gateway	Central API Gateway
Tranzaksiyalar	Saga Pattern	Tarqatilgan tranzaksiyalarni boshqarish
Ma'lumotlar bazasi	PostgreSQL (Order, User, Payment)
MongoDB (Product)	RDBMS va NoSQL integratsiya
CI/CD (optional)	Jenkins / GitHub Actions	Deploy va testlarni avtomatlashtirish

? Auth-Service
Vazifalari:

Foydalanuvchi ro‘yxatdan o‘tishi va kirishi

JWT token yaratish va tekshirish

Rollar: ADMIN, USER

Texnologiyalar:

Spring Boot + Spring Security

JWT

PostgreSQL (foydalanuvchilar bazasi)

BCrypt (parolni shifrlash)

? Product-Service
Vazifalari:

Mahsulot qo‘shish, tahrirlash, o‘chirish

Mahsulotlarni izlash va ko‘rsatish

Texnologiyalar:

Spring Boot + REST API

MongoDB (NoSQL mahsulotlar uchun yaxshi)

OpenAPI/Swagger

? Order-Service
Vazifalari:

Buyurtma yaratish

Buyurtma holatini kuzatish

Saga Pattern orqali: buyurtma ? to‘lov ? inventory tekshiruvi

Texnologiyalar:

Spring Boot

PostgreSQL

RabbitMQ (to‘lov va inventoryga event yuborish)

? Payment-Service
Vazifalari:

To‘lovni qabul qilish (bank APIs)

Tranzaksiyani tekshirish

Order-Service dan kelgan event asosida ishlash

Texnologiyalar:

Spring Boot

PostgreSQL

RabbitMQ (Buyurtma to‘lovdan keyin holatini yuborish uchun)

? Notification-Service
Vazifalari:

Email/SMS yuborish

Asinxron: RabbitMQ orqali xabarni olish

Texnologiyalar:

Spring Boot

RabbitMQ

? Inventory-Service (ixtiyoriy)
Vazifalari:

Mahsulotlar sonini kamaytirish yoki oshirish

Order-Service tomonidan so‘rov qabul qilish

Texnologiyalar:

Spring Boot

PostgreSQL yoki MongoDB

? API Gateway (Spring Cloud Gateway)
Vazifalari:

Foydalanuvchi so‘rovlarini kerakli servislarga uzatish

JWT ni tekshirish

Rate Limiting, Logging, va xavfsizlik filtrlari

Texnologiyalar:

Spring Cloud Gateway

Spring Security

? Eureka Server (Service Discovery)
Vazifalari:

Xizmatlar bir-birini topadi

Har bir xizmat o‘zini ro‘yxatdan o‘tkazadi

Texnologiyalar:

Spring Cloud Netflix Eureka

? Monitoring va Log
Grafana + Prometheus

Har bir servisda Micrometer kutubxonasi orqali metrikalarni yig‘ish

Grafana orqali vizual monitoring

Logstash/ELK Stack (Qo‘shimcha)

Har bir servisdan loglarni to‘plash va ko‘rish

? Saga Pattern (Tarqatilgan Tranzaksiya)
Scenario: Buyurtma yaratish

Order-Service buyurtma yaratadi va pending statusida saqlaydi

Event yuboriladi: order.created

Payment-Service to‘lovni amalga oshiradi

Inventory-Service mahsulot miqdorini kamaytiradi

Har bir bosqich muvaffaqiyatli bo‘lsa, Order-Service holatini confirmed qiladi

Xatolik bo‘lsa, kompensatsiya eventlari yuboriladi (payment.cancel, inventory.restore)



? 1. Loyihani Rejalashtirish
Xizmatlar ro‘yxati:

eureka-server – xizmatlarni ro‘yxatdan o‘tkazish

api-gateway – kirish nuqtasi

auth-service – foydalanuvchilarni boshqarish

product-service – mahsulotlar

order-service – buyurtmalar

payment-service – to‘lovlar

notification-service – email yoki sms

inventory-service – mahsulot zaxirasi

config-server (optional) – konfiguratsiya markazi

?? 2. Texnologiyalarni Tanlash
Maqsad	Texnologiya
Framework	Spring Boot
Xizmatlararo aloqa	REST + RabbitMQ
Autentifikatsiya	JWT + Spring Security
Xizmat kashfoti	Eureka Server
API Gateway	Spring Cloud Gateway
Ma'lumotlar bazasi	PostgreSQL (user/order/payment) + MongoDB (product)
Konteynerlash	Docker + Docker Compose
Monitoring	Prometheus + Grafana
Dokumentatsiya	Swagger (springdoc-openapi)