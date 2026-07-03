# Workspace Rules

- BẤT CỨ KHI NÀO có thay đổi về mã nguồn (code) hoặc giao diện (UI), phải cập nhật file `HISTORY.md` để ghi chú lại các thay đổi đó trước khi báo cáo hoàn thành cho người dùng.
- khi cập nhật file `history.md` thì sau mỗi gạch đầu dòng sẽ hiển thị ngày tháng năm,giờ, phút rồi mới tới nội dung thay đổi 

1. Nguyên tắc tổ chức mã nguồn (Code Organization)
- Xóa bỏ trùng lặp (No Redundancy): Chủ động rà soát, xóa bỏ các hàm, biến hoặc logic bị ghi đè/trùng lặp. - Khi thực hiện xóa, phải có một phần giải thích ngắn gọn lý do ở phần thuyết minh.

- Cấu trúc phân tầng rõ ràng: * Các thư viện import/require đặt ở đầu file.

- Các hằng số (constants) và cấu hình toàn cục (global configs).

- Các luồng xử lý chính/Hàm khởi tạo.

- Các hàm bổ trợ (helpers) hoặc các định nghĩa Model/Schema (thường gom cụm gọn gàng ở cuối file hoặc tách biệt để tránh xung đột giao diện/designer).
2. Tư duy thiết kế và Thực thi (Architecture & Execution)
- Tiếp cận từng bước (Step-by-step)
- Chặt chẽ & An toàn: Luôn kiểm tra biên (boundary checks), kiểm tra dữ liệu đầu vào (input validation), xử lý ngoại lệ (try-catch) và bẫy lỗi đầy đủ để hệ thống không bị crash hoặc tràn bộ nhớ.

- Tách biệt nghiệp vụ (Separation of Concerns): Tách biệt rõ ràng giữa logic xử lý dữ liệu (backend/core) và giao diện hiển thị (UI/frontend). Ưu tiên thiết kế dạng mô-đun để dễ bảo trì và mở rộng.
3. Quy chuẩn viết mã và Chú thích (Clean Code & Comments)
- Đặt tên có ý nghĩa: Tên biến, hàm, lớp phải tường minh, phản ánh đúng chức năng (ví dụ: dùng total_tokens thay vì t, duration_ms thay vì d).

- Chú thích thông minh (Meaningful Comments):

- Chú thích ở đầu các hàm phức tạp để giải thích mục đích và đầu vào/đầu ra.

- Chú thích inline (trong dòng) ở các đoạn xử lý thuật toán khó hiểu hoặc các quyết định kỹ thuật đặc biệt.

- Không chú thích những điều quá hiển nhiên.

- Định dạng nhất quán: Sử dụng thụt lề (indentation) chuẩn, khoảng trắng hợp lý giữa các khối lệnh để tối ưu hóa khả năng đọc lướt (scannability).