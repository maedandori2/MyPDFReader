# 📋 Lịch sử thay đổi — MyPDFReader

## [v1.5.1] - 2026-07-02

### 🐛 Rà soát & Sửa lỗi toàn dự án

#### 🔴 Lỗi nghiêm trọng đã fix
- **`SyncActivity` — Sai path sync**: `localFolderFile` bị tạo sai bằng cách ghép `filesDir` + `"/sdcard/MyPDF"` → file tải về nhưng không thấy trên màn hình. Đã sửa thành `File(MainActivity.PDF_FOLDER)` trực tiếp, đồng bộ với `SyncWorker`.
- **`PdfViewerActivity` — OOM crash**: Bitmap trang cũ không được `recycle()` trước khi render trang mới → leak bộ nhớ tích lũy → `OutOfMemoryError` sau nhiều lần chuyển trang. Đã thêm `oldBitmap?.recycle()`.
- **`PdfViewerActivity` — UI đóng băng**: `Bitmap.createBitmap()` + `page.render()` chạy trên **Main Thread** → UI đóng băng khi chuyển trang. Đã chuyển sang `Dispatchers.IO` trong `lifecycleScope.launch{}`.
- **`PdfViewerActivity` — Matrix không reset**: Khi chuyển trang, `matrix` không được `reset()` trước khi `fitToScreen()` → ảnh có thể render sai vị trí. Đã thêm `matrix.reset()`.
- **`UpdateChecker` — BroadcastReceiver leak**: Receiver chỉ unregister khi download thành công; nếu download bị hủy hoặc thất bại → leak vĩnh viễn. Đã thêm `Handler.postDelayed` tự unregister sau 5 phút.
- **`PdfTextExtractor` — TextRecognizer không close**: Mỗi lần OCR tạo instance mới mà không đóng → resource leak. Đã chuyển sang `lazy` singleton.
- **`SettingsActivity` — Crash khi mở Setting**: `SettingsManager.init(context)` chưa từng được gọi ở bất cứ đâu trong codebase → truy cập `SharedPreferences` gây ngoại lệ `UninitializedPropertyAccessException`. Đã thêm `SettingsManager.init(this)` vào `MainActivity` và `SettingsActivity`.
- **`MainActivity` — Mất kết nối tính năng đọc metadata PDF**: `PdfMetadataManager.init(this)` và sự kiện cho nút `"🔍 Scan"` (`btnScanMetadata`) chưa từng được gán vào `MainActivity` từ phiên bản trước → ứng dụng không tải hoặc không cho phép scan hiển thị thông tin 品名, 自社品番, 自社品名. Đã kết nối đầy đủ khởi tạo, load JSON và dialog hiển thị tiến trình scan metadata.

#### 🟡 Hiệu năng đã cải thiện
- **`MainActivity.loadPdfFiles()`**: Chuyển scan file system từ **Main Thread đồng bộ** sang `Dispatchers.IO` trong coroutine → không còn block UI khi `onResume()`.
- **`SyncManager`**: Thêm `connectTimeout = 15s` và `readTimeout = 30–60s` cho tất cả HTTP connections (`findFolderId`, `listDriveFiles`) → app không treo vô thời hạn khi mạng kém.

### ✨ Tính năng mới & UI
- **Cập nhật ứng dụng & Tinh gọn Setting**: 
  - Chuyển logic kiểm tra và tải cập nhật (UpdateChecker) từ popup tự động ở trang chủ vào màn hình **⚙ Setting** với thanh progress (tiến trình tải % real-time).
  - Bỏ phần "Cài đặt hiển thị" (cỡ chữ, độ trong suốt, thời gian thông báo) khỏi màn hình Setting theo yêu cầu để giao diện tinh gọn, chỉ tập trung vào kiểm tra cập nhật.
- **Đồng bộ 2 chiều riêng cho file `pdf_metadata.json` (`SyncManager`)**:
  - Các file PDF tài liệu giữ nguyên cơ chế **chỉ tải về từ Drive (1 chiều)** nếu bản trên Drive mới hơn.
  - Riêng file thông tin OCR `pdf_metadata.json` áp dụng cơ chế **đồng bộ 2 chiều thông minh**:
    - Nếu file trên Drive mới hơn máy: tải về máy và tự động cập nhật hiển thị lên danh sách.
    - Nếu file trên máy mới hơn Drive (do người dùng vừa bấm nút Scan OCR tạo dữ liệu mới): tự động tải lên Google Drive (ghi đè file cũ hoặc tạo file mới nếu Drive chưa có) để chia sẻ kết quả quét cho các thiết bị khác.

#### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `SyncActivity.kt` | Fix sai path localFolderFile |
| `PdfViewerActivity.kt` | Render IO thread, recycle bitmap cũ, reset matrix, thêm coroutine imports |
| `UpdateChecker.kt` | Fix BroadcastReceiver leak, thêm Handler/Looper imports |
| `PdfTextExtractor.kt` | TextRecognizer lazy singleton |
| `MainActivity.kt` | loadPdfFiles() chạy trên IO thread |
| `SyncManager.kt` | Thêm HTTP timeout cho findFolderId và listDriveFiles |

---

## [v1.5.0] - 2026-07-01

### 🔍 Trích xuất thông tin PDF (品名, 自社品番, 自社品名)
- **OCR trang đầu PDF**: Dùng ML Kit Text Recognition (Japanese) để trích xuất 品名, 自社品番, 自社品名 từ trang đầu.
- **Bounding box parsing**: Dùng vị trí pixel (bounding box) của từng element OCR thay vì parse text thuần. Tìm key (品名) → lấy element ngay bên phải cùng dòng = giá trị. Đảm bảo đọc đúng bảng dù OCR trả text không theo thứ tự.
- **Chống nhầm key chuỗi con**: Tìm key dài trước (自社品番 → 自社品名 → 品番 → 品名). Khi tìm "品名" loại trừ element chứa "自社品名", tìm "品番" loại trừ "自社品番". Hỗ trợ cả 2 dạng file.
- **Ưu tiên vị trí**: Nếu từ khóa xuất hiện nhiều lần trong trang, luôn lấy từ khóa nằm ở vị trí cao nhất (trên cùng) của trang PDF để tránh lấy nhầm thông tin ở phần nội dung.
- **Xử lý ô trống/dấu gạch ngang**: Nếu ô giá trị là dấu gạch ngang (-) và bị OCR bỏ qua, hệ thống sẽ trả về rỗng (null) thay vì nhảy sang lấy giá trị của cột tiếp theo (như カラー).
- **Ưu tiên từ khóa**: Nếu trên trang PDF có cả cụm `自社品番/自社品名` và `品番/品名`, hệ thống sẽ ưu tiên trích xuất `自社品番/自社品名` (thường ở góc trên bên trái) và bỏ qua cụm còn lại để hiển thị gọn gàng 1 trong 2 dạng.
- **Nút "🔍 Scan"**: Trên header, bấm để scan tất cả file chưa có metadata. Hiện dialog progress (1/20, 2/20...).
- **Hiển thị metadata**: Dưới tên file PDF, hiện metadata màu xanh teal (11sp). Luôn hiển thị theo thứ tự chuẩn: `自社品番 | 自社品名 | 品番 | 品名` (nếu file có đủ cả 4 thông tin, app sẽ lấy và hiện đủ cả 4).
- **Lưu file JSON**: Kết quả OCR lưu vào `pdf_metadata.json` trong thư mục MyPDF.
- **Đồng bộ Google Drive**: Khi Sync, upload `pdf_metadata.json` lên Drive. Máy khác sync sẽ download và merge metadata.
- **Update file description**: Gán metadata vào description của file PDF trên Google Drive.

### 📁 File mới
| File | Mô tả |
|------|-------|
| `PdfMetadataManager.kt` | Singleton quản lý file `pdf_metadata.json` — load/save/merge/format |
| `PdfTextExtractor.kt` | OCR trích xuất thông tin từ trang đầu PDF bằng ML Kit Japanese |

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `build.gradle` | Thêm dependency `com.google.mlkit:text-recognition-japanese:16.0.1` |
| `item_pdf_file.xml` | Thêm `tvMetadata` TextView dưới tên file |
| `PdfFileAdapter.kt` | Hiển thị metadata cho cả 2 tab (All + Reading List) |
| `activity_main.xml` | Thêm nút `btnScanMetadata` trên header |
| `MainActivity.kt` | Init PdfMetadataManager, xử lý nút Scan với progress dialog |
| `SyncManager.kt` | Thêm sync metadata JSON (upload/download/merge) + update file description |
| `LocaleHelper.kt` | Thêm chuỗi scan_title, scan_preparing, scan_complete, all_scanned (vi+ja) |
| `HISTORY.md` | Cập nhật changelog v1.5.0 |

---

## [v1.4.6] - 2026-07-01

### 🔄 Tách biệt gesture vuốt dọc/ngang
- **Vuốt lên/xuống**: Chỉ chuyển trang trong file PDF đang mở. Hiện toast khi đã ở trang đầu/cuối.
- **Vuốt trái/phải**: Chỉ chuyển qua lại giữa các file khác nhau (không còn chuyển trang).
- Trước đây cả hai hướng vuốt đều có thể chuyển trang + file, gây nhầm lẫn.

---

## [v1.4.5] - 2026-06-30

### 🎨 Cải thiện giao diện Header
- **Nút Settings đẹp hơn**: Đổi từ emoji ⚙ đơn giản thành nút "⚙ Setting" có background xanh đậm bo tròn, chữ trắng đậm, hiệu ứng ripple khi nhấn.
- **Cờ ngôn ngữ to hơn**: Tăng kích thước emoji cờ 🇻🇳🇯🇵 từ 28sp → 36sp, dễ bấm hơn trên tablet.

### ✨ Thông báo đọc file khi vuốt chuyển file
- **Hiện thông báo khi vuốt**: Khi đọc file từ Danh sách đọc và vuốt sang file tiếp theo/trước đó, thông báo "📖 Đang đọc file số X" sẽ tự động hiện lên cho file mới.
- **Hủy timer cũ khi vuốt nhanh**: Khi vuốt liên tục, timer và animation cũ được hủy trước khi hiển thị thông báo mới, tránh bị overlap.

### 📁 File mới
| File | Mô tả |
|------|-------|
| `bg_settings_btn.xml` | Drawable background cho nút Settings: hình chữ nhật bo tròn 8dp, màu `#0D47A1`, có ripple effect |

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `activity_main.xml` | Tăng `textSize` cờ 28sp→36sp, đổi btnSettings thành nút có text+background đẹp |
| `PdfViewerActivity.kt` | Thêm field `isFromReadingList`, gọi `showReadingNotice()` trong `switchFile()`, thêm `removeCallbacksAndMessages` + `animate().cancel()` để reset khi vuốt nhanh |
| `HISTORY.md` | Cập nhật changelog lên `v1.4.5` |

---


### ✨ Tính năng mới: Nút cài đặt hiển thị (⚙ Settings)
- **Nút ⚙ trên header**: Thêm nút Settings trên thanh header (giữa cờ 🇯🇵 và nút Sync) để mở hộp thoại cài đặt.
- **Cỡ chữ tên file tùy chọn**: SeekBar cho phép chỉnh cỡ chữ tên file trong Danh sách đọc từ 12sp → 32sp (mặc định 19sp).
- **Độ trong suốt thông báo**: SeekBar chỉnh opacity thông báo "Đang đọc file số X" từ 10% → 100% (mặc định 50%).
- **Thời gian hiển thị thông báo**: SeekBar chỉnh thời gian hiển thị thông báo từ 1s → 30s (mặc định 5s).
- **Lưu cài đặt**: Tất cả cài đặt được lưu vào SharedPreferences, giữ nguyên khi tắt app.
- **Đa ngôn ngữ**: Hộp thoại hiển thị đúng ngôn ngữ (Việt/Nhật).

### 📁 File mới
| File | Mô tả |
|------|-------|
| `SettingsManager.kt` | Singleton quản lý cài đặt hiển thị (cỡ chữ, opacity, thời gian) qua SharedPreferences |

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `activity_main.xml` | Thêm nút `btnSettings` (⚙) vào header |
| `MainActivity.kt` | Thêm import `AlertDialog`, `SeekBar`, `LinearLayout`, `TextView`. Init `SettingsManager`, thêm `showSettingsDialog()` với 3 SeekBar |
| `PdfFileAdapter.kt` | Đổi `textSize = 19f` → `SettingsManager.getFileNameSize().toFloat()` |
| `PdfViewerActivity.kt` | Đổi `alpha = 0.5f` → `SettingsManager.getNoticeOpacityFloat()`, `5000` → `SettingsManager.getNoticeDurationMs()` |
| `LocaleHelper.kt` | Thêm 6 chuỗi dịch settings: `settings_title`, `settings_file_name_size`, `settings_notice_opacity`, `settings_notice_duration`, `settings_save`, `settings_cancel` |
| `HISTORY.md` | Cập nhật changelog lên `v1.4.4` |

---


### ✨ Thông báo đang đọc file số mấy
- **Overlay thông báo**: Khi mở file từ **Danh sách đọc**, trên màn hình PDF sẽ hiện thông báo "📖 Đang đọc file số X" (tiếng Việt) hoặc "📖 ファイル X を読んでいます" (tiếng Nhật) tùy theo ngôn ngữ đang chọn.
- **Tự động ẩn sau 5 giây**: Thông báo hiển thị ở opacity 50%, sau 5 giây sẽ tự fade out mượt mà rồi biến mất.
- **Chỉ hiển thị khi mở từ danh sách đọc**: Không hiện khi mở file từ tab "Tất cả file".

### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `activity_pdf_viewer.xml` | Thêm `tvReadingNotice` (TextView overlay, background bán trong suốt) |
| `PdfViewerActivity.kt` | Thêm import `ObjectAnimator`, thêm `noticeHandler`, đọc `reading_list_index` từ intent, thêm hàm `showReadingNotice()` với fade-out animation |
| `MainActivity.kt` | Truyền thêm `reading_list_index` qua intent khi mở file từ reading list |
| `LocaleHelper.kt` | Thêm chuỗi dịch `reading_file_number` cho cả tiếng Việt và tiếng Nhật |
| `HISTORY.md` | Cập nhật changelog lên `v1.4.3` |

---


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
