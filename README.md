#  Microservices Architecture – Spring Boot + Spring Cloud

Ushbu loyiha mikroxizmatlar arxitekturasi asosida yaratilgan bo‘lib, har bir xizmat alohida modulda ishlaydi va ular o‘zaro **Eureka Service Discovery**, **API Gateway**, va boshqa servislar orqali bog‘langan.

##  Loyihadagi xizmatlar:

### 1.  eureka-server — Xizmatlarni ro‘yxatdan o‘tkazish (Service Discovery)
- Xizmat: Spring Cloud Netflix Eureka Server
- Maqsadi: Barcha xizmatlarni ro‘yxatdan o‘tkazish va topishga yordam beradi.
- Port: `8761`
- Kirish: `http://localhost:8761`

### 2.  api-gateway — Kirish nuqtasi
- Xizmat: Spring Cloud Gateway
- Maqsadi: Tashqi so‘rovlarni ichki mikroxizmatlarga yo‘naltirish.
- Port: `8080`
- Afzallik: Load balancing, filtering, routing, va security.

### 3.  auth-service — Foydalanuvchilarni boshqarish
- Xizmat: JWT asosidagi autentifikatsiya va avtorizatsiya.
- Maqsadi: Login, ro‘yxatdan o‘tish, tokenlar bilan ishlash.
- Port: `8081`

### 4.  product-service — Mahsulotlar
- Xizmat: Mahsulotlar bilan ishlovchi REST API.
- Funksiyalar: Qo‘shish, o‘chirish, yangilash, olish.
- Port: `8082`

### 5.  inventory-service — Mahsulot zaxirasi
- Xizmat: Ombordagi mavjud mahsulotlar haqida ma’lumot.
- Port: `8083`

### 6.  order-service — Buyurtmalar
- Xizmat: Buyurtmalarni boshqarish.
- Funksiyalar: Buyurtma yaratish, ko‘rish, bekor qilish.
- Port: `8084`

### 7.  payment-service — To‘lovlar
- Xizmat: To‘lov tizimi bilan integratsiya.
- Maqsadi: To‘lovni qabul qilish, tekshirish.
- Port: `8085`

### 8.  notification-service — Email yoki SMS
- Xizmat: Foydalanuvchilarga email yoki sms yuborish.
- Texnologiyalar: MailSender / Twilio (tanlovga qarab).
- Port: `8086`

### 9.  config-server (ixtiyoriy) — Konfiguratsiya markazi
- Xizmat: Barcha mikroxizmatlar uchun markazlashtirilgan konfiguratsiya.
- Maqsadi: application.yml fayllarni GitHub yoki lokal papkadan o‘qish.
- Port: `8888`

---

##  Texnologiyalar

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

##  Ishga tushirish ketma-ketligi

1. `eureka-server` ni ishga tushiring:
   ```bash
   cd eureka-server
   mvn spring-boot:run

Umumiy Arxitektura
Xizmatlar:
API Gateway – Tashqi so‘rovlar uchun bitta kirish nuqtasi

Auth-Service – Foydalanuvchi ro‘yxatdan o‘tishi, kirishi va JWT asosida autentifikatsiya

Product-Service – Mahsulotlar ro‘yxati, qidiruv, qo‘shish va yangilash

Order-Service – Buyurtma yaratish, holatini kuzatish

Payment-Service – To‘lovlar va tranzaksiyalarni boshqarish

Notification-Service – Email yoki SMS yuborish (masalan RabbitMQ orqali)

Inventory-Service (optional) – Ombordagi mahsulot sonini boshqarish

Shipping-Service (optional) – Yetkazib berish jarayonini boshqarish
