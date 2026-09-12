# Phần mềm quản lý nhà hàng

## Giới thiệu

**Quản lý nhà hàng** là ứng dụng desktop được xây dựng bằng Java nhằm hỗ trợ nhân viên quản lý các hoạt động cơ bản trong nhà hàng trên một giao diện tập trung. Phần mềm sử dụng Java Swing cho giao diện, MySQL để lưu trữ dữ liệu và FlatLaf để tạo giao diện hiện đại, dễ sử dụng.

## Chức năng chính

- Đăng nhập và phân quyền sử dụng hệ thống.
- Quản lý bàn ăn và trạng thái bàn.
- Quản lý danh sách món ăn, giá bán và thông tin sản phẩm.
- Quản lý nhân viên.
- Tạo và quản lý đơn hàng.
- Lập hóa đơn và theo dõi thanh toán.
- Hiển thị thông tin tổng quan, thống kê hoạt động nhà hàng.
- Kiểm tra dữ liệu đầu vào trước khi thêm hoặc cập nhật thông tin.

## Công nghệ sử dụng

- **Java 25**
- **Java Swing**: xây dựng giao diện desktop.
- **FlatLaf 3.6.1**: giao diện cho ứng dụng Swing.
- **MySQL**: cơ sở dữ liệu.
- **JDBC Driver 9.4.0**: kết nối Java với MySQL.
- **Maven**: quản lý thư viện và biên dịch dự án.

## Kiến trúc dự án

Dự án được tổ chức theo các lớp chức năng:

- `view`: các màn hình giao diện như đăng nhập, trang chủ, quản lý bàn, món ăn, đơn hàng và thanh toán.
- `controller`: tiếp nhận thao tác từ giao diện và điều phối xử lý.
- `service`: chứa các nghiệp vụ của hệ thống.
- `dao`: thực hiện truy vấn và thao tác với cơ sở dữ liệu.
- `model`: các lớp mô hình dữ liệu.
- `config`: cấu hình kết nối MySQL.
- `util`: các tiện ích dùng chung và kiểm tra dữ liệu.

## Yêu cầu môi trường

- JDK 25 hoặc phiên bản tương thích với cấu hình Maven của dự án.
- Apache Maven 3.9 trở lên.
- MySQL Server đang chạy cục bộ.
- Cơ sở dữ liệu có tên `quanlynhahang`.

## Cấu hình cơ sở dữ liệu

Thông tin kết nối hiện tại được khai báo trong `quanlynhahang/src/main/java/com/nhahang/config/DBHelper.java`:

```text
Host: localhost
Port: 3306
Database: quanlynhahang
Username: root
Password: 123456
```

Hãy tạo cơ sở dữ liệu và điều chỉnh thông tin đăng nhập trong `DBHelper.java` cho phù hợp với máy local trước khi chạy ứng dụng. Dự án hiện chưa kèm file script khởi tạo các bảng, vì vậy cần chuẩn bị schema MySQL tương ứng với các DAO trong thư mục `src/main/java/com/nhahang/dao`.

## Cài đặt và chạy

Mở terminal tại thư mục dự án `quanlynhahang`, sau đó chạy:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.nhahang.Main"
```

Nếu Maven chưa có cấu hình plugin `exec`, có thể chạy lớp `com.nhahang.Main` trực tiếp từ IDE sau khi Maven tải xong các dependency.

## Cấu trúc thư mục

```text
quanlynhahang/
├── pom.xml
└── src/
	└── main/
		├── java/com/nhahang/
		│   ├── config/
		│   ├── controller/
		│   ├── dao/
		│   ├── model/
		│   ├── service/
		│   ├── util/
		│   ├── view/
		│   └── Main.java
		└── resources/images/
```

## Mục tiêu đề tài

Đề tài hướng đến việc xây dựng một hệ thống quản lý nhà hàng đơn giản, trực quan và có thể mở rộng, giúp giảm thao tác thủ công trong việc quản lý bàn, món ăn, đơn hàng, hóa đơn và thanh toán.