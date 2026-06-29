# 📋 Lịch sử thay đổi — MyPDFReader

---

## [v1.4.2] - 2026-06-30

### ✨ Cải tiến giao diện Danh sách đọc
- **Tên file lớn hơn**: Tăng kích thước chữ tên file trong danh sách đọc từ 15sp lên 19sp để dễ đọc hơn.
- **Số thứ tự có thể sửa trực tiếp**: Thay đổi ô số thứ tự (1, 2, 3...) từ `TextView` sang `EditText`, cho phép người dùng bấm vào và nhập số mới.
- **Hoán đổi vị trí tự động (swap)**: Khi người dùng sửa số thứ tự của một item (ví dụ: sửa item 1 thành 3), item đó sẽ được chuyển đến vị trí 3, và các item khác tự động dịch chuyển theo.

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `item_pdf_file.xml` | Đổi `tvIndex` từ `TextView` → `EditText` với `inputType="number"`, `imeOptions="actionDone"`, `selectAllOnFocus` |
| `PdfFileAdapter.kt` | Thêm import `EditText`/`EditorInfo`, đổi type `tvIndex`, thêm logic `setupIndexEditor()` và `handleIndexChange()`, tăng text size tên file, thêm callback `onSwapPosition` |
| `MainActivity.kt` | Thêm callback `onSwapPosition` khi tạo adapter, thêm hàm `swapItems()` gọi `ReadingListManager.moveToPosition()` |
| `HISTORY.md` | Cập nhật changelog lên `v1.4.2` |

---

## [v1.4.1] - 2026-06-27

### ⏪ Hoàn tác (Rollback)
- **Hủy bỏ Đa danh sách đọc**: Đưa ứng dụng về cấu trúc sử dụng duy nhất một danh sách đọc toàn cục để tránh sự phức tạp và nhầm lẫn cho người dùng.
- **Tự động chuyển đổi dữ liệu**: Cập nhật Database version 3, tự động gộp tất cả file ở mọi danh sách về lại một danh sách duy nhất.
- **Khôi phục UI**: Gỡ bỏ Spinner chọn danh sách và hộp thoại (Dialog) khi thêm PDF.

---

## [v1.4.0] - 2026-06-27

### ✨ Tính năng mới: Đa danh sách đọc
- **Tạo và phân loại danh sách đọc tự do**: Người dùng có thể tạo nhiều danh sách đọc với tên gọi riêng (vd: Công việc, Giải trí, Tài liệu học tập...). 
- **Tương tác thông minh qua nút `+`**: Khi bấm thêm một PDF vào danh sách đọc, hệ thống sẽ mở một hộp thoại cho phép bạn chọn danh sách muốn thêm vào, hoặc tạo ngay một danh sách mới. Nếu bạn bỏ qua hoặc không chọn tên, hệ thống tự động đưa file vào danh sách đọc mặc định mang tên **"Chung"**.
- **Chuyển đổi linh hoạt (Dropdown/Spinner)**: Trong tab "Danh sách đọc", một menu thả xuống (Spinner) được bổ sung ở trên cùng giúp bạn dễ dàng chuyển qua lại giữa các danh sách đọc cá nhân hóa của mình.
- **Tương thích Room Database**: Nâng cấp schema Room DB với tính năng `Migration` (khóa chính ghép từ đường dẫn file và tên danh sách). Toàn bộ dữ liệu danh sách đọc trước đây sẽ tự động được chuyển sang danh sách "Chung" mà không bị mất mát.

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `PdfEntity` & `PdfDao` | Thay đổi khóa chính thành `(path, listName)` để 1 file có thể nằm ở nhiều danh sách khác nhau |
| `AppDatabase` | Viết mã tự động di chuyển dữ liệu (Migration) từ DB cũ sang cấu trúc đa danh sách |
| `ReadingListManager.kt` | Nâng cấp lõi lưu trữ và quản lý tập tin theo tên danh sách đọc |
| `activity_main.xml` | Gắn `Spinner` (menu chọn danh sách) vào ngay trên thẻ hiển thị Danh sách đọc |
| `MainActivity.kt` | Lập trình hộp thoại Dialog khi bấm nút `+`, xử lý sự kiện chuyển list trong `Spinner` |
| `HISTORY.md` | Cập nhật changelog lên `v1.4.0` |

---

## [v1.3.1] - 2026-06-27

### 🎨 Cập nhật Giao diện (UI)
- **Đổi tên ứng dụng**: Đổi tiêu đề ứng dụng trên màn hình chính từ "My PDF Reader" thành "仕様書".
- **Tối ưu hiển thị cho Tablet**:
  - Mở rộng vùng cảm ứng của các nút chuyển đổi ngôn ngữ (🇻🇳, 🇯🇵) và nút "Sync" lấp đầy chiều cao header (56dp).
  - Tăng kích thước emoji cờ (lên 28sp) và nới rộng khoảng cách giữa các cờ (16dp), cũng như đẩy khoảng cách cụm cờ và nút Sync (32dp) để chống bấm nhầm trên màn hình cảm ứng lớn.

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `activity_main.xml` | Đổi text header, chỉnh `layout_height="match_parent"` và cập nhật `marginEnd` cho các nút |
| `HISTORY.md` | Cập nhật changelog |

---

## [v1.3.0] - 2026-06-27

### ✨ Tính năng mới & Nâng cấp (Major Update)
- **Ảnh bìa Thumbnail**: Thay thế icon mặc định bằng hình ảnh thu nhỏ trang đầu tiên của file PDF trong tab "Tất cả file".
  - Áp dụng công nghệ `PdfRenderer` chạy ngầm.
  - Tối ưu hóa cực nhẹ cho các máy cấu hình thấp (như Kindle Fire HD 10) nhờ vào bộ nhớ đệm `LruCache` giúp vuốt danh sách siêu mượt, không bị tràn RAM.
- **Tối ưu hoá cực hạn với Room Database**:
  - Chuyển đổi toàn bộ hệ thống lưu trữ "Danh sách đọc" từ `SharedPreferences` sang kiến trúc `Room Database` (SQLite).
  - Khắc phục hoàn toàn tình trạng máy bị đứng, treo ứng dụng khi số lượng file lên tới mức hàng ngàn (2.000 - 5.000 file).
  - Tự động di chuyển (migrate) dữ liệu cũ của người dùng sang hệ thống mới để không mất danh sách đang đọc dở.

### 📝 File đã sửa/thêm mới
| File | Thay đổi |
|------|----------|
| `build.gradle` | Thêm các plugin và thư viện Room |
| `PdfEntity`, `PdfDao`, `AppDatabase` | Thêm mới: Kiến trúc Database cho Room |
| `ReadingListManager.kt` | Nâng cấp toàn diện sang Room + tự động chuyển đổi dữ liệu cũ |
| `item_pdf_file.xml` | Đổi icon thành `ImageView` (id: `ivThumbnail`) |
| `PdfThumbnailLoader.kt` | Thêm mới: Module nạp ảnh bìa bất đồng bộ có giới hạn RAM an toàn |
| `PdfFileAdapter.kt` | Tích hợp Coroutines nạp thumbnail bất đồng bộ với cơ chế tái chế chống leak RAM |
| `HISTORY.md` | Cập nhật changelog lên `v1.3.0` |

---

## [v1.2.3] - 2026-06-27

### ✨ Tính năng mới
- **Danh sách thư mục Drive**: Sau khi kết nối thành công với Google Drive, app sẽ tự động gọi API để lấy toàn bộ danh sách các thư mục có sẵn và hiển thị trong dropdown.
- Người dùng có thể nhấn vào để chọn thư mục từ danh sách (mặc định vẫn là `shiyo`), hoặc tự gõ tên thư mục nếu muốn (nhờ sử dụng `AutoCompleteTextView`).

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `SyncManager.kt` | Thêm hàm `listAllFolders()` gọi API Drive để tìm các file có `mimeType='application/vnd.google-apps.folder'` |
| `activity_sync.xml` | Đổi `EditText` thành `AutoCompleteTextView` |
| `SyncActivity.kt` | Tải danh sách thư mục ngầm và gắn vào Adapter khi đăng nhập thành công |
| `HISTORY.md` | Cập nhật changelog |

---

## [v1.2.2] - 2026-06-27

### 🐛 Sửa lỗi
- **Fix lỗi thiếu đa ngôn ngữ**: Cập nhật các nút `← Back`, `◀ Trang trước` và `Trang sau ▶` trong `PdfViewerActivity` và `SyncActivity` sử dụng `LocaleHelper` để hỗ trợ dịch sang tiếng Nhật.

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `LocaleHelper.kt` | Thêm chuỗi dịch `back_button` |
| `PdfViewerActivity.kt` | Thay hardcoded text bằng `LocaleHelper.getString()` cho các nút điều hướng |
| `SyncActivity.kt` | Áp dụng đa ngôn ngữ cho nút `btnBack` |
| `HISTORY.md` | Cập nhật changelog |

---

## [v1.2.1] - 2026-06-27

### 🐛 Sửa lỗi
- **Fix crash khi vuốt trái/phải**: App bị crash khi vuốt ngang để chuyển file do gesture trigger đồng thời với việc close/reopen PdfRenderer. Thêm flag `isNavigating` chống double-trigger, đóng page trước khi chuyển file.

### 🔧 Thay đổi
- **Đổi logic vuốt trái/phải**: Vuốt trái = chuyển sang trang tiếp theo (thay vì chuyển file). Nếu đang ở trang cuối thì mới chuyển sang file tiếp theo. Vuốt phải tương tự ngược lại.
- **Nút ◀/▶ cũng chuyển file**: Khi đang ở trang cuối, ấn ▶ sẽ mở file tiếp theo. Khi đang ở trang đầu, ấn ◀ sẽ mở file trước đó.
- **Natural sort thứ tự file**: Sắp xếp file theo số tự nhiên (1 → 2 → 10 → 20) thay vì alphabetical (1 → 10 → 2 → 20). File có tên là số sẽ được sắp xếp đúng thứ tự.
- **Hiển thị vị trí file**: Title bar hiển thị `[3/10] filename.pdf` để biết đang xem file thứ mấy trong danh sách.

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `PdfViewerActivity.kt` | Fix crash, đổi logic vuốt, hiển thị vị trí file, nút nav chuyển file |
| `MainActivity.kt` | Natural sort thứ tự file |
| `HISTORY.md` | Cập nhật changelog |

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
