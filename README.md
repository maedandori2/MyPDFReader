# My PDF Reader - Kindle Fire 10

Ứng dụng đọc PDF cho Kindle Fire 10, tối ưu cho 2.000+ file PDF.

## Tính năng
- 📂 Scan tự động thư mục `/sdcard/MyPDF/`
- 🔍 Tìm kiếm realtime theo mã số 9 chữ số
- 📋 Reading List: giữ file → Add to list
- ✅ Đánh dấu đã đọc / chưa đọc
- ▶ Nút "Đọc file tiếp theo" (tự mở file chưa đọc đầu tiên)
- 🔢 Sắp xếp thứ tự trong Reading List (nút ▲▼)
- 📖 Đọc PDF với zoom pinch, chuyển trang

---

## Cách build APK trên GitHub (không cần cài gì)

### Bước 1: Tạo tài khoản GitHub
→ https://github.com/signup

### Bước 2: Tạo repository mới
1. Đăng nhập GitHub
2. Nhấn nút **New** (màu xanh)
3. Repository name: `MyPDFReader`
4. Chọn **Public**
5. Nhấn **Create repository**

### Bước 3: Upload toàn bộ code
1. Trong trang repository vừa tạo
2. Nhấn **uploading an existing file**
3. Kéo thả toàn bộ thư mục `MyPDFReader` vào
4. Nhấn **Commit changes**

### Bước 4: Chạy GitHub Actions
1. Vào tab **Actions** trong repository
2. Nhấn **Build APK** → **Run workflow** → **Run workflow**
3. Chờ khoảng 5-10 phút

### Bước 5: Tải APK
1. Sau khi build xong, nhấn vào build vừa chạy
2. Kéo xuống phần **Artifacts**
3. Tải file `MyPDFReader-debug`
4. Giải nén → có file `app-debug.apk`

### Bước 6: Cài lên Kindle Fire
1. Copy `app-debug.apk` vào Kindle Fire (qua USB hoặc email)
2. Trên Kindle: Settings → Security → **Unknown Sources** → Bật ON
3. Mở file manager → tìm file APK → cài đặt
4. Mở app → cấp quyền bộ nhớ khi được hỏi

### Bước 7: Copy file PDF
1. Kết nối Kindle với máy tính qua USB
2. Copy 2.000 file PDF vào thư mục `/sdcard/MyPDF/`
3. Mở app → file tự động hiện ra

---

## Cách dùng Reading List
- **Thêm vào list**: Giữ tay (long press) lên tên file → thông báo "Đã thêm"
- **Xem danh sách**: Nhấn tab **Danh sách đọc**
- **Đọc theo thứ tự**: Nhấn nút **▶ Đọc file tiếp theo** ở dưới
- **Sắp xếp lại**: Dùng nút ▲ ▼ bên phải mỗi file
- **Xóa khỏi list**: Giữ tay lên file trong tab Danh sách đọc

---

## Lưu ý Kindle Fire
- Cần bật **Unknown Sources** trước khi cài APK
- Thư mục PDF: `/sdcard/MyPDF/` (app tự tạo lần đầu)
- Dữ liệu Reading List được lưu trong app, không mất khi tắt máy
