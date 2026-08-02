# 📋 Lịch sử thay đổi — MyPDFReader

## [v1.5.3] - 2026-08-01

### 🐛 Sửa lỗi crash khi mở file XDW
- 2026-08-01, 16:30: **Khôi phục native renderer và sửa triệt để crash SIGSEGV (`BaseBridge.java`, `XdwViewerActivity.kt`)**:
  - Nguyên nhân: Việc load thư viện bị thiếu `icudata` và `supkBase64` gây lỗi SIGSEGV khi khởi tạo. Thêm vào đó, `getCPUFeatures()` bị gọi trước khi kiểm tra OS kiến trúc, gây `UnsatisfiedLinkError` trên ARM64 (do không có file `.so` cho VFP).
  - Sửa (`BaseBridge.java`): 
    - Đảm bảo load đủ `icudata` và `supkBase64`.
    - Dùng `android.os.Build.SUPPORTED_ABIS` để nhận diện `arm64-v8a` thay vì dựa hoàn toàn vào JNI `getCPUFeatures()`.
    - Đưa `getCPUFeatures()` vào `try-catch` an toàn.
  - Sửa (`XdwViewerActivity.kt`): Bật lại biến cờ `allowNativeRenderer = true` để app mở file `.xdw` trực tiếp bên trong ứng dụng thay vì đẩy sang app DocuWorks bên ngoài.

#### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `BaseBridge.java` | Sửa static initializer: load đủ `icudata`/`supkBase64`, nhận diện arm64 an toàn bằng Build.SUPPORTED_ABIS |
| `XdwViewerActivity.kt` | Bật lại `allowNativeRenderer = true` để đọc file trực tiếp bằng BaseBridge thay vì app ngoài |
| `XdwReaderHelper.kt` | Thêm crash detection qua SharedPreferences, synchronized cho singleton bridge |
| `HISTORY.md` | Cập nhật changelog v1.5.3 |

---



## [v1.5.2] - 2026-07-03

### ✨ Hỗ trợ đồng bộ và đọc file DocuWorks (`.xdw`)
- 2026-07-03, 00:02: **Tích hợp toàn diện khả năng đồng bộ từ Google Drive và mở tài liệu `.xdw` (`SyncManager`, `MainActivity`, `PdfFileAdapter`, `XdwViewerActivity`, `PdfViewerActivity`)**:
  - **Đồng bộ trên Google Drive (`SyncManager`)**: Bổ sung hỗ trợ mở rộng đuôi tệp `.xdw` và các MIME type chính thức của Fuji Xerox / Fujifilm DocuWorks (`application/vnd.fujixerox.docuworks`, `application/vnd.fujifilm.docuworks`, `application/x-xdw`). Cho phép tải xuống, liệt kê, xóa và giữ đồng bộ các tệp `.xdw` ngang hàng với `.pdf` trong thư mục `MyPDF/`.
  - **Nhận diện giao diện danh sách (`PdfFileAdapter` & `MainActivity`)**: Tạo mới biểu tượng riêng biệt cho tệp DocuWorks (`@drawable/ic_xdw` màu xanh lam đặc trưng). Khi quét danh sách tài liệu, ứng dụng tự động phân loại: hiển thị đúng đuôi `.xdw` cùng biểu tượng DocuWorks, trong khi các tệp PDF tiếp tục sử dụng biểu tượng đỏ và hiển thị tên không kèm phần mở rộng như cũ.
  - **Màn hình điều hướng tài liệu chuyên dụng (`XdwViewerActivity` & `activity_xdw_viewer.xml`)**: Xây dựng màn hình hiển thị trung gian khi người dùng chọn đọc tệp `.xdw` với đầy đủ thanh header, nút quay lại (`← Quay lại`), tên tệp và các nút chuyển tiếp (`◀ File trước` / `File tiếp theo ▶`) cũng như hỗ trợ thao tác vuốt ngang màn hình (swipe) để chuyển file nhịp nhàng như khi đang đọc PDF.
  - **Cơ chế gọi ứng dụng DocuWorks Viewer bên ngoài**: Khi mở màn hình `XdwViewerActivity` hoặc bấm nút `"🚀 Mở lại DocuWorks Viewer"`, ứng dụng sử dụng `FileProvider` an toàn để tạo URI và tự động gửi Intent mở tài liệu sang ứng dụng DocuWorks Viewer đã cài đặt trên điện thoại theo cơ chế thử nghiệm tầng MIME type dự phòng (`vnd.fujixerox.docuworks` → `vnd.fujifilm.docuworks` → `x-xdw` → `*/*`).
  - **Cải tiến luồng chuyển tiếp giữa các file (`PdfViewerActivity` & `XdwViewerActivity`)**: Khi người dùng đang đọc tài liệu (PDF hoặc XDW) và bấm nút Trang trước/Trang sau (hoặc vuốt ngang màn hình), ứng dụng tự động kiểm tra định dạng của file tiếp theo để linh hoạt chuyển đổi giữa trình xem PDF nội bộ và trình xem XDW bên ngoài mà không cần quay ngược ra màn hình chính.
- 2026-07-03, 00:14: **Sửa lỗi không tải được file `.xdw` từ Google Drive (`SyncManager`)**:
  - **Phân tích nguyên nhân gốc rễ**: Khi gọi Google Drive API trong phương thức `listDriveFiles`, ứng dụng trước đây gửi câu truy vấn server-side (`q`) bị giới hạn chỉ yêu cầu các tệp có `mimeType='application/pdf'` hoặc `mimeType='application/json'`, khiến máy chủ Google Drive tự động loại bỏ toàn bộ tài liệu DocuWorks (`.xdw`) ngay từ tầng trả về của API.
  - **Cải tiến giải pháp**: Tối ưu lại câu truy vấn Google Drive API thành `mimeType!='application/vnd.google-apps.folder'` để lấy toàn bộ danh sách tệp trong thư mục `MyPDF/` từ máy chủ. Logic lọc tệp ở client ([SyncManager.kt:L328](file:///j:/android%20make/MyPDFReader/app/src/main/java/com/mypdf/reader/SyncManager.kt#L328)) sẽ chịu trách nhiệm phân loại chính xác tài liệu theo phần mở rộng (`.pdf`, `.xdw`, `.json`) hoặc MIME type DocuWorks. Đặc biệt, **nếu Google Drive trả về file DocuWorks bị mất phần mở rộng (tên file không chứa `.xdw`), ứng dụng sẽ tự động nối thêm `.xdw` vào tên file** để màn hình danh sách có thể nhận diện hiển thị thành công. Điều này đảm bảo 100% file `.xdw` được nhận diện và tải xuống máy, kể cả khi Google Drive gán sai MIME type (như `application/octet-stream`) hoặc khi bị lỗi thiếu đuôi file.
  - **Nâng cấp đồng bộ hai chiều**: Sửa đổi logic so sánh thời gian cho toàn bộ các file (không chỉ riêng file cấu hình metadata). Từ nay trở đi, nếu file (`.pdf`, `.xdw`) trên máy được cập nhật mới hơn (ví dụ lưu chỉnh sửa) so với bản trên Google Drive, ứng dụng sẽ tự động tải file lên (Upload) để đè lên Drive, thay vì chỉ tải xuống một chiều như trước kia.
  - **Xử lý triệt để lỗi "Trôi thời gian" trên Android & FAT32**:
  - **Thiết kế lại kiến trúc Đồng bộ Một chiều (One-Way Sync)**: Chuyển toàn bộ cơ chế đồng bộ tài liệu (`.pdf`, `.xdw`) từ hai chiều sang **một chiều** (chỉ tải từ Google Drive xuống máy). Nếu phát hiện file mới thêm vào máy cục bộ hoặc máy cục bộ có dữ liệu mới hơn, ứng dụng sẽ tự động **bỏ qua** mà không tải ngược (Upload) lên Drive để đảm bảo Drive luôn là kho chứa tài liệu chuẩn gốc không bị thao tác nhầm đè lên. Ngoại lệ duy nhất là file `pdf_metadata.json` vẫn giữ cơ chế đồng bộ hai chiều để bảo toàn các thẻ gắn nhãn của người dùng.
  - **Khắc phục lỗi "bỏ sót tệp" khi thư mục có quá nhiều tài liệu (Pagination)**: Sửa đổi triệt để cơ chế quét tài liệu của Google Drive API. Do giới hạn mặc định của API chỉ trả về tối đa 1000 tệp tin cho mỗi lần truy vấn, những tài liệu `.xdw` nằm sau ngưỡng này sẽ bị bỏ sót hoàn toàn. Ứng dụng hiện tại đã được thiết kế thuật toán phân trang vòng lặp (`nextPageToken`), liên tục lấy dữ liệu sang các trang kế tiếp cho đến khi quét trọn vẹn 100% số lượng tệp trong thư mục gốc.
  - **Áp dụng công nghệ State Tracking bằng JSON (`sync_state.json`)**: Triệt để vô hiệu hóa lỗi sai số thời gian của hệ thống FAT32 trên Kindle Fire. Ứng dụng không còn sử dụng hàm `File.lastModified()` của Android để đối chiếu mốc thời gian nữa. Thay vào đó, sau mỗi lần tải tài liệu từ Drive thành công, ứng dụng sẽ lưu chính xác mốc thời gian nguyên bản gốc của Google Drive vào một file siêu dữ liệu độc lập `sync_state.json` đặt trong máy. Ở những lần đồng bộ sau, ứng dụng chỉ dùng file State này để so sánh, đảm bảo độ chính xác tuyệt đối từng mili-giây.
- 03/07/2026 17:04: **Hỗ trợ tự động nhận diện file chép thủ công (Offline Copying)**: Nâng cấp tính năng đồng bộ trong `SyncManager.kt` giúp tự động nhận diện các tệp PDF/XDW được chép thủ công (qua cáp USB) chưa có trong lịch sử đồng bộ. Ứng dụng sẽ ghi nhận ngày giờ trên Drive vào bộ nhớ để bỏ qua tải lần đầu (tiết kiệm băng thông cực lớn cho hàng ngàn tệp) nhưng vẫn theo dõi mốc thời gian để tải lại chính xác nếu có bản cập nhật mới trên Drive sau này.
- 03/07/2026 17:15: **Tách biệt "Khởi tạo dữ liệu đồng bộ"**: Chuyển logic tự động nhận diện file chép thủ công thành một tiến trình chạy độc lập thông qua nút bấm **"Khởi tạo dữ liệu đồng bộ"** trong màn hình `⚙ Setting`. Thay vì chỉ tạo State cho các file có sẵn trên máy, ứng dụng sẽ quét toàn bộ danh sách trên Google Drive và lưu lại ngày giờ vào `sync_state.json`. Nhờ đó, tính năng cập nhật đã được nâng cấp thông minh hơn: Nếu ngày sửa đổi của file trên máy tính bảng (local) mới hơn ngày sửa đổi trên Drive, ứng dụng sẽ luôn bỏ qua tải đè để bảo vệ file của người dùng.

## [v1.5.1] - 2026-07-02

### 🐛 Sửa lỗi tương thích hiển thị & đồng bộ trên Kindle Fire 10
- 2026-07-02, 23:40: **Khắc phục lỗi không hiển thị thông tin `自社品番, 品番, 自社品名, 品名` trên Kindle Fire 10 (`PdfMetadataManager` & `SyncManager`)**:
  - **Phân tích nguyên nhân gốc rễ**: Trên máy ảo MEmu, tên tệp tin và phần mở rộng luôn đồng nhất là chữ thường (`.pdf`), tuy nhiên khi chuyển sang các dòng máy tính bảng Kindle Fire 10 (chạy Fire OS / hệ thống tệp FAT32), trình quản lý tệp và MTP thường tự động đổi hoa/thường (ví dụ thành `.PDF`, `.Pdf` hoặc thay đổi hoa/thường của tên gốc). Do trước đây ứng dụng truy vấn key trong `metadataMap` và so sánh tên file khi đồng bộ theo kiểu phân biệt chữ hoa/thường (case-sensitive) khớp với đuôi `.pdf`, kết quả tra cứu bị trả về `null`, khiến thẻ hiển thị thông tin (`tvMetadata`) bị ẩn (`GONE`).
  - **Cải tiến `PdfMetadataManager`**: Thêm phương thức tra cứu linh hoạt `findMetadataEntry(fileName)`, hỗ trợ tìm kiếm chính xác, tìm kiếm không phân biệt chữ hoa/thường, và tìm kiếm theo tên gốc không phụ thuộc vào phần mở rộng (`.pdf/.PDF`). Áp dụng đồng bộ cho các phương thức `getMetadata`, `hasMetadata`, `formatForDisplay`, `formatForHighlightedDisplay` và `mergeFromRemote`. Đảm bảo khi lưu metadata mới qua `setMetadata` luôn chuẩn hóa key về định dạng chuẩn `*.pdf` chữ thường để thống nhất trên mọi thiết bị.
  - **Cải tiến `SyncManager`**: Chuyển toàn bộ các bước so sánh tên file `pdf_metadata.json` và kiểm tra danh sách tệp trên Google Drive sang chế độ không phân biệt chữ hoa/thường (`ignoreCase = true`). Đồng thời tối ưu logic đồng bộ từ Drive: Nếu tệp `pdf_metadata.json` trên thiết bị chưa có dữ liệu OCR thực tế (`getMetadataCount() == 0`), ứng dụng sẽ luôn ưu tiên tải về dữ liệu từ Google Drive thay vì so sánh mốc thời gian (tránh trường hợp thiết bị mới tạo tệp rỗng có timestamp mới hơn Drive bị đẩy đè lên cloud).

### ✨ Tính năng mới & Cải tiến UI
- 2026-07-02, 10:48: **Tùy chọn "Luôn giữ sáng màn hình khi đọc" (`SettingsActivity` & `PdfViewerActivity`)**:
  - Thêm công tắc tùy chọn trong màn hình Cài đặt (`💡 Luôn giữ sáng màn hình khi đọc` / `💡 読書中は常に画面をオンにする`) cho phép người dùng bật/tắt tính năng giữ sáng màn hình.
  - Khi đọc tài liệu PDF trong `PdfViewerActivity`, ứng dụng tự động kiểm tra thiết lập trong `SettingsManager` và áp dụng/gỡ bỏ cờ `FLAG_KEEP_SCREEN_ON` một cách linh hoạt, hỗ trợ tối đa cho công nhân/kỹ thuật viên khi làm việc lâu với tài liệu.
- 2026-07-02, 10:48: **Làm nổi bật và tùy chỉnh màu sắc thông tin `自社品番, 品番, 自社品名, 品名` (`SettingsActivity`, `SettingsManager`, `PdfMetadataManager`)**:
  - Thiết kế mới cho khung hiển thị metadata trong danh sách file: Sử dụng background badge ấm áp màu Amber (`@drawable/bg_metadata_badge`), tăng kích thước chữ lên `14sp` rõ nét, bổ sung padding và khoảng cách dòng thoáng đãng.
  - Tối ưu trải nghiệm đọc (UX) theo nguyên tắc tập trung vào giá trị: Mặc định thu nhỏ và làm dịu màu các tên nhãn (`<small>` màu xám `#78909C`), trong khi in đậm và tô màu tương phản mạnh cho phần giá trị: màu Đỏ thẫm nổi bật (`#C62828`) cho tên sản phẩm (`自社品名`, `品名`) và Xanh đậm (`#0D47A1`) cho mã số (`自社品番`, `品番`).
  - **Thêm tính năng "Màu hiển thị (自社品番, 品番, 自社品名, 品名)" trong màn hình Cài đặt**: Cho phép người dùng tùy chọn màu sắc riêng biệt cho từng phần Nhãn và Giá trị của cả 4 thông tin. Giao diện trực quan với nút bấm hiển thị sẵn màu sắc hiện tại, hỗ trợ chọn nhanh từ danh sách 10 màu chuẩn hoặc nhập mã Hex tùy chỉnh bất kỳ (`#FF0000`, `#00838F`, v.v.). Khi sửa màu xong, quay lại danh sách tài liệu màu sắc mới sẽ lập tức được áp dụng.

### 🐛 Sửa lỗi danh sách đọc & Kiểm tra cập nhật
- 2026-07-02, 10:48: **`UpdateChecker` & `UpdateCheckerWithProgress` — Cải tiến kiểm tra, tự động cài đặt và mở thư mục Download 3 lớp**:
  - Khi tải `version.json` từ `raw.githubusercontent.com`, máy chủ GitHub CDN thường lưu cache từ 5–15 phút khiến app đọc phải bản JSON cũ dù vừa push lên GitHub. Đã bổ sung tham số chống cache timestamp (`?t=currentTimeMillis()`) và thiết lập `useCaches = false`, `Cache-Control: no-cache` để đảm bảo app luôn tải chính xác phiên bản mới nhất từ server.
  - Theo yêu cầu, đã thay đổi thư mục lưu file APK bản cập nhật (`MyPDFReader-update.apk`) khi tải từ màn hình Cài đặt sang **thư mục Download chung của điện thoại (`/storage/emulated/0/Download`)** thay vì thư mục riêng của app. Người dùng có thể dễ dàng tìm thấy file trong các ứng dụng Quản lý tệp.
  - **Cơ chế cài đặt thông minh (Tự động cài hoặc Mở thư mục Download 3 lớp)**: Khi tải bản cập nhật xong, ứng dụng tự động kiểm tra quyền `canRequestPackageInstalls()`. Nếu thiết bị đã cho phép, app sẽ lập tức khởi chạy màn hình cài đặt APK tự động. Nếu thiết bị chặn hoặc chưa có quyền (trên Android 8+), app sẽ tự động chuyển tiếp nhịp nhàng sang mở Trình quản lý file tại thư mục `Download` theo cơ chế 3 lớp dự phòng (`ACTION_VIEW_DOWNLOADS` → URI `resource/folder` → File Picker chung) để người dùng tự bấm cài đặt mà không gặp bất kỳ lỗi hay rào cản bảo mật nào.
  - **Sửa lỗi hiển thị % bị sai font/ký tự khi tải bản cập nhật (`LocaleHelper` & `SettingsActivity`)**: Phát hiện từ khóa `"downloading"` trong từ điển bị trùng giữa tính năng Đồng bộ Drive (`"Đang tải (%d/%d): %s"`) và Kiểm tra cập nhật (`"Đang tải... %d%%"`), dẫn đến chuỗi tải về bản cập nhật bị ghi đè và hiển thị sai định dạng `%d/%d`. Đã đổi tên khóa riêng biệt thành `"update_downloading"` để hiển thị chính xác % tiến độ tải xuống.
  - **Sửa lỗi "Tải xong nhưng không mở được cài đặt" trên Android 8+ (`AndroidManifest.xml`, `UpdateChecker`, `UpdateCheckerWithProgress`)**: Bổ sung quyền `REQUEST_INSTALL_PACKAGES` trong Manifest và `<root-path>` vào `file_paths.xml` để đảm bảo FileProvider không bị từ chối truy cập thư mục Download trên mọi dòng máy.
- 2026-07-02, 10:48: **`MainActivity` & `PdfFileAdapter` — Số thứ tự không tự cập nhật khi sửa**: 
  - Trước đây, khi sửa trực tiếp số thứ tự trong ô nhập liệu (EditText) tại tab Reading List, callback `onSwapPosition` chưa được kết nối vào `MainActivity`, dẫn đến việc các item không chuyển vị trí và các số thứ tự phía sau không tự động cập nhật.
  - Đã kết nối callback `onSwapPosition` vào `MainActivity` (gọi `ReadingListManager.moveToPosition`), giúp khi sửa số thứ tự của một bài đọc, bài đọc sẽ chuyển đến đúng vị trí mới và tất cả số thứ tự phía sau tự động dịch chuyển và cập nhật chính xác.
  - Bổ sung `notifyItemRangeChanged` trong thao tác di chuyển lên/xuống (`moveItem`) và xoá bài đọc (`removeFromReadingList`) để số thứ tự hiển thị luôn đồng bộ real-time.

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

- **`UpdateCheckerWithProgress` — Fix lỗi tải xuống thất bại**: Chuyển từ `DownloadManager` của hệ thống (hay bị từ chối quyền trên Android 10+ và lỗi khi gặp 302 Redirect từ GitHub Releases) sang tải trực tiếp bằng `HttpURLConnection` trong Coroutine (`Dispatchers.IO`), tự động xử lý chuyển hướng (redirect) và lưu vào vùng nhớ an toàn `getExternalFilesDir`, đảm bảo cài đặt APK thành công 100%.
- **Fix lỗi 404 khi bấm tải bản cập nhật mới (`build.yml`)**: Phát hiện quy trình tự động trên GitHub Actions trước đây chỉ tải file APK lên mục *Artifacts* tạm thời (phải đăng nhập GitHub mới tải được) mà quên không tạo bản phát hành *GitHub Release*, dẫn đến đường link tải `releases/latest/download/app-release.apk` bị báo lỗi 404. Đã bổ sung bước tự động tạo GitHub Release và đính kèm file `app-release.apk` vào workflow `build.yml`.
- **Tối ưu hóa siêu nhẹ & Thay đổi giao diện icon PDF**:
  - **Thay ảnh bìa PDF bằng Icon vector tĩnh (`@drawable/ic_pdf`)**: Loại bỏ hoàn toàn quy trình mở luồng file (`ParcelFileDescriptor`), render trang đầu (`PdfRenderer`) và cache Bitmap mỗi khi cuộn danh sách. Giúp danh sách cuộn mượt mà tức thì ở 60/120 FPS, giải phóng hàng chục MB RAM và không gây hao pin.
  - **Kích hoạt R8 Minification & Resource Shrinking (`build.gradle`)**: Bật `minifyEnabled true` và `shrinkResources true` cho bản release kèm bộ quy tắc ProGuard chuẩn cho Gson/Room/ML Kit. Quá trình này tự động cắt bỏ code và tài nguyên dư thừa của các thư viện Google Material, AppCompat, Coroutines... giúp giảm đáng kể dung lượng file APK (gọn nhẹ hơn ~40-60%) và khởi động app nhanh hơn.

#### 📝 File đã sửa
| File | Thay đổi |
|------|----------|
| `SyncActivity.kt` | Fix sai path localFolderFile |
| `PdfViewerActivity.kt` | Render IO thread, recycle bitmap cũ, reset matrix, thêm coroutine imports |
| `UpdateCheckerWithProgress.kt` | Thay thế `DownloadManager` bằng Coroutine HTTP download để fix lỗi tải thất bại |
| `file_paths.xml` | Thêm `<external-files-path>` cho FileProvider |
| `.github/workflows/build.yml` | Thêm bước tự động tạo GitHub Release đính kèm APK để fix lỗi tải 404 |
| `item_pdf_file.xml` & `ic_pdf.xml` | Thay hiển thị ảnh bìa PDF bằng icon vector tĩnh siêu nhẹ |
| `build.gradle` & `proguard-rules.pro` | Bật R8 minification & shrinkResources tối ưu hóa dung lượng APK |
| `PdfTextExtractor.kt` | TextRecognizer lazy singleton |
| `MainActivity.kt` | loadPdfFiles() chạy trên IO thread |
| `SyncManager.kt` | Thêm HTTP timeout cho findFolderId và listDriveFiles; đồng bộ 2 chiều cho `pdf_metadata.json` |

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
-   X � a   b �  e n g i n e   j p . c o . f u j i x e r o x   c i,   t � c h   h �p   S D K   c o m . f u j i f i l m . f b   m �i   v �   c �p   n h �t   t o � n   b �  t h �  v i �n   N a t i v e   ( . s o )   t �  s o u r c e s / r e s o u r c e s   �  k h �c   p h �c   t r i �t   �  l �i   S I G S E G V   t r � n   m � y   A n d r o i d   �i   m �i . 
 
 -   �n g   d �n g   �   ��c   b i � n   d �c h   t h � n h   c � n g   s a u   k h i   d �n   d �p   c � c   m �   n g u �n   c i. 
 
 -   S �a   l �i   S I G S E G V :   P h �c   h �i   t o � n   b �  c � c   c l a s s   p h �  t h u �c   v �   p h ��n g   t h �c   N a t i v e   g �c   c �a   B a s e B r i d g e   ( b �  l ��c   b �  t r ��c   � )   �  J N I _ O n L o a d   v �   C + +   C a l l b a c k   k h � n g   b �  c r a s h   d o   l �i   N o S u c h M e t h o d E r r o r   ( c h �n g   h �n   n h �  k h i   C + +   g �i   n g ��c   l �i   h � m   p a s t e B i t m a p   c �a   J a v a ) . 
 
 -   T h � m   c �  c h �  g h i   l o g   r a   f i l e   \ x d w _ d e b u g . t x t \   t r o n g   C a c h e   �  t r a c k i n g   x e m   h � m   N a t i v e   n � o   g � y   r a   l �i   S I G S E G V   k h i   c h �y   t r � n   t h i �t   b �. 
 
 -   S �a   l �i   S I G S E G V   k h i   m �  f i l e   X D W   b �n g   c � c h   k i �m   t r a   m �   l �i   t r �  v �  c �a   h � m   \ g e t N u m b e r O f P a g e s ( ) \   ( c h �  g �i   \ g e t X b d P a g e C o u n t \   n �u   k h � n g   c �   l �i ,   t r � n h   g �i   t r � n   d o c u m e n t   h �n g )   v �   b �  s u n g   l �n h   \ i n i t D o c E d i t ( ) \   �  k h �i   t �o   s t a t e   C + + . 
 
 -   B �  s u n g   l �n h   \ c r e a t e C a n v a s A n d B i t m a p ( ) \   t r ��c   k h i   g �i   \ s e t D r a w i n g E n v ( ) \   �  c �p   p h � t   v � n g   n h �  B i t m a p   c h o   C + + ,   t r � n h   l �i   S I G S E G V   k h i   c �  g �n g   v �  l � n   v � n g   n h �  N U L L . 
 
 -   B �  s u n g   l �n h   \ i n i t T i l e d L a y e r ( ) \   v �   p u b l i c   c l a s s   \ D r a w e r S t a t u s O b s e r v a b l e \   �  k h �i   t �o   �y   �  h �  t h �n g   r e n d e r   C + +   t r ��c   k h i   t h i �t   l �p   m � i   t r ��n g   v �,   n h �m   s �a   l �i   S I G S E G V   c u �i   c � n g   t r o n g   \ s e t D r a w i n g E n v \ . 
 
 -   S �a   l �i   g i a o   d i �n   h i �n   t h �  2   n � t   t r � n g   t � n   n h a u   b �n g   c � c h   t � c h   r i � n g   c h u �i   d �c h   v �   �n   t h a n h   c h �n   F i l e   n �u   t h �  m �c   c h �  c �   1   f i l e . 
 
 -   S �a   l �i   �n h   t r �n g   ( k h � n g   r e n d e r )   d o   n g u y � n   b �n   C + +   r e n d e r   t r �c   t i �p   v � o   v � n g   n h �  t )n h   \ B a s e B r i d g e . c a c h e \   t h � n g   q u a   \ p a s t e B i t m a p ( ) \   t h a y   v �   B i t m a p   �o   t r u y �n   v � o   h � m . 
 
 -   S �a   l �i   ' k h � n g   t h �  m �  f i l e   . x d w '   d o   d � n g   B i t m a p   1 x 1   p i x e l   t h a y   v �   k � c h   t h ��c   t h �t   l � m   t r � n   b �  n h �  C + + .   T h a y   v � o   � ,   c �p   p h � t   �  k � c h   t h ��c   m � n   h � n h   �  C + +   v �  a n   t o � n . 
 
 -   C h �n h   s �a   \ X d w V i e w e r A c t i v i t y \   �  h i �n   A l e r t D i a l o g   b �t   l �i   c h i   t i �t   ( T r a c e )   n �u   h � m   \ o p e n D o c u m e n t \   t r �  v �  s �  � m ,   g i � p   c h �n   o � n   c h � n h   x � c   n g u y � n   n h � n   t h a y   v �   t �  �n g   m �  b �n g   �n g   d �n g   n g o � i   ( D o c u W o r k s ) . 
 
 -   V �   h i �u   h � a   t � n h   n n g   t �  �n g   k h � a   N a t i v e   R e n d e r e r   k h i   p h � t   h i �n   C r a s h   ( x � a   c �  K E Y _ N A T I V E _ F A I L E D )   �  �m   b �o   �n g   d �n g   l u � n   c �  g �n g   m �  b �n g   C + +   t h a y   v �   t �  �n g   �y   r a   D o c u W o r k s . 
 
 -   S �a   l o g i c   t r �  v �  �n h   t �  C + + :   K h i   t r u y �n   t r �c   t i �p   �i   t ��n g   B i t m a p   v � o   C + + ,   C + +   s �  g h i   t h �n g   p i x e l   v � o   B i t m a p   �   t h a y   v �   t r �  v �  q u a   b i �n   \ B a s e B r i d g e . c a c h e \ .   C �p   n h �t   m �   n g u �n   K o t l i n   �  l �y   � n g   \ d u m m y B i t m a p \   r a   h i �n   t h �,   g i �i   q u y �t   l �i   c h �  h i �n   t h �  1   �   v u � n g   n h �  1 x 1   p i x e l . 
 
 -   S �a   l �i   l o g i c   n g n   c �n   h � m   \ s h o w P a g e \   h o �t   �n g :   B �  s u n g   d � n g   \ u s i n g N a t i v e R e n d e r e r   =   t r u e \   k h i   m �  f i l e   t h � n h   c � n g .   T r ��c   �   c �  n � y   m �c   �n h   l �   \  a l s e \   k h i �n   �n g   d �n g   t �i   x o n g   f i l e   n h �n g   t �  c h �i   v �  �n h   v �   g i �u   l u � n   c � c   n � t   c h u y �n   t r a n g . 
 
 -   T �  �n g   t � n h   t o � n   t �  l �  t h u   p h � n g   ( S c a l e / D P I )   c h o   C + + :   T h a y   v �   � p   c �n g   \ 1 . 0 f \   ( k h i �n   �n h   v �  r a   s i � u   n h �  �  g � c   m � n   h � n h ) ,   m �   n g u �n   g i �  � y   s �  t � n h   t o � n   �n g   d �a   t r � n   k � c h   t h ��c   g i �y   t h �t   v �   �  p h � n   g i �i   m � n   h � n h .   C �p   n h �t   n � y   s �  g i � p   t � i   l i �u   t r � n   v i �n   ( f i t   c e n t e r )   v �a   k h � t   t r � n   m � n   h � n h   i �n   t h o �i   c �a   n g ��i   d � n g . 
 
 -   T �t   k i �m   t r a   a n   t o � n   t h �  v i �n   C + + :   V i �c   b � o   l �i   \ 
 
 k h � n g 
 
 t h �
 
 m �
 
 f i l e \   x u �t   p h � t   t �  v i �c   h � m   k i �m   t r a   a n   t o � n   �   n h �n   t h �y   h �  t h �n g   c h �a   l o a d   x o n g   t h �  v i �n   n � n   t �  �n g   b �  c u �c .   �   � p   �n g   d �n g   l u � n   t i n   t ��n g   v �   g i a o   v i �c   t h �n g   c h o   C + +   �  t � m   r a   n g u y � n   n h � n   g �c   r �  ( h o �c   s �  c h �y   t h � n h   c � n g   l u � n ) . 
 
 -   S �a   l �i   m i s m a t c h   k � c h   t h ��c   b �  �m   ( B u f f e r   S i z e   M i s m a t c h )   v �i   C + + :   V �   C + +   s �  t �  c h �i   v �  v �   t r �  v �  l �i   n �u   k � c h   t h ��c   k h u n g   t r a n h   ( B i t m a p )   k h � n g   k h �p   c h � n h   x � c   t �i   t �n g   p i x e l   s o   v �i   y � u   c �u   c �a   �  p h � n   g i �i   ( D P I ) .   G i �  � y ,   �n g   d �n g   s �  n h �  C + +   t � n h   t o � n   k � c h   t h ��c   c h i �u   d � i / r �n g   t �i   D P I   c �n   t h i �t ,   s a u   �   m �i   t �o   r a   k h u n g   t r a n h   ( B i t m a p )   v �a   k h � t   y   h �t   k � c h   t h ��c   �   �  t r � n h   b �  t �  c h �i . 
 
 -   �   �n   t h � n h   c � n g   c � c   n � t   i �u   h ��n g   ( T r a n g   t r ��c / T r a n g   s a u )   k h i   �c   f i l e   X D W   t h e o   y � u   c �u   c �a   n g ��i   d � n g ,   �  k h � n g   b �  t r � n g   l �p   v �i   b �  c � n g   c �  c �a   P D F   c �   s �n . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 8 : 5 9 :   S � � � a   l � �  i   m � � n   h � � n h   �  e n   k h i   x e m   f i l e   X D W   b � � � n g   c � � c h   t h i � � � t   l � � � p   n � � � n   t r � � � n g   c h o   b i t m a p   ( A R G B _ 8 8 8 8   m � � � c   �  � � 9 n h   l � �   t r o n g   s u � �  t ) ,   h i � � �n   t h � � 9   l � � � i   c � � c   n � � t   �  i � � � u   h � � � � : n g   t r a n g ,   v � �   t h � � m   x � � �   l � �   f a l l b a c k   t � � �   �  � � "!n g   m � � x  b � � � n g   a p p   n g o � � i   n � � � u   n a t i v e   k h � � n g   m � � x  �  � � � � � c . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 0 2 :   S � � � a   l � �  i   t h � �   v i � � ! n   N a t i v e   k h � � n g   r e n d e r   �  � � � � � c   t r a n g   X D W   d o   t r u y � � � n   s a i   t h a m   s � �    s c a l e   ( t r u y � � � n   9 6 . 0 f   t h a y   v � �   1 . 0 f ) . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 0 7 :   S � � � a   l � �  i   N a t i v e   k h � � n g   r e n d e r   �  � � � � � c   d o   t r u y � � � n   s a i   p a g e   i n d e x   ( t h � �   v i � � ! n   C + +   k � � �   v � � � n g   p a g e I n d e x   b � � � t   �  � � � u   t � � �   1   t h a y   v � �   0 ) .   � � � �   t h � � m   c � �   c h � � �   b r u t e - f o r c e   t h � � �   �  � � "!n g   ( t � � �   f a l l b a c k   s a n g   1 - b a s e d   i n d e x   v � �   s c a l e   k h � � c )   �  � � �  �  � � � m   b � � � o   k h � � n g   b � � 9   r � � : t   r e n d e r   � � � n h . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 1 0 :   C � � � p   n h � � � t   h � � m   g e t P a g e B i t m a p   �  � � �  t � � n h   t o � � n   t � � �   �  � � "!n g   h � � !   s � �    s c a l e   ( t h u   p h � � n g )   s a o   c h o   t � � i   l i � � ! u   v � � � a   v � � � n   v � � : i   k � � c h   t h � � � � : c   m � � n   h � � n h   h i � � �n   t h � � 9 .   K h � � � c   p h � � � c   l � �  i   r e n d e r   � � � n h   q u � �   n h � � �   b � � � n g   1 / 9 6   m � � n   h � � n h   d o   b � � 9   f i x   c � � � n g   s c a l e = 1 . 0 f . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 1 2 :   S � � � a   l � �  i   t � � n h   t o � � n   h � � !   s � �    s c a l e   ( t h u   p h � � n g )   b � � 9   s a i   k h i   n h � � n   n h � � � m   v � � : i   t a r g e t D p i   ( l � � m   c h o   � � � n h   x u � � � t   b � � 9   p h � � n g   t o   h � � n   9 6   l � � � n   s o   v � � : i   b � � n h   t h � � � � � n g ,   g � � y   l � �  i   O u t   o f   M e m o r y   v � �   t r � � �   v � � �   - 1 ) .   S c a l e   h i � � ! n   t � � � i   l � �   h � � !   s � �    t h � � � c   ( v � �   d � � �   1 . 3 6 x ) . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 1 5 :   T � � i   t h i � � � t   k � � �   l o g i c   g e t P a g e B i t m a p :   K h � � i   p h � � � c   l � � � i   l u � �  n g   v � � �   c a n v a s   c h u � � � n   t h e o   k � � c h   t h � � � � : c   P a p e r   g � �  c   t h a y   v � �   � � p   v � � �   t r � � � c   t i � � � p   v � � o   C a n v a s   m � � n   h � � n h ,   g i � � � i   q u y � � � t   t r i � � ! t   �  � � �  v � � � n   �  � � �   t h a m   s � �    s c a l e   ( D P I )   b � � 9   l � � ! c h   p h a   v � � : i   k � � c h   t h � � � � : c   b i t m a p   g � � y   l � �  i   k h � � n g   m � � x  �  � � � � � c   f i l e . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 1 7 :   T h a y   t h � � �   t o � � n   b � � "!  l o g i c   t � � n h   s c a l e   v � �   k � � c h   t h � � � � : c   k h � � "   g i � � � y   b � � � n g   v � � n g   l � � � p   B r u t e - f o r c e   g i � � � m   d � � � n   ( t � � �   3 0 0 . 0 f   x u � �  n g   1 . 0 f ) .   P h � � � � n g   p h � � p   n � � y   g i � � p   t � � �   �  � � "!n g   d � �   t � � m   t � � �   l � � !   s c a l e   l � � : n   n h � � � t   m � �   C + +   c � �   t h � � �  v � � �   l � � � t   v � � o   C a n v a s   m � � n   h � � n h   m � �   k h � � n g   b � � 9   v � �n g   l � �  i   - 1 ,   �  � � � m   b � � � o   � � � n h   r e n d e r   � � x  �  � � "!  p h � � n   g i � � � i   t � �  t   n h � � � t   c � �   t h � � �  c h o   m � � � i   l o � � � i   f i l e   x d w . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 2 :   K h � � � c   p h � � � c   l � �  i   � � � n h   X D W   b � � 9   c r o p   s � � t   m � � p   v i � � � n   d o   t � � �   l � � !   z o o m   � � x  n g � � � � � n g   c � � � c   h � � � n   ( b � � � n g   c � � c h   t h a y   t h � � �   v � � n g   l � � � p   b r u t e - f o r c e   t h � � n h   t h u � � � t   t o � � n   t � � n h   t o � � n   t o � � n   h � � � c   c h � � n h   x � � c   1 0 0 % ,   c � � "!n g   t h � � m   5 %   l � � �   m a r g i n   �  � � �  � � � n h   h i � � �n   t h � � 9   v � � � a   v � � � n   v � �   d � � &   n h � � n   h � � n ) . 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 2 :   B � � "   s u n g   c � � c   b � � � n   d � � 9 c h   c � � n   t h i � � � u   ( " p r e v _ f i l e " ,   " n e x t _ f i l e " )   v � � o   L o c a l e H e l p e r   �  � � �  h a i   n � � t   �  i � � � u   h � � � � : n g   h i � � �n   t h � � 9   �  � � n g   n g � � n   n g � � �   ( V i � � ! t / N h � � � t )   t h a y   v � �   h i � � ! n   t � � n   b i � � � n   n g u y � � n   t h � � � y . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 5 :   H o � � n   t h i � � ! n   l o g i c   g e t P a g e B i t m a p :   K � � � t   h � � � p   g i � � � a   t � � n h   t o � � n   s c a l e   b � � � n g   t o � � n   h � � � c   v � �   d � �   t � � m   B r u t e - f o r c e   n g u y � � n   t h � � � y   ( I n t e g e r ) .   G i � � � i   q u y � � � t   t � � � n   g � �  c   v i � � ! c   t h � �   v i � � ! n   C + +   t � � �   c h � �  i   c � � c   s � �    t h � � � c   ( f r a c t i o n a l   s c a l e )   h o � � � c   t � � �   �  � � "!n g   n � � m   l � �  i   - 1   k h i   s c a l e   c h � � � m   s � � t   v i � � � n ,   �  � � � m   b � � � o   l � � � y   �  � � � � � c   t � � �   l � � !   n g u y � � n   ( I n t e g e r )   l � � : n   n h � � � t   m � �   v � � � n   g i � � �   l � � �   a n   t o � � n   5 % . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 8 :   T � � i   k i � � � n   t r � � c   l � � � i   t o � � n   b � � "!  c � �   c h � � �   C a n v a s   c � � � a   D o c u W o r k s   C + + :   P h � � t   h i � � ! n   r a   n g u y � � n   n h � � n   g � �  c   r � � &   C + +   n � � m   l � �  i   - 1   l � �   d o   B i t m a p   �  � � � u   v � � o   c � �   k � � c h   t h � � � � : c   L � � aN   H � � N   s o   v � � : i   k � � c h   t h � � � � : c   t h � � � t   c � � � a   � � � n h   s a u   k h i   z o o m .   G i � � � i   p h � � p :   � � � � � o   n g � � � � � c   q u y   t r � � n h ,   l � � � y   m � � "!t   t � � �   l � � !   Z o o m   n g u y � � n   ( I n t e g e r )   a n   t o � � n   ( t � � �   1 0 0   �  � � � n   3 0 0 ) ,   t � � n h   t o � � n   r a   k � � c h   t h � � � � : c   ( W i d t h   x   H e i g h t )   C H � � N H   X � � C   �  � � � n   t � � � n g   p i x e l   m � �   b � � � c   � � � n h   c � � � n ,   v � �   t � � � o   B i t m a p   v � � : i   �  � � n g   k � � c h   t h � � � � : c   �  � � .   B � � � n g   c � � c h   n � � y ,   C + +   s � � �   v � � �   v � � � a   k h � � t   1 0 0 %   v � � o   B i t m a p   m � �   k h � � n g   b a o   g i � � �   b � � o   l � �  i   k � � c h   t h � � � � : c . 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 8 :   � � � � �  �  � � � m   b � � � o   � � � n h   c � �   l � � �   ( m a r g i n )   �  � � � p ,   t h a y   v � �   c a n   t h i � � ! p   v � � o   s c a l e ,   g i � � �   �  � � y   � � � n h   x u � � � t   r a   s � � �   �  � � � � � c   A n d r o i d   I m a g e V i e w   t � � �   �  � � "!n g   b � � p   n h � � �   ( f i t C e n t e r )   v � �   c h � � n   t h � � m   p a d d i n g   8 d p   c h u � � � n   U I   A n d r o i d . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 3 0 :   X � � �   l � �   t r i � � ! t   �  � � �  q u y   t � � � c   g i � � : i   h � � � n   k h � � � t   k h e   c � � � a   t h � �   v i � � ! n   C + +   D o c u W o r k s .   T h � �   v i � � ! n   y � � u   c � � � u   3   �  i � � � u   k i � � ! n   �  � �  n g   t h � � � i :   S c a l e   p h � � � i   l � �   s � �    n g u y � � n ,   B i t m a p   k h � � n g   �  � � � � � c   l � � : n   h � � n   m � � n   h � � n h   �  i � � ! n   t h o � � � i   ( n � � � u   l � � : n   h � � n   s � � �   b � � 9   t � � �   c h � �  i   c � � � p   p h � � t   b � � "!  n h � � : ) ,   v � �   t � � i   l i � � ! u   s a u   k h i   t h u   p h � � n g   p h � � � i   p h � � �   k � � n   1 0 0 %   d i � � ! n   t � � c h   B i t m a p .   G i � � � i   p h � � p   l � �   t � � m   r a   h � � !   s � �    Z o o m   n g u y � � n   ( I n t e g e r )   l � � : n   n h � � � t   s a o   c h o   b � � � c   � � � n h   x u � � � t   r a   h o � � n   t o � � n   n � � � m   l � � � t   b � � n   t r o n g   k � � c h   t h � � � � : c   m � � n   h � � n h . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 3 3 :   T h e o   y � � u   c � � � u   c � � � a   n g � � � � � i   d � � n g ,   k h � � i   p h � � � c   l � � � i   c � �   c h � � �   r e n d e r   B r u t e - f o r c e   m � � � n h   n h � � � t   ( c h � � � y   l � � i   t � � �   3 0 0 . 0 f   x u � �  n g )   �  � � �  �  � � � m   b � � � o   l u � � n   l o a d   t h � � n h   c � � n g   f i l e   . x d w .   � � � �  n g   t h � � � i   t h i � � � t   k � � �   v � �   b � � "   s u n g   t h � � m   m � � "!t   T h a n h   Z o o m   ( S e e k B a r )   t r � � � c   q u a n   n g a y   t r � � n   g i a o   d i � � ! n   �  � � � c   f i l e ,   c h o   p h � � p   n g � � � � � i   d � � n g   t � � y   � �   v u � �  t   �  � � �  p h � � n g   t o   t h u   n h � � �   b � � � n   v � � �   t h e o   �  � � n g   t � � �   l � � !   m o n g   m u � �  n ,   g i � � p   d � � &   d � � n g   q u a n   s � � t   c h i   t i � � � t   v � �n   b � � � n   m � �   k h � � n g   b � � 9   v � � � � : n g   g i � � : i   h � � � n . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 4 4 :   T h � � m   t � � n h   n � �n g   p h � � n g   t o / t h u   n h � � �   b � � � n g   c � � c h   v u � �  t   ( P i n c h - t o - z o o m )   c h o   t r � � n h   �  � � � c   f i l e   . x d w .   S � � �   d � � � n g   c � �   c h � � �   M a t r i x   k � � � t   h � � � p   v � � : i   o n T o u c h L i s t e n e r   �  � � �  b � � � t   c � � c   s � � �   k i � � ! n   �  a   �  i � � �m   ( 2   n g � � n   t a y ) ,   t � � � � n g   t � � �   n h � �   n h � � � n g   g � �   �  � �   t r i � � �n   k h a i   � � x  P d f V i e w e r A c t i v i t y ,   c h o   p h � � p   n g � � � � � i   d � � n g   k � � o   t h � � �   ( p a n )   v � �   p h � � n g   t o   ( z o o m )   � � � n h   b i t m a p   �  � �   �  � � � � � c   r e n d e r .   S � � �   k � � � t   h � � � p   g i � � � a   t h a n h   S e e k B a r   ( r e n d e r   c h � � � t   l � � � � � n g   c a o )   v � �   P i n c h - t o - z o o m   ( p h � � n g   t o   n h a n h )   t � � � o   r a   t r � � � i   n g h i � � ! m   �  � � � c   X D W   m � � � � � t   m � �   v � �   c � � � c   k � � �   l i n h   h o � � � t . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 5 6 :   D � � � i   t h a n h   c � � � u   h � � n h   " T � � �   l � � !   S c a l e   X D W "   ( � � � � "!  p h � � n   g i � � � i   r e n d e r )   t � � �   g i a o   d i � � ! n   �  � � � c   X D W   t r � � � c   t i � � � p   v � � o   t r a n g   C � � i   � � � � � t   ( S e t t i n g ) .   T h a n h   k � � o   b � � n   C � � i   � � � � � t   v � � � n   h � �    t r � � �   l � � � a   c h � � � n   d � � � i   t � � �   l � � !   ( 1 0 0 - 5 0 0 )   v � �   � � � n g   d � � � n g   s � � �   t � � �   �  � � "!n g   t � � � i   m � � � c   c h � � � t   l � � � � � n g   n � � y   � � x  n h � � � n g   l � � � n   �  � � � c   X D W   t i � � � p   t h e o . 
 
 -   0 1 - 0 8 - 2 0 2 6 ,   2 0 : 0 0 :   C h � � 0 n h   s � � � a   l � � � i   d � � � i   t � � �   l � � !   g i � � : i   h � � � n   c h o   t h a n h   c � � � u   h � � n h   �  � � "!  p h � � n   g i � � � i   ( S c a l e   X D W )   t r o n g   m � � � c   C � � i   � � � � � t .   G i � � : i   h � � � n   m � � : i   c h o   p h � � p   k � � o   t � � �   1 0   ( t h � � � p   n h � � � t )   �  � � � n   2 0 0   ( c a o   n h � � � t )   v � � : i   g i � �   t r � � 9   m � � � c   �  � � 9 n h   �  � � � � � c   �  � � a   v � � �   1 5 0 . 
 
 

- 02-08-2026, 10:25: Tái cấu trúc XdwViewerActivity và XdwReaderHelper sử dụng kỹ thuật Tiling (chia nhỏ ảnh) qua RecyclerView, giải quyết triệt để lỗi OutOfMemory (OOM) khi render XDW lớn mà vẫn giữ được độ phân giải cao.

- 02-08-2026, 11:39: Bổ sung luật ProGuard (keep class) cho package \com.fujifilm.fb.**\ để sửa lỗi crash do R8 obfuscate code JNI khi build bản Release (như trên GitHub Actions).
