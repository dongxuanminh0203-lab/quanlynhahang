# Quản Lý Nhà Hàng - Restaurant Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![Status](https://img.shields.io/badge/Status-Active-brightgreen.svg)

Hệ thống quản lý nhà hàng toàn diện được xây dựng bằng Java

</div>

---

## 📋 Mục Lục

- [Giới Thiệu](#giới-thiệu)
- [Tính Năng](#tính-năng)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cài Đặt](#cài-đặt)
- [Cách Sử Dụng](#cách-sử-dụng)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
- [Đóng Góp](#đóng-góp)
- [Liên Hệ](#liên-hệ)

---

## 🎯 Giới Thiệu

**Quản Lý Nhà Hàng** là một ứng dụng desktop được phát triển bằng Java, cung cấp các công cụ quản lý toàn diện cho các nhà hàng. Ứng dụng hỗ trợ quản lý menu, đơn hàng, khách hàng, nhân viên và doanh thu.

---

## ✨ Tính Năng

### Quản Lý Cơ Bản
- ✅ **Quản Lý Menu**: Thêm, sửa, xóa các món ăn và thức uống
- ✅ **Quản Lý Đơn Hàng**: Tạo, theo dõi và quản lý các đơn hàng
- ✅ **Quản Lý Bàn**: Kiểm soát tình trạng và bố cục các bàn
- ✅ **Quản Lý Khách Hàng**: Lưu thông tin khách hàng và lịch sử mua hàng

### Quản Lý Nhân Sự
- ✅ **Quản Lý Nhân Viên**: Thêm, sửa, xóa thông tin nhân viên
- ✅ **Quản Lý Ca Làm Việc**: Sắp xếp lịch làm việc cho nhân viên
- ✅ **Quản Lý Lương**: Tính toán và quản lý lương nhân viên

### Báo Cáo & Phân Tích
- ✅ **Thống Kê Doanh Thu**: Xem báo cáo doanh thu theo ngày, tháng, năm
- ✅ **Báo Cáo Bán Hàng**: Phân tích sản phẩm bán chạy nhất
- ✅ **Báo Cáo Chi Phí**: Theo dõi chi phí hoạt động

---

## 🖥️ Yêu Cầu Hệ Thống

- **Java**: JDK 8 trở lên
- **RAM**: Tối thiểu 2GB
- **Ổ Cứng**: Tối thiểu 500MB dung lượng trống
- **Hệ Điều Hành**: Windows, macOS, hoặc Linux

---

## 📦 Cài Đặt

### 1. Clone Repository
```bash
git clone https://github.com/dongxuanminh0203-lab/quanlynhahang.git
cd quanlynhahang
```

### 2. Biên Dịch Dự Án
```bash
javac -d bin src/**/*.java
```

### 3. Chạy Ứng Dụng
```bash
java -cp bin Main
```

---

## 🚀 Cách Sử Dụng

### Khởi Động Ứng Dụng
1. Mở terminal/command prompt
2. Điều hướng đến thư mục dự án
3. Chạy lệnh: `java -cp bin Main`

### Các Bước Cơ Bản
1. **Đăng Nhập**: Sử dụng tài khoản quản trị viên
2. **Cấu Hình Ban Đầu**: Thiết lập menu, bàn, nhân viên
3. **Bắt Đầu Hoạt Động**: Tạo đơn hàng và quản lý từng ngày

---

## 📁 Cấu Trúc Dự Án

```
quanlynhahang/
├── src/                          # Mã nguồn chính
│   ├── controllers/              # Các controller xử lý logic
│   ├── models/                   # Các model dữ liệu
│   ├── views/                    # Các giao diện người dùng
│   ├── services/                 # Các service xử lý nghiệp vụ
│   ├── utils/                    # Các tiện ích và hàm hỗ trợ
│   └── Main.java                 # File khởi động chính
├── .vscode/                      # Cấu hình VS Code
├── quanlynhahang/                # Tài nguyên ứng dụng
├── README.md                     # File này
└── pom.xml                       # File cấu hình Maven (nếu dùng)
```

---

## 🛠️ Công Nghệ Sử Dụng

- **Ngôn Ngữ**: Java
- **Giao Diện**: Swing/JavaFX
- **Cơ Sở Dữ Liệu**: MySQL/SQLite (tùy cấu hình)
- **Quản Lý Phụ Thuộc**: Maven (nếu có)

---

## 🤝 Đóng Góp

Chúng tôi chào đón mọi đóng góp! Để đóng góp:

1. Fork repository
2. Tạo branch tính năng (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push đến branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

---

## 📝 Ghi Chú

- Đây là fork từ repository [minhxuan2325-hub/quanlynhahang-1](https://github.com/minhxuan2325-hub/quanlynhahang-1)
- Dự án được phát triển để mục đích học tập và thực hành

---

## 📞 Liên Hệ

- **Tác Giả**: [dongxuanminh0203-lab](https://github.com/dongxuanminh0203-lab)
- **Email**: Liên hệ qua GitHub

---

<div align="center">

⭐ Nếu dự án này hữu ích, hãy cho chúng tôi một star!

Cảm ơn đã sử dụng **Quản Lý Nhà Hàng**!

</div>
