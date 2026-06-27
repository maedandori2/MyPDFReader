# 📋 Lịch sử thay đổi — MyPDFReader

---

## [v1.2] - 2026-06-27

### ✨ Tính năng mới
- **Chuyển đổi ngôn ngữ 🇻🇳/🇯🇵**: Thêm 2 nút cờ Việt Nam và Nhật Bản trên thanh header (cạnh nút Sync). Ấn vào để chuyển đổi toàn bộ giao diện app giữa tiếng Việt và tiếng Nhật. Ngôn ngữ được lưu lại sau khi tắt app.
- **Hệ thống đa ngôn ngữ (LocaleHelper)**: Tạo singleton quản lý 50+ chuỗi dịch Việt-Nhật, hỗ trợ mở rộng thêm ngôn ngữ trong tương lai.
- **Auto-sync khi có thay đổi**: Thay thế hệ thống cài đặt sync theo giờ cố định (1h/2h/4h/8h) bằng cơ chế tự động phát hiện file mới trên Google Drive và tải về. Chỉ cần bật/tắt Switch, không cần cài đặt tần suất.
- **File HISTORY.md**: Tạo file changelog lưu lại toàn bộ lịch sử thay đổi của dự án.

### 🔧 Thay đổi
- **SyncActivity**: Loại bỏ RadioGroup chọn tần suất sync (1h/2h/4h/8h), thay bằng Switch on/off đơn giản với mô tả "Khi có file mới trên Drive sẽ tự động tải về".
- **SyncWorker**: Cập nhật logic polling 15 phút (minimum WorkManager), chỉ tải file mới chưa có trong thư mục local.
- **SyncManager**: Thêm hàm `checkAndSyncNewFiles()` so sánh danh sách file Drive vs local, thêm quản lý trạng thái auto-sync qua SharedPreferences.
- **Tất cả Activity & Adapter**: Thay toàn bộ hardcoded Vietnamese strings bằng `LocaleHelper.getString()` để hỗ trợ đa ngôn ngữ.

### 📁 File mới
| File | Mô tả |
|------|-------|
| `LocaleHelper.kt` | Singleton quản lý đa ngôn ngữ Việt-Nhật |
| `HISTORY.md` | File changelog dự án |

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `activity_main.xml` | Thêm 2 nút cờ 🇻🇳/🇯🇵 vào header |
| `activity_sync.xml` | Xóa RadioGroup interval, thêm id cho TextView, đổi label auto-sync |
| `MainActivity.kt` | Logic chuyển đổi ngôn ngữ + highlight nút cờ + đa ngôn ngữ |
| `PdfViewerActivity.kt` | Đa ngôn ngữ Toast messages |
| `PdfFileAdapter.kt` | Đa ngôn ngữ status text |
| `SyncActivity.kt` | Đa ngôn ngữ + auto-sync WorkManager scheduling |
| `SyncManager.kt` | Thêm `checkAndSyncNewFiles()` + quản lý auto-sync + đa ngôn ngữ |
| `SyncWorker.kt` | Kiểm tra auto-sync enabled + dùng `checkAndSyncNewFiles()` |

---

## [v1.1] - Phiên bản trước
- Tích hợp Google Drive Sync (đăng nhập, tải file PDF từ Drive)
- Reading List manager (thêm/xóa/sắp xếp danh sách đọc)
- PDF Viewer với zoom/pan/swipe navigation
- Tự động sync theo lịch cố định (1h/2h/4h/8h) qua WorkManager
- Tìm kiếm file PDF theo tên
- Giữ màn hình luôn sáng khi đọc PDF

---

## [v1.0] - Phiên bản đầu tiên
- Đọc file PDF từ thư mục `/sdcard/MyPDF`
- Danh sách file PDF cục bộ
- Xem PDF toàn màn hình
