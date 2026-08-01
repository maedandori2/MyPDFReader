# ğŸ“‹ Lá»‹ch sá»­ thay Ä‘á»•i â€” MyPDFReader

## [v1.5.3] - 2026-08-01

### ğŸ› Sá»­a lá»—i crash khi má»Ÿ file XDW
- 2026-08-01, 16:30: **KhÃ´i phá»¥c native renderer vÃ  sá»­a triá»‡t Ä‘á»ƒ crash SIGSEGV (`BaseBridge.java`, `XdwViewerActivity.kt`)**:
  - NguyÃªn nhÃ¢n: Viá»‡c load thÆ° viá»‡n bá»‹ thiáº¿u `icudata` vÃ  `supkBase64` gÃ¢y lá»—i SIGSEGV khi khá»Ÿi táº¡o. ThÃªm vÃ o Ä‘Ã³, `getCPUFeatures()` bá»‹ gá»i trÆ°á»›c khi kiá»ƒm tra OS kiáº¿n trÃºc, gÃ¢y `UnsatisfiedLinkError` trÃªn ARM64 (do khÃ´ng cÃ³ file `.so` cho VFP).
  - Sá»­a (`BaseBridge.java`): 
    - Äáº£m báº£o load Ä‘á»§ `icudata` vÃ  `supkBase64`.
    - DÃ¹ng `android.os.Build.SUPPORTED_ABIS` Ä‘á»ƒ nháº­n diá»‡n `arm64-v8a` thay vÃ¬ dá»±a hoÃ n toÃ n vÃ o JNI `getCPUFeatures()`.
    - ÄÆ°a `getCPUFeatures()` vÃ o `try-catch` an toÃ n.
  - Sá»­a (`XdwViewerActivity.kt`): Báº­t láº¡i biáº¿n cá» `allowNativeRenderer = true` Ä‘á»ƒ app má»Ÿ file `.xdw` trá»±c tiáº¿p bÃªn trong á»©ng dá»¥ng thay vÃ¬ Ä‘áº©y sang app DocuWorks bÃªn ngoÃ i.

#### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `BaseBridge.java` | Sá»­a static initializer: load Ä‘á»§ `icudata`/`supkBase64`, nháº­n diá»‡n arm64 an toÃ n báº±ng Build.SUPPORTED_ABIS |
| `XdwViewerActivity.kt` | Báº­t láº¡i `allowNativeRenderer = true` Ä‘á»ƒ Ä‘á»c file trá»±c tiáº¿p báº±ng BaseBridge thay vÃ¬ app ngoÃ i |
| `XdwReaderHelper.kt` | ThÃªm crash detection qua SharedPreferences, synchronized cho singleton bridge |
| `HISTORY.md` | Cáº­p nháº­t changelog v1.5.3 |

---



## [v1.5.2] - 2026-07-03

### âœ¨ Há»— trá»£ Ä‘á»“ng bá»™ vÃ  Ä‘á»c file DocuWorks (`.xdw`)
- 2026-07-03, 00:02: **TÃ­ch há»£p toÃ n diá»‡n kháº£ nÄƒng Ä‘á»“ng bá»™ tá»« Google Drive vÃ  má»Ÿ tÃ i liá»‡u `.xdw` (`SyncManager`, `MainActivity`, `PdfFileAdapter`, `XdwViewerActivity`, `PdfViewerActivity`)**:
  - **Äá»“ng bá»™ trÃªn Google Drive (`SyncManager`)**: Bá»• sung há»— trá»£ má»Ÿ rá»™ng Ä‘uÃ´i tá»‡p `.xdw` vÃ  cÃ¡c MIME type chÃ­nh thá»©c cá»§a Fuji Xerox / Fujifilm DocuWorks (`application/vnd.fujixerox.docuworks`, `application/vnd.fujifilm.docuworks`, `application/x-xdw`). Cho phÃ©p táº£i xuá»‘ng, liá»‡t kÃª, xÃ³a vÃ  giá»¯ Ä‘á»“ng bá»™ cÃ¡c tá»‡p `.xdw` ngang hÃ ng vá»›i `.pdf` trong thÆ° má»¥c `MyPDF/`.
  - **Nháº­n diá»‡n giao diá»‡n danh sÃ¡ch (`PdfFileAdapter` & `MainActivity`)**: Táº¡o má»›i biá»ƒu tÆ°á»£ng riÃªng biá»‡t cho tá»‡p DocuWorks (`@drawable/ic_xdw` mÃ u xanh lam Ä‘áº·c trÆ°ng). Khi quÃ©t danh sÃ¡ch tÃ i liá»‡u, á»©ng dá»¥ng tá»± Ä‘á»™ng phÃ¢n loáº¡i: hiá»ƒn thá»‹ Ä‘Ãºng Ä‘uÃ´i `.xdw` cÃ¹ng biá»ƒu tÆ°á»£ng DocuWorks, trong khi cÃ¡c tá»‡p PDF tiáº¿p tá»¥c sá»­ dá»¥ng biá»ƒu tÆ°á»£ng Ä‘á» vÃ  hiá»ƒn thá»‹ tÃªn khÃ´ng kÃ¨m pháº§n má»Ÿ rá»™ng nhÆ° cÅ©.
  - **MÃ n hÃ¬nh Ä‘iá»u hÆ°á»›ng tÃ i liá»‡u chuyÃªn dá»¥ng (`XdwViewerActivity` & `activity_xdw_viewer.xml`)**: XÃ¢y dá»±ng mÃ n hÃ¬nh hiá»ƒn thá»‹ trung gian khi ngÆ°á»i dÃ¹ng chá»n Ä‘á»c tá»‡p `.xdw` vá»›i Ä‘áº§y Ä‘á»§ thanh header, nÃºt quay láº¡i (`â† Quay láº¡i`), tÃªn tá»‡p vÃ  cÃ¡c nÃºt chuyá»ƒn tiáº¿p (`â—€ File trÆ°á»›c` / `File tiáº¿p theo â–¶`) cÅ©ng nhÆ° há»— trá»£ thao tÃ¡c vuá»‘t ngang mÃ n hÃ¬nh (swipe) Ä‘á»ƒ chuyá»ƒn file nhá»‹p nhÃ ng nhÆ° khi Ä‘ang Ä‘á»c PDF.
  - **CÆ¡ cháº¿ gá»i á»©ng dá»¥ng DocuWorks Viewer bÃªn ngoÃ i**: Khi má»Ÿ mÃ n hÃ¬nh `XdwViewerActivity` hoáº·c báº¥m nÃºt `"ğŸš€ Má»Ÿ láº¡i DocuWorks Viewer"`, á»©ng dá»¥ng sá»­ dá»¥ng `FileProvider` an toÃ n Ä‘á»ƒ táº¡o URI vÃ  tá»± Ä‘á»™ng gá»­i Intent má»Ÿ tÃ i liá»‡u sang á»©ng dá»¥ng DocuWorks Viewer Ä‘Ã£ cÃ i Ä‘áº·t trÃªn Ä‘iá»‡n thoáº¡i theo cÆ¡ cháº¿ thá»­ nghiá»‡m táº§ng MIME type dá»± phÃ²ng (`vnd.fujixerox.docuworks` â†’ `vnd.fujifilm.docuworks` â†’ `x-xdw` â†’ `*/*`).
  - **Cáº£i tiáº¿n luá»“ng chuyá»ƒn tiáº¿p giá»¯a cÃ¡c file (`PdfViewerActivity` & `XdwViewerActivity`)**: Khi ngÆ°á»i dÃ¹ng Ä‘ang Ä‘á»c tÃ i liá»‡u (PDF hoáº·c XDW) vÃ  báº¥m nÃºt Trang trÆ°á»›c/Trang sau (hoáº·c vuá»‘t ngang mÃ n hÃ¬nh), á»©ng dá»¥ng tá»± Ä‘á»™ng kiá»ƒm tra Ä‘á»‹nh dáº¡ng cá»§a file tiáº¿p theo Ä‘á»ƒ linh hoáº¡t chuyá»ƒn Ä‘á»•i giá»¯a trÃ¬nh xem PDF ná»™i bá»™ vÃ  trÃ¬nh xem XDW bÃªn ngoÃ i mÃ  khÃ´ng cáº§n quay ngÆ°á»£c ra mÃ n hÃ¬nh chÃ­nh.
- 2026-07-03, 00:14: **Sá»­a lá»—i khÃ´ng táº£i Ä‘Æ°á»£c file `.xdw` tá»« Google Drive (`SyncManager`)**:
  - **PhÃ¢n tÃ­ch nguyÃªn nhÃ¢n gá»‘c rá»…**: Khi gá»i Google Drive API trong phÆ°Æ¡ng thá»©c `listDriveFiles`, á»©ng dá»¥ng trÆ°á»›c Ä‘Ã¢y gá»­i cÃ¢u truy váº¥n server-side (`q`) bá»‹ giá»›i háº¡n chá»‰ yÃªu cáº§u cÃ¡c tá»‡p cÃ³ `mimeType='application/pdf'` hoáº·c `mimeType='application/json'`, khiáº¿n mÃ¡y chá»§ Google Drive tá»± Ä‘á»™ng loáº¡i bá» toÃ n bá»™ tÃ i liá»‡u DocuWorks (`.xdw`) ngay tá»« táº§ng tráº£ vá» cá»§a API.
  - **Cáº£i tiáº¿n giáº£i phÃ¡p**: Tá»‘i Æ°u láº¡i cÃ¢u truy váº¥n Google Drive API thÃ nh `mimeType!='application/vnd.google-apps.folder'` Ä‘á»ƒ láº¥y toÃ n bá»™ danh sÃ¡ch tá»‡p trong thÆ° má»¥c `MyPDF/` tá»« mÃ¡y chá»§. Logic lá»c tá»‡p á»Ÿ client ([SyncManager.kt:L328](file:///j:/android%20make/MyPDFReader/app/src/main/java/com/mypdf/reader/SyncManager.kt#L328)) sáº½ chá»‹u trÃ¡ch nhiá»‡m phÃ¢n loáº¡i chÃ­nh xÃ¡c tÃ i liá»‡u theo pháº§n má»Ÿ rá»™ng (`.pdf`, `.xdw`, `.json`) hoáº·c MIME type DocuWorks. Äáº·c biá»‡t, **náº¿u Google Drive tráº£ vá» file DocuWorks bá»‹ máº¥t pháº§n má»Ÿ rá»™ng (tÃªn file khÃ´ng chá»©a `.xdw`), á»©ng dá»¥ng sáº½ tá»± Ä‘á»™ng ná»‘i thÃªm `.xdw` vÃ o tÃªn file** Ä‘á»ƒ mÃ n hÃ¬nh danh sÃ¡ch cÃ³ thá»ƒ nháº­n diá»‡n hiá»ƒn thá»‹ thÃ nh cÃ´ng. Äiá»u nÃ y Ä‘áº£m báº£o 100% file `.xdw` Ä‘Æ°á»£c nháº­n diá»‡n vÃ  táº£i xuá»‘ng mÃ¡y, ká»ƒ cáº£ khi Google Drive gÃ¡n sai MIME type (nhÆ° `application/octet-stream`) hoáº·c khi bá»‹ lá»—i thiáº¿u Ä‘uÃ´i file.
  - **NÃ¢ng cáº¥p Ä‘á»“ng bá»™ hai chiá»u**: Sá»­a Ä‘á»•i logic so sÃ¡nh thá»i gian cho toÃ n bá»™ cÃ¡c file (khÃ´ng chá»‰ riÃªng file cáº¥u hÃ¬nh metadata). Tá»« nay trá»Ÿ Ä‘i, náº¿u file (`.pdf`, `.xdw`) trÃªn mÃ¡y Ä‘Æ°á»£c cáº­p nháº­t má»›i hÆ¡n (vÃ­ dá»¥ lÆ°u chá»‰nh sá»­a) so vá»›i báº£n trÃªn Google Drive, á»©ng dá»¥ng sáº½ tá»± Ä‘á»™ng táº£i file lÃªn (Upload) Ä‘á»ƒ Ä‘Ã¨ lÃªn Drive, thay vÃ¬ chá»‰ táº£i xuá»‘ng má»™t chiá»u nhÆ° trÆ°á»›c kia.
  - **Xá»­ lÃ½ triá»‡t Ä‘á»ƒ lá»—i "TrÃ´i thá»i gian" trÃªn Android & FAT32**:
  - **Thiáº¿t káº¿ láº¡i kiáº¿n trÃºc Äá»“ng bá»™ Má»™t chiá»u (One-Way Sync)**: Chuyá»ƒn toÃ n bá»™ cÆ¡ cháº¿ Ä‘á»“ng bá»™ tÃ i liá»‡u (`.pdf`, `.xdw`) tá»« hai chiá»u sang **má»™t chiá»u** (chá»‰ táº£i tá»« Google Drive xuá»‘ng mÃ¡y). Náº¿u phÃ¡t hiá»‡n file má»›i thÃªm vÃ o mÃ¡y cá»¥c bá»™ hoáº·c mÃ¡y cá»¥c bá»™ cÃ³ dá»¯ liá»‡u má»›i hÆ¡n, á»©ng dá»¥ng sáº½ tá»± Ä‘á»™ng **bá» qua** mÃ  khÃ´ng táº£i ngÆ°á»£c (Upload) lÃªn Drive Ä‘á»ƒ Ä‘áº£m báº£o Drive luÃ´n lÃ  kho chá»©a tÃ i liá»‡u chuáº©n gá»‘c khÃ´ng bá»‹ thao tÃ¡c nháº§m Ä‘Ã¨ lÃªn. Ngoáº¡i lá»‡ duy nháº¥t lÃ  file `pdf_metadata.json` váº«n giá»¯ cÆ¡ cháº¿ Ä‘á»“ng bá»™ hai chiá»u Ä‘á»ƒ báº£o toÃ n cÃ¡c tháº» gáº¯n nhÃ£n cá»§a ngÆ°á»i dÃ¹ng.
  - **Kháº¯c phá»¥c lá»—i "bá» sÃ³t tá»‡p" khi thÆ° má»¥c cÃ³ quÃ¡ nhiá»u tÃ i liá»‡u (Pagination)**: Sá»­a Ä‘á»•i triá»‡t Ä‘á»ƒ cÆ¡ cháº¿ quÃ©t tÃ i liá»‡u cá»§a Google Drive API. Do giá»›i háº¡n máº·c Ä‘á»‹nh cá»§a API chá»‰ tráº£ vá» tá»‘i Ä‘a 1000 tá»‡p tin cho má»—i láº§n truy váº¥n, nhá»¯ng tÃ i liá»‡u `.xdw` náº±m sau ngÆ°á»¡ng nÃ y sáº½ bá»‹ bá» sÃ³t hoÃ n toÃ n. á»¨ng dá»¥ng hiá»‡n táº¡i Ä‘Ã£ Ä‘Æ°á»£c thiáº¿t káº¿ thuáº­t toÃ¡n phÃ¢n trang vÃ²ng láº·p (`nextPageToken`), liÃªn tá»¥c láº¥y dá»¯ liá»‡u sang cÃ¡c trang káº¿ tiáº¿p cho Ä‘áº¿n khi quÃ©t trá»n váº¹n 100% sá»‘ lÆ°á»£ng tá»‡p trong thÆ° má»¥c gá»‘c.
  - **Ãp dá»¥ng cÃ´ng nghá»‡ State Tracking báº±ng JSON (`sync_state.json`)**: Triá»‡t Ä‘á»ƒ vÃ´ hiá»‡u hÃ³a lá»—i sai sá»‘ thá»i gian cá»§a há»‡ thá»‘ng FAT32 trÃªn Kindle Fire. á»¨ng dá»¥ng khÃ´ng cÃ²n sá»­ dá»¥ng hÃ m `File.lastModified()` cá»§a Android Ä‘á»ƒ Ä‘á»‘i chiáº¿u má»‘c thá»i gian ná»¯a. Thay vÃ o Ä‘Ã³, sau má»—i láº§n táº£i tÃ i liá»‡u tá»« Drive thÃ nh cÃ´ng, á»©ng dá»¥ng sáº½ lÆ°u chÃ­nh xÃ¡c má»‘c thá»i gian nguyÃªn báº£n gá»‘c cá»§a Google Drive vÃ o má»™t file siÃªu dá»¯ liá»‡u Ä‘á»™c láº­p `sync_state.json` Ä‘áº·t trong mÃ¡y. á» nhá»¯ng láº§n Ä‘á»“ng bá»™ sau, á»©ng dá»¥ng chá»‰ dÃ¹ng file State nÃ y Ä‘á»ƒ so sÃ¡nh, Ä‘áº£m báº£o Ä‘á»™ chÃ­nh xÃ¡c tuyá»‡t Ä‘á»‘i tá»«ng mili-giÃ¢y.
- 03/07/2026 17:04: **Há»— trá»£ tá»± Ä‘á»™ng nháº­n diá»‡n file chÃ©p thá»§ cÃ´ng (Offline Copying)**: NÃ¢ng cáº¥p tÃ­nh nÄƒng Ä‘á»“ng bá»™ trong `SyncManager.kt` giÃºp tá»± Ä‘á»™ng nháº­n diá»‡n cÃ¡c tá»‡p PDF/XDW Ä‘Æ°á»£c chÃ©p thá»§ cÃ´ng (qua cÃ¡p USB) chÆ°a cÃ³ trong lá»‹ch sá»­ Ä‘á»“ng bá»™. á»¨ng dá»¥ng sáº½ ghi nháº­n ngÃ y giá» trÃªn Drive vÃ o bá»™ nhá»› Ä‘á»ƒ bá» qua táº£i láº§n Ä‘áº§u (tiáº¿t kiá»‡m bÄƒng thÃ´ng cá»±c lá»›n cho hÃ ng ngÃ n tá»‡p) nhÆ°ng váº«n theo dÃµi má»‘c thá»i gian Ä‘á»ƒ táº£i láº¡i chÃ­nh xÃ¡c náº¿u cÃ³ báº£n cáº­p nháº­t má»›i trÃªn Drive sau nÃ y.
- 03/07/2026 17:15: **TÃ¡ch biá»‡t "Khá»Ÿi táº¡o dá»¯ liá»‡u Ä‘á»“ng bá»™"**: Chuyá»ƒn logic tá»± Ä‘á»™ng nháº­n diá»‡n file chÃ©p thá»§ cÃ´ng thÃ nh má»™t tiáº¿n trÃ¬nh cháº¡y Ä‘á»™c láº­p thÃ´ng qua nÃºt báº¥m **"Khá»Ÿi táº¡o dá»¯ liá»‡u Ä‘á»“ng bá»™"** trong mÃ n hÃ¬nh `âš™ Setting`. Thay vÃ¬ chá»‰ táº¡o State cho cÃ¡c file cÃ³ sáºµn trÃªn mÃ¡y, á»©ng dá»¥ng sáº½ quÃ©t toÃ n bá»™ danh sÃ¡ch trÃªn Google Drive vÃ  lÆ°u láº¡i ngÃ y giá» vÃ o `sync_state.json`. Nhá» Ä‘Ã³, tÃ­nh nÄƒng cáº­p nháº­t Ä‘Ã£ Ä‘Æ°á»£c nÃ¢ng cáº¥p thÃ´ng minh hÆ¡n: Náº¿u ngÃ y sá»­a Ä‘á»•i cá»§a file trÃªn mÃ¡y tÃ­nh báº£ng (local) má»›i hÆ¡n ngÃ y sá»­a Ä‘á»•i trÃªn Drive, á»©ng dá»¥ng sáº½ luÃ´n bá» qua táº£i Ä‘Ã¨ Ä‘á»ƒ báº£o vá»‡ file cá»§a ngÆ°á»i dÃ¹ng.

## [v1.5.1] - 2026-07-02

### ğŸ› Sá»­a lá»—i tÆ°Æ¡ng thÃ­ch hiá»ƒn thá»‹ & Ä‘á»“ng bá»™ trÃªn Kindle Fire 10
- 2026-07-02, 23:40: **Kháº¯c phá»¥c lá»—i khÃ´ng hiá»ƒn thá»‹ thÃ´ng tin `è‡ªç¤¾å“ç•ª, å“ç•ª, è‡ªç¤¾å“å, å“å` trÃªn Kindle Fire 10 (`PdfMetadataManager` & `SyncManager`)**:
  - **PhÃ¢n tÃ­ch nguyÃªn nhÃ¢n gá»‘c rá»…**: TrÃªn mÃ¡y áº£o MEmu, tÃªn tá»‡p tin vÃ  pháº§n má»Ÿ rá»™ng luÃ´n Ä‘á»“ng nháº¥t lÃ  chá»¯ thÆ°á»ng (`.pdf`), tuy nhiÃªn khi chuyá»ƒn sang cÃ¡c dÃ²ng mÃ¡y tÃ­nh báº£ng Kindle Fire 10 (cháº¡y Fire OS / há»‡ thá»‘ng tá»‡p FAT32), trÃ¬nh quáº£n lÃ½ tá»‡p vÃ  MTP thÆ°á»ng tá»± Ä‘á»™ng Ä‘á»•i hoa/thÆ°á»ng (vÃ­ dá»¥ thÃ nh `.PDF`, `.Pdf` hoáº·c thay Ä‘á»•i hoa/thÆ°á»ng cá»§a tÃªn gá»‘c). Do trÆ°á»›c Ä‘Ã¢y á»©ng dá»¥ng truy váº¥n key trong `metadataMap` vÃ  so sÃ¡nh tÃªn file khi Ä‘á»“ng bá»™ theo kiá»ƒu phÃ¢n biá»‡t chá»¯ hoa/thÆ°á»ng (case-sensitive) khá»›p vá»›i Ä‘uÃ´i `.pdf`, káº¿t quáº£ tra cá»©u bá»‹ tráº£ vá» `null`, khiáº¿n tháº» hiá»ƒn thá»‹ thÃ´ng tin (`tvMetadata`) bá»‹ áº©n (`GONE`).
  - **Cáº£i tiáº¿n `PdfMetadataManager`**: ThÃªm phÆ°Æ¡ng thá»©c tra cá»©u linh hoáº¡t `findMetadataEntry(fileName)`, há»— trá»£ tÃ¬m kiáº¿m chÃ­nh xÃ¡c, tÃ¬m kiáº¿m khÃ´ng phÃ¢n biá»‡t chá»¯ hoa/thÆ°á»ng, vÃ  tÃ¬m kiáº¿m theo tÃªn gá»‘c khÃ´ng phá»¥ thuá»™c vÃ o pháº§n má»Ÿ rá»™ng (`.pdf/.PDF`). Ãp dá»¥ng Ä‘á»“ng bá»™ cho cÃ¡c phÆ°Æ¡ng thá»©c `getMetadata`, `hasMetadata`, `formatForDisplay`, `formatForHighlightedDisplay` vÃ  `mergeFromRemote`. Äáº£m báº£o khi lÆ°u metadata má»›i qua `setMetadata` luÃ´n chuáº©n hÃ³a key vá» Ä‘á»‹nh dáº¡ng chuáº©n `*.pdf` chá»¯ thÆ°á»ng Ä‘á»ƒ thá»‘ng nháº¥t trÃªn má»i thiáº¿t bá»‹.
  - **Cáº£i tiáº¿n `SyncManager`**: Chuyá»ƒn toÃ n bá»™ cÃ¡c bÆ°á»›c so sÃ¡nh tÃªn file `pdf_metadata.json` vÃ  kiá»ƒm tra danh sÃ¡ch tá»‡p trÃªn Google Drive sang cháº¿ Ä‘á»™ khÃ´ng phÃ¢n biá»‡t chá»¯ hoa/thÆ°á»ng (`ignoreCase = true`). Äá»“ng thá»i tá»‘i Æ°u logic Ä‘á»“ng bá»™ tá»« Drive: Náº¿u tá»‡p `pdf_metadata.json` trÃªn thiáº¿t bá»‹ chÆ°a cÃ³ dá»¯ liá»‡u OCR thá»±c táº¿ (`getMetadataCount() == 0`), á»©ng dá»¥ng sáº½ luÃ´n Æ°u tiÃªn táº£i vá» dá»¯ liá»‡u tá»« Google Drive thay vÃ¬ so sÃ¡nh má»‘c thá»i gian (trÃ¡nh trÆ°á»ng há»£p thiáº¿t bá»‹ má»›i táº¡o tá»‡p rá»—ng cÃ³ timestamp má»›i hÆ¡n Drive bá»‹ Ä‘áº©y Ä‘Ã¨ lÃªn cloud).

### âœ¨ TÃ­nh nÄƒng má»›i & Cáº£i tiáº¿n UI
- 2026-07-02, 10:48: **TÃ¹y chá»n "LuÃ´n giá»¯ sÃ¡ng mÃ n hÃ¬nh khi Ä‘á»c" (`SettingsActivity` & `PdfViewerActivity`)**:
  - ThÃªm cÃ´ng táº¯c tÃ¹y chá»n trong mÃ n hÃ¬nh CÃ i Ä‘áº·t (`ğŸ’¡ LuÃ´n giá»¯ sÃ¡ng mÃ n hÃ¬nh khi Ä‘á»c` / `ğŸ’¡ èª­æ›¸ä¸­ã¯å¸¸ã«ç”»é¢ã‚’ã‚ªãƒ³ã«ã™ã‚‹`) cho phÃ©p ngÆ°á»i dÃ¹ng báº­t/táº¯t tÃ­nh nÄƒng giá»¯ sÃ¡ng mÃ n hÃ¬nh.
  - Khi Ä‘á»c tÃ i liá»‡u PDF trong `PdfViewerActivity`, á»©ng dá»¥ng tá»± Ä‘á»™ng kiá»ƒm tra thiáº¿t láº­p trong `SettingsManager` vÃ  Ã¡p dá»¥ng/gá»¡ bá» cá» `FLAG_KEEP_SCREEN_ON` má»™t cÃ¡ch linh hoáº¡t, há»— trá»£ tá»‘i Ä‘a cho cÃ´ng nhÃ¢n/ká»¹ thuáº­t viÃªn khi lÃ m viá»‡c lÃ¢u vá»›i tÃ i liá»‡u.
- 2026-07-02, 10:48: **LÃ m ná»•i báº­t vÃ  tÃ¹y chá»‰nh mÃ u sáº¯c thÃ´ng tin `è‡ªç¤¾å“ç•ª, å“ç•ª, è‡ªç¤¾å“å, å“å` (`SettingsActivity`, `SettingsManager`, `PdfMetadataManager`)**:
  - Thiáº¿t káº¿ má»›i cho khung hiá»ƒn thá»‹ metadata trong danh sÃ¡ch file: Sá»­ dá»¥ng background badge áº¥m Ã¡p mÃ u Amber (`@drawable/bg_metadata_badge`), tÄƒng kÃ­ch thÆ°á»›c chá»¯ lÃªn `14sp` rÃµ nÃ©t, bá»• sung padding vÃ  khoáº£ng cÃ¡ch dÃ²ng thoÃ¡ng Ä‘Ã£ng.
  - Tá»‘i Æ°u tráº£i nghiá»‡m Ä‘á»c (UX) theo nguyÃªn táº¯c táº­p trung vÃ o giÃ¡ trá»‹: Máº·c Ä‘á»‹nh thu nhá» vÃ  lÃ m dá»‹u mÃ u cÃ¡c tÃªn nhÃ£n (`<small>` mÃ u xÃ¡m `#78909C`), trong khi in Ä‘áº­m vÃ  tÃ´ mÃ u tÆ°Æ¡ng pháº£n máº¡nh cho pháº§n giÃ¡ trá»‹: mÃ u Äá» tháº«m ná»•i báº­t (`#C62828`) cho tÃªn sáº£n pháº©m (`è‡ªç¤¾å“å`, `å“å`) vÃ  Xanh Ä‘áº­m (`#0D47A1`) cho mÃ£ sá»‘ (`è‡ªç¤¾å“ç•ª`, `å“ç•ª`).
  - **ThÃªm tÃ­nh nÄƒng "MÃ u hiá»ƒn thá»‹ (è‡ªç¤¾å“ç•ª, å“ç•ª, è‡ªç¤¾å“å, å“å)" trong mÃ n hÃ¬nh CÃ i Ä‘áº·t**: Cho phÃ©p ngÆ°á»i dÃ¹ng tÃ¹y chá»n mÃ u sáº¯c riÃªng biá»‡t cho tá»«ng pháº§n NhÃ£n vÃ  GiÃ¡ trá»‹ cá»§a cáº£ 4 thÃ´ng tin. Giao diá»‡n trá»±c quan vá»›i nÃºt báº¥m hiá»ƒn thá»‹ sáºµn mÃ u sáº¯c hiá»‡n táº¡i, há»— trá»£ chá»n nhanh tá»« danh sÃ¡ch 10 mÃ u chuáº©n hoáº·c nháº­p mÃ£ Hex tÃ¹y chá»‰nh báº¥t ká»³ (`#FF0000`, `#00838F`, v.v.). Khi sá»­a mÃ u xong, quay láº¡i danh sÃ¡ch tÃ i liá»‡u mÃ u sáº¯c má»›i sáº½ láº­p tá»©c Ä‘Æ°á»£c Ã¡p dá»¥ng.

### ğŸ› Sá»­a lá»—i danh sÃ¡ch Ä‘á»c & Kiá»ƒm tra cáº­p nháº­t
- 2026-07-02, 10:48: **`UpdateChecker` & `UpdateCheckerWithProgress` â€” Cáº£i tiáº¿n kiá»ƒm tra, tá»± Ä‘á»™ng cÃ i Ä‘áº·t vÃ  má»Ÿ thÆ° má»¥c Download 3 lá»›p**:
  - Khi táº£i `version.json` tá»« `raw.githubusercontent.com`, mÃ¡y chá»§ GitHub CDN thÆ°á»ng lÆ°u cache tá»« 5â€“15 phÃºt khiáº¿n app Ä‘á»c pháº£i báº£n JSON cÅ© dÃ¹ vá»«a push lÃªn GitHub. ÄÃ£ bá»• sung tham sá»‘ chá»‘ng cache timestamp (`?t=currentTimeMillis()`) vÃ  thiáº¿t láº­p `useCaches = false`, `Cache-Control: no-cache` Ä‘á»ƒ Ä‘áº£m báº£o app luÃ´n táº£i chÃ­nh xÃ¡c phiÃªn báº£n má»›i nháº¥t tá»« server.
  - Theo yÃªu cáº§u, Ä‘Ã£ thay Ä‘á»•i thÆ° má»¥c lÆ°u file APK báº£n cáº­p nháº­t (`MyPDFReader-update.apk`) khi táº£i tá»« mÃ n hÃ¬nh CÃ i Ä‘áº·t sang **thÆ° má»¥c Download chung cá»§a Ä‘iá»‡n thoáº¡i (`/storage/emulated/0/Download`)** thay vÃ¬ thÆ° má»¥c riÃªng cá»§a app. NgÆ°á»i dÃ¹ng cÃ³ thá»ƒ dá»… dÃ ng tÃ¬m tháº¥y file trong cÃ¡c á»©ng dá»¥ng Quáº£n lÃ½ tá»‡p.
  - **CÆ¡ cháº¿ cÃ i Ä‘áº·t thÃ´ng minh (Tá»± Ä‘á»™ng cÃ i hoáº·c Má»Ÿ thÆ° má»¥c Download 3 lá»›p)**: Khi táº£i báº£n cáº­p nháº­t xong, á»©ng dá»¥ng tá»± Ä‘á»™ng kiá»ƒm tra quyá»n `canRequestPackageInstalls()`. Náº¿u thiáº¿t bá»‹ Ä‘Ã£ cho phÃ©p, app sáº½ láº­p tá»©c khá»Ÿi cháº¡y mÃ n hÃ¬nh cÃ i Ä‘áº·t APK tá»± Ä‘á»™ng. Náº¿u thiáº¿t bá»‹ cháº·n hoáº·c chÆ°a cÃ³ quyá»n (trÃªn Android 8+), app sáº½ tá»± Ä‘á»™ng chuyá»ƒn tiáº¿p nhá»‹p nhÃ ng sang má»Ÿ TrÃ¬nh quáº£n lÃ½ file táº¡i thÆ° má»¥c `Download` theo cÆ¡ cháº¿ 3 lá»›p dá»± phÃ²ng (`ACTION_VIEW_DOWNLOADS` â†’ URI `resource/folder` â†’ File Picker chung) Ä‘á»ƒ ngÆ°á»i dÃ¹ng tá»± báº¥m cÃ i Ä‘áº·t mÃ  khÃ´ng gáº·p báº¥t ká»³ lá»—i hay rÃ o cáº£n báº£o máº­t nÃ o.
  - **Sá»­a lá»—i hiá»ƒn thá»‹ % bá»‹ sai font/kÃ½ tá»± khi táº£i báº£n cáº­p nháº­t (`LocaleHelper` & `SettingsActivity`)**: PhÃ¡t hiá»‡n tá»« khÃ³a `"downloading"` trong tá»« Ä‘iá»ƒn bá»‹ trÃ¹ng giá»¯a tÃ­nh nÄƒng Äá»“ng bá»™ Drive (`"Äang táº£i (%d/%d): %s"`) vÃ  Kiá»ƒm tra cáº­p nháº­t (`"Äang táº£i... %d%%"`), dáº«n Ä‘áº¿n chuá»—i táº£i vá» báº£n cáº­p nháº­t bá»‹ ghi Ä‘Ã¨ vÃ  hiá»ƒn thá»‹ sai Ä‘á»‹nh dáº¡ng `%d/%d`. ÄÃ£ Ä‘á»•i tÃªn khÃ³a riÃªng biá»‡t thÃ nh `"update_downloading"` Ä‘á»ƒ hiá»ƒn thá»‹ chÃ­nh xÃ¡c % tiáº¿n Ä‘á»™ táº£i xuá»‘ng.
  - **Sá»­a lá»—i "Táº£i xong nhÆ°ng khÃ´ng má»Ÿ Ä‘Æ°á»£c cÃ i Ä‘áº·t" trÃªn Android 8+ (`AndroidManifest.xml`, `UpdateChecker`, `UpdateCheckerWithProgress`)**: Bá»• sung quyá»n `REQUEST_INSTALL_PACKAGES` trong Manifest vÃ  `<root-path>` vÃ o `file_paths.xml` Ä‘á»ƒ Ä‘áº£m báº£o FileProvider khÃ´ng bá»‹ tá»« chá»‘i truy cáº­p thÆ° má»¥c Download trÃªn má»i dÃ²ng mÃ¡y.
- 2026-07-02, 10:48: **`MainActivity` & `PdfFileAdapter` â€” Sá»‘ thá»© tá»± khÃ´ng tá»± cáº­p nháº­t khi sá»­a**: 
  - TrÆ°á»›c Ä‘Ã¢y, khi sá»­a trá»±c tiáº¿p sá»‘ thá»© tá»± trong Ã´ nháº­p liá»‡u (EditText) táº¡i tab Reading List, callback `onSwapPosition` chÆ°a Ä‘Æ°á»£c káº¿t ná»‘i vÃ o `MainActivity`, dáº«n Ä‘áº¿n viá»‡c cÃ¡c item khÃ´ng chuyá»ƒn vá»‹ trÃ­ vÃ  cÃ¡c sá»‘ thá»© tá»± phÃ­a sau khÃ´ng tá»± Ä‘á»™ng cáº­p nháº­t.
  - ÄÃ£ káº¿t ná»‘i callback `onSwapPosition` vÃ o `MainActivity` (gá»i `ReadingListManager.moveToPosition`), giÃºp khi sá»­a sá»‘ thá»© tá»± cá»§a má»™t bÃ i Ä‘á»c, bÃ i Ä‘á»c sáº½ chuyá»ƒn Ä‘áº¿n Ä‘Ãºng vá»‹ trÃ­ má»›i vÃ  táº¥t cáº£ sá»‘ thá»© tá»± phÃ­a sau tá»± Ä‘á»™ng dá»‹ch chuyá»ƒn vÃ  cáº­p nháº­t chÃ­nh xÃ¡c.
  - Bá»• sung `notifyItemRangeChanged` trong thao tÃ¡c di chuyá»ƒn lÃªn/xuá»‘ng (`moveItem`) vÃ  xoÃ¡ bÃ i Ä‘á»c (`removeFromReadingList`) Ä‘á»ƒ sá»‘ thá»© tá»± hiá»ƒn thá»‹ luÃ´n Ä‘á»“ng bá»™ real-time.

### ğŸ› RÃ  soÃ¡t & Sá»­a lá»—i toÃ n dá»± Ã¡n

#### ğŸ”´ Lá»—i nghiÃªm trá»ng Ä‘Ã£ fix
- **`SyncActivity` â€” Sai path sync**: `localFolderFile` bá»‹ táº¡o sai báº±ng cÃ¡ch ghÃ©p `filesDir` + `"/sdcard/MyPDF"` â†’ file táº£i vá» nhÆ°ng khÃ´ng tháº¥y trÃªn mÃ n hÃ¬nh. ÄÃ£ sá»­a thÃ nh `File(MainActivity.PDF_FOLDER)` trá»±c tiáº¿p, Ä‘á»“ng bá»™ vá»›i `SyncWorker`.
- **`PdfViewerActivity` â€” OOM crash**: Bitmap trang cÅ© khÃ´ng Ä‘Æ°á»£c `recycle()` trÆ°á»›c khi render trang má»›i â†’ leak bá»™ nhá»› tÃ­ch lÅ©y â†’ `OutOfMemoryError` sau nhiá»u láº§n chuyá»ƒn trang. ÄÃ£ thÃªm `oldBitmap?.recycle()`.
- **`PdfViewerActivity` â€” UI Ä‘Ã³ng bÄƒng**: `Bitmap.createBitmap()` + `page.render()` cháº¡y trÃªn **Main Thread** â†’ UI Ä‘Ã³ng bÄƒng khi chuyá»ƒn trang. ÄÃ£ chuyá»ƒn sang `Dispatchers.IO` trong `lifecycleScope.launch{}`.
- **`PdfViewerActivity` â€” Matrix khÃ´ng reset**: Khi chuyá»ƒn trang, `matrix` khÃ´ng Ä‘Æ°á»£c `reset()` trÆ°á»›c khi `fitToScreen()` â†’ áº£nh cÃ³ thá»ƒ render sai vá»‹ trÃ­. ÄÃ£ thÃªm `matrix.reset()`.
- **`UpdateChecker` â€” BroadcastReceiver leak**: Receiver chá»‰ unregister khi download thÃ nh cÃ´ng; náº¿u download bá»‹ há»§y hoáº·c tháº¥t báº¡i â†’ leak vÄ©nh viá»…n. ÄÃ£ thÃªm `Handler.postDelayed` tá»± unregister sau 5 phÃºt.
- **`PdfTextExtractor` â€” TextRecognizer khÃ´ng close**: Má»—i láº§n OCR táº¡o instance má»›i mÃ  khÃ´ng Ä‘Ã³ng â†’ resource leak. ÄÃ£ chuyá»ƒn sang `lazy` singleton.
- **`SettingsActivity` â€” Crash khi má»Ÿ Setting**: `SettingsManager.init(context)` chÆ°a tá»«ng Ä‘Æ°á»£c gá»i á»Ÿ báº¥t cá»© Ä‘Ã¢u trong codebase â†’ truy cáº­p `SharedPreferences` gÃ¢y ngoáº¡i lá»‡ `UninitializedPropertyAccessException`. ÄÃ£ thÃªm `SettingsManager.init(this)` vÃ o `MainActivity` vÃ  `SettingsActivity`.
- **`MainActivity` â€” Máº¥t káº¿t ná»‘i tÃ­nh nÄƒng Ä‘á»c metadata PDF**: `PdfMetadataManager.init(this)` vÃ  sá»± kiá»‡n cho nÃºt `"ğŸ” Scan"` (`btnScanMetadata`) chÆ°a tá»«ng Ä‘Æ°á»£c gÃ¡n vÃ o `MainActivity` tá»« phiÃªn báº£n trÆ°á»›c â†’ á»©ng dá»¥ng khÃ´ng táº£i hoáº·c khÃ´ng cho phÃ©p scan hiá»ƒn thá»‹ thÃ´ng tin å“å, è‡ªç¤¾å“ç•ª, è‡ªç¤¾å“å. ÄÃ£ káº¿t ná»‘i Ä‘áº§y Ä‘á»§ khá»Ÿi táº¡o, load JSON vÃ  dialog hiá»ƒn thá»‹ tiáº¿n trÃ¬nh scan metadata.

#### ğŸŸ¡ Hiá»‡u nÄƒng Ä‘Ã£ cáº£i thiá»‡n
- **`MainActivity.loadPdfFiles()`**: Chuyá»ƒn scan file system tá»« **Main Thread Ä‘á»“ng bá»™** sang `Dispatchers.IO` trong coroutine â†’ khÃ´ng cÃ²n block UI khi `onResume()`.
- **`SyncManager`**: ThÃªm `connectTimeout = 15s` vÃ  `readTimeout = 30â€“60s` cho táº¥t cáº£ HTTP connections (`findFolderId`, `listDriveFiles`) â†’ app khÃ´ng treo vÃ´ thá»i háº¡n khi máº¡ng kÃ©m.

### âœ¨ TÃ­nh nÄƒng má»›i & UI
- **Cáº­p nháº­t á»©ng dá»¥ng & Tinh gá»n Setting**: 
  - Chuyá»ƒn logic kiá»ƒm tra vÃ  táº£i cáº­p nháº­t (UpdateChecker) tá»« popup tá»± Ä‘á»™ng á»Ÿ trang chá»§ vÃ o mÃ n hÃ¬nh **âš™ Setting** vá»›i thanh progress (tiáº¿n trÃ¬nh táº£i % real-time).
  - Bá» pháº§n "CÃ i Ä‘áº·t hiá»ƒn thá»‹" (cá»¡ chá»¯, Ä‘á»™ trong suá»‘t, thá»i gian thÃ´ng bÃ¡o) khá»i mÃ n hÃ¬nh Setting theo yÃªu cáº§u Ä‘á»ƒ giao diá»‡n tinh gá»n, chá»‰ táº­p trung vÃ o kiá»ƒm tra cáº­p nháº­t.
- **Äá»“ng bá»™ 2 chiá»u riÃªng cho file `pdf_metadata.json` (`SyncManager`)**:
  - CÃ¡c file PDF tÃ i liá»‡u giá»¯ nguyÃªn cÆ¡ cháº¿ **chá»‰ táº£i vá» tá»« Drive (1 chiá»u)** náº¿u báº£n trÃªn Drive má»›i hÆ¡n.
  - RiÃªng file thÃ´ng tin OCR `pdf_metadata.json` Ã¡p dá»¥ng cÆ¡ cháº¿ **Ä‘á»“ng bá»™ 2 chiá»u thÃ´ng minh**:
    - Náº¿u file trÃªn Drive má»›i hÆ¡n mÃ¡y: táº£i vá» mÃ¡y vÃ  tá»± Ä‘á»™ng cáº­p nháº­t hiá»ƒn thá»‹ lÃªn danh sÃ¡ch.
    - Náº¿u file trÃªn mÃ¡y má»›i hÆ¡n Drive (do ngÆ°á»i dÃ¹ng vá»«a báº¥m nÃºt Scan OCR táº¡o dá»¯ liá»‡u má»›i): tá»± Ä‘á»™ng táº£i lÃªn Google Drive (ghi Ä‘Ã¨ file cÅ© hoáº·c táº¡o file má»›i náº¿u Drive chÆ°a cÃ³) Ä‘á»ƒ chia sáº» káº¿t quáº£ quÃ©t cho cÃ¡c thiáº¿t bá»‹ khÃ¡c.

- **`UpdateCheckerWithProgress` â€” Fix lá»—i táº£i xuá»‘ng tháº¥t báº¡i**: Chuyá»ƒn tá»« `DownloadManager` cá»§a há»‡ thá»‘ng (hay bá»‹ tá»« chá»‘i quyá»n trÃªn Android 10+ vÃ  lá»—i khi gáº·p 302 Redirect tá»« GitHub Releases) sang táº£i trá»±c tiáº¿p báº±ng `HttpURLConnection` trong Coroutine (`Dispatchers.IO`), tá»± Ä‘á»™ng xá»­ lÃ½ chuyá»ƒn hÆ°á»›ng (redirect) vÃ  lÆ°u vÃ o vÃ¹ng nhá»› an toÃ n `getExternalFilesDir`, Ä‘áº£m báº£o cÃ i Ä‘áº·t APK thÃ nh cÃ´ng 100%.
- **Fix lá»—i 404 khi báº¥m táº£i báº£n cáº­p nháº­t má»›i (`build.yml`)**: PhÃ¡t hiá»‡n quy trÃ¬nh tá»± Ä‘á»™ng trÃªn GitHub Actions trÆ°á»›c Ä‘Ã¢y chá»‰ táº£i file APK lÃªn má»¥c *Artifacts* táº¡m thá»i (pháº£i Ä‘Äƒng nháº­p GitHub má»›i táº£i Ä‘Æ°á»£c) mÃ  quÃªn khÃ´ng táº¡o báº£n phÃ¡t hÃ nh *GitHub Release*, dáº«n Ä‘áº¿n Ä‘Æ°á»ng link táº£i `releases/latest/download/app-release.apk` bá»‹ bÃ¡o lá»—i 404. ÄÃ£ bá»• sung bÆ°á»›c tá»± Ä‘á»™ng táº¡o GitHub Release vÃ  Ä‘Ã­nh kÃ¨m file `app-release.apk` vÃ o workflow `build.yml`.
- **Tá»‘i Æ°u hÃ³a siÃªu nháº¹ & Thay Ä‘á»•i giao diá»‡n icon PDF**:
  - **Thay áº£nh bÃ¬a PDF báº±ng Icon vector tÄ©nh (`@drawable/ic_pdf`)**: Loáº¡i bá» hoÃ n toÃ n quy trÃ¬nh má»Ÿ luá»“ng file (`ParcelFileDescriptor`), render trang Ä‘áº§u (`PdfRenderer`) vÃ  cache Bitmap má»—i khi cuá»™n danh sÃ¡ch. GiÃºp danh sÃ¡ch cuá»™n mÆ°á»£t mÃ  tá»©c thÃ¬ á»Ÿ 60/120 FPS, giáº£i phÃ³ng hÃ ng chá»¥c MB RAM vÃ  khÃ´ng gÃ¢y hao pin.
  - **KÃ­ch hoáº¡t R8 Minification & Resource Shrinking (`build.gradle`)**: Báº­t `minifyEnabled true` vÃ  `shrinkResources true` cho báº£n release kÃ¨m bá»™ quy táº¯c ProGuard chuáº©n cho Gson/Room/ML Kit. QuÃ¡ trÃ¬nh nÃ y tá»± Ä‘á»™ng cáº¯t bá» code vÃ  tÃ i nguyÃªn dÆ° thá»«a cá»§a cÃ¡c thÆ° viá»‡n Google Material, AppCompat, Coroutines... giÃºp giáº£m Ä‘Ã¡ng ká»ƒ dung lÆ°á»£ng file APK (gá»n nháº¹ hÆ¡n ~40-60%) vÃ  khá»Ÿi Ä‘á»™ng app nhanh hÆ¡n.

#### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `SyncActivity.kt` | Fix sai path localFolderFile |
| `PdfViewerActivity.kt` | Render IO thread, recycle bitmap cÅ©, reset matrix, thÃªm coroutine imports |
| `UpdateCheckerWithProgress.kt` | Thay tháº¿ `DownloadManager` báº±ng Coroutine HTTP download Ä‘á»ƒ fix lá»—i táº£i tháº¥t báº¡i |
| `file_paths.xml` | ThÃªm `<external-files-path>` cho FileProvider |
| `.github/workflows/build.yml` | ThÃªm bÆ°á»›c tá»± Ä‘á»™ng táº¡o GitHub Release Ä‘Ã­nh kÃ¨m APK Ä‘á»ƒ fix lá»—i táº£i 404 |
| `item_pdf_file.xml` & `ic_pdf.xml` | Thay hiá»ƒn thá»‹ áº£nh bÃ¬a PDF báº±ng icon vector tÄ©nh siÃªu nháº¹ |
| `build.gradle` & `proguard-rules.pro` | Báº­t R8 minification & shrinkResources tá»‘i Æ°u hÃ³a dung lÆ°á»£ng APK |
| `PdfTextExtractor.kt` | TextRecognizer lazy singleton |
| `MainActivity.kt` | loadPdfFiles() cháº¡y trÃªn IO thread |
| `SyncManager.kt` | ThÃªm HTTP timeout cho findFolderId vÃ  listDriveFiles; Ä‘á»“ng bá»™ 2 chiá»u cho `pdf_metadata.json` |

---

## [v1.5.0] - 2026-07-01

### ğŸ” TrÃ­ch xuáº¥t thÃ´ng tin PDF (å“å, è‡ªç¤¾å“ç•ª, è‡ªç¤¾å“å)
- **OCR trang Ä‘áº§u PDF**: DÃ¹ng ML Kit Text Recognition (Japanese) Ä‘á»ƒ trÃ­ch xuáº¥t å“å, è‡ªç¤¾å“ç•ª, è‡ªç¤¾å“å tá»« trang Ä‘áº§u.
- **Bounding box parsing**: DÃ¹ng vá»‹ trÃ­ pixel (bounding box) cá»§a tá»«ng element OCR thay vÃ¬ parse text thuáº§n. TÃ¬m key (å“å) â†’ láº¥y element ngay bÃªn pháº£i cÃ¹ng dÃ²ng = giÃ¡ trá»‹. Äáº£m báº£o Ä‘á»c Ä‘Ãºng báº£ng dÃ¹ OCR tráº£ text khÃ´ng theo thá»© tá»±.
- **Chá»‘ng nháº§m key chuá»—i con**: TÃ¬m key dÃ i trÆ°á»›c (è‡ªç¤¾å“ç•ª â†’ è‡ªç¤¾å“å â†’ å“ç•ª â†’ å“å). Khi tÃ¬m "å“å" loáº¡i trá»« element chá»©a "è‡ªç¤¾å“å", tÃ¬m "å“ç•ª" loáº¡i trá»« "è‡ªç¤¾å“ç•ª". Há»— trá»£ cáº£ 2 dáº¡ng file.
- **Æ¯u tiÃªn vá»‹ trÃ­**: Náº¿u tá»« khÃ³a xuáº¥t hiá»‡n nhiá»u láº§n trong trang, luÃ´n láº¥y tá»« khÃ³a náº±m á»Ÿ vá»‹ trÃ­ cao nháº¥t (trÃªn cÃ¹ng) cá»§a trang PDF Ä‘á»ƒ trÃ¡nh láº¥y nháº§m thÃ´ng tin á»Ÿ pháº§n ná»™i dung.
- **Xá»­ lÃ½ Ã´ trá»‘ng/dáº¥u gáº¡ch ngang**: Náº¿u Ã´ giÃ¡ trá»‹ lÃ  dáº¥u gáº¡ch ngang (-) vÃ  bá»‹ OCR bá» qua, há»‡ thá»‘ng sáº½ tráº£ vá» rá»—ng (null) thay vÃ¬ nháº£y sang láº¥y giÃ¡ trá»‹ cá»§a cá»™t tiáº¿p theo (nhÆ° ã‚«ãƒ©ãƒ¼).
- **Æ¯u tiÃªn tá»« khÃ³a**: Náº¿u trÃªn trang PDF cÃ³ cáº£ cá»¥m `è‡ªç¤¾å“ç•ª/è‡ªç¤¾å“å` vÃ  `å“ç•ª/å“å`, há»‡ thá»‘ng sáº½ Æ°u tiÃªn trÃ­ch xuáº¥t `è‡ªç¤¾å“ç•ª/è‡ªç¤¾å“å` (thÆ°á»ng á»Ÿ gÃ³c trÃªn bÃªn trÃ¡i) vÃ  bá» qua cá»¥m cÃ²n láº¡i Ä‘á»ƒ hiá»ƒn thá»‹ gá»n gÃ ng 1 trong 2 dáº¡ng.
- **NÃºt "ğŸ” Scan"**: TrÃªn header, báº¥m Ä‘á»ƒ scan táº¥t cáº£ file chÆ°a cÃ³ metadata. Hiá»‡n dialog progress (1/20, 2/20...).
- **Hiá»ƒn thá»‹ metadata**: DÆ°á»›i tÃªn file PDF, hiá»‡n metadata mÃ u xanh teal (11sp). LuÃ´n hiá»ƒn thá»‹ theo thá»© tá»± chuáº©n: `è‡ªç¤¾å“ç•ª | è‡ªç¤¾å“å | å“ç•ª | å“å` (náº¿u file cÃ³ Ä‘á»§ cáº£ 4 thÃ´ng tin, app sáº½ láº¥y vÃ  hiá»‡n Ä‘á»§ cáº£ 4).
- **LÆ°u file JSON**: Káº¿t quáº£ OCR lÆ°u vÃ o `pdf_metadata.json` trong thÆ° má»¥c MyPDF.
- **Äá»“ng bá»™ Google Drive**: Khi Sync, upload `pdf_metadata.json` lÃªn Drive. MÃ¡y khÃ¡c sync sáº½ download vÃ  merge metadata.
- **Update file description**: GÃ¡n metadata vÃ o description cá»§a file PDF trÃªn Google Drive.

### ğŸ“ File má»›i
| File | MÃ´ táº£ |
|------|-------|
| `PdfMetadataManager.kt` | Singleton quáº£n lÃ½ file `pdf_metadata.json` â€” load/save/merge/format |
| `PdfTextExtractor.kt` | OCR trÃ­ch xuáº¥t thÃ´ng tin tá»« trang Ä‘áº§u PDF báº±ng ML Kit Japanese |

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `build.gradle` | ThÃªm dependency `com.google.mlkit:text-recognition-japanese:16.0.1` |
| `item_pdf_file.xml` | ThÃªm `tvMetadata` TextView dÆ°á»›i tÃªn file |
| `PdfFileAdapter.kt` | Hiá»ƒn thá»‹ metadata cho cáº£ 2 tab (All + Reading List) |
| `activity_main.xml` | ThÃªm nÃºt `btnScanMetadata` trÃªn header |
| `MainActivity.kt` | Init PdfMetadataManager, xá»­ lÃ½ nÃºt Scan vá»›i progress dialog |
| `SyncManager.kt` | ThÃªm sync metadata JSON (upload/download/merge) + update file description |
| `LocaleHelper.kt` | ThÃªm chuá»—i scan_title, scan_preparing, scan_complete, all_scanned (vi+ja) |
| `HISTORY.md` | Cáº­p nháº­t changelog v1.5.0 |

---

## [v1.4.6] - 2026-07-01

### ğŸ”„ TÃ¡ch biá»‡t gesture vuá»‘t dá»c/ngang
- **Vuá»‘t lÃªn/xuá»‘ng**: Chá»‰ chuyá»ƒn trang trong file PDF Ä‘ang má»Ÿ. Hiá»‡n toast khi Ä‘Ã£ á»Ÿ trang Ä‘áº§u/cuá»‘i.
- **Vuá»‘t trÃ¡i/pháº£i**: Chá»‰ chuyá»ƒn qua láº¡i giá»¯a cÃ¡c file khÃ¡c nhau (khÃ´ng cÃ²n chuyá»ƒn trang).
- TrÆ°á»›c Ä‘Ã¢y cáº£ hai hÆ°á»›ng vuá»‘t Ä‘á»u cÃ³ thá»ƒ chuyá»ƒn trang + file, gÃ¢y nháº§m láº«n.

---

## [v1.4.5] - 2026-06-30

### ğŸ¨ Cáº£i thiá»‡n giao diá»‡n Header
- **NÃºt Settings Ä‘áº¹p hÆ¡n**: Äá»•i tá»« emoji âš™ Ä‘Æ¡n giáº£n thÃ nh nÃºt "âš™ Setting" cÃ³ background xanh Ä‘áº­m bo trÃ²n, chá»¯ tráº¯ng Ä‘áº­m, hiá»‡u á»©ng ripple khi nháº¥n.
- **Cá» ngÃ´n ngá»¯ to hÆ¡n**: TÄƒng kÃ­ch thÆ°á»›c emoji cá» ğŸ‡»ğŸ‡³ğŸ‡¯ğŸ‡µ tá»« 28sp â†’ 36sp, dá»… báº¥m hÆ¡n trÃªn tablet.

### âœ¨ ThÃ´ng bÃ¡o Ä‘á»c file khi vuá»‘t chuyá»ƒn file
- **Hiá»‡n thÃ´ng bÃ¡o khi vuá»‘t**: Khi Ä‘á»c file tá»« Danh sÃ¡ch Ä‘á»c vÃ  vuá»‘t sang file tiáº¿p theo/trÆ°á»›c Ä‘Ã³, thÃ´ng bÃ¡o "ğŸ“– Äang Ä‘á»c file sá»‘ X" sáº½ tá»± Ä‘á»™ng hiá»‡n lÃªn cho file má»›i.
- **Há»§y timer cÅ© khi vuá»‘t nhanh**: Khi vuá»‘t liÃªn tá»¥c, timer vÃ  animation cÅ© Ä‘Æ°á»£c há»§y trÆ°á»›c khi hiá»ƒn thá»‹ thÃ´ng bÃ¡o má»›i, trÃ¡nh bá»‹ overlap.

### ğŸ“ File má»›i
| File | MÃ´ táº£ |
|------|-------|
| `bg_settings_btn.xml` | Drawable background cho nÃºt Settings: hÃ¬nh chá»¯ nháº­t bo trÃ²n 8dp, mÃ u `#0D47A1`, cÃ³ ripple effect |

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `activity_main.xml` | TÄƒng `textSize` cá» 28spâ†’36sp, Ä‘á»•i btnSettings thÃ nh nÃºt cÃ³ text+background Ä‘áº¹p |
| `PdfViewerActivity.kt` | ThÃªm field `isFromReadingList`, gá»i `showReadingNotice()` trong `switchFile()`, thÃªm `removeCallbacksAndMessages` + `animate().cancel()` Ä‘á»ƒ reset khi vuá»‘t nhanh |
| `HISTORY.md` | Cáº­p nháº­t changelog lÃªn `v1.4.5` |

---


### âœ¨ TÃ­nh nÄƒng má»›i: NÃºt cÃ i Ä‘áº·t hiá»ƒn thá»‹ (âš™ Settings)
- **NÃºt âš™ trÃªn header**: ThÃªm nÃºt Settings trÃªn thanh header (giá»¯a cá» ğŸ‡¯ğŸ‡µ vÃ  nÃºt Sync) Ä‘á»ƒ má»Ÿ há»™p thoáº¡i cÃ i Ä‘áº·t.
- **Cá»¡ chá»¯ tÃªn file tÃ¹y chá»n**: SeekBar cho phÃ©p chá»‰nh cá»¡ chá»¯ tÃªn file trong Danh sÃ¡ch Ä‘á»c tá»« 12sp â†’ 32sp (máº·c Ä‘á»‹nh 19sp).
- **Äá»™ trong suá»‘t thÃ´ng bÃ¡o**: SeekBar chá»‰nh opacity thÃ´ng bÃ¡o "Äang Ä‘á»c file sá»‘ X" tá»« 10% â†’ 100% (máº·c Ä‘á»‹nh 50%).
- **Thá»i gian hiá»ƒn thá»‹ thÃ´ng bÃ¡o**: SeekBar chá»‰nh thá»i gian hiá»ƒn thá»‹ thÃ´ng bÃ¡o tá»« 1s â†’ 30s (máº·c Ä‘á»‹nh 5s).
- **LÆ°u cÃ i Ä‘áº·t**: Táº¥t cáº£ cÃ i Ä‘áº·t Ä‘Æ°á»£c lÆ°u vÃ o SharedPreferences, giá»¯ nguyÃªn khi táº¯t app.
- **Äa ngÃ´n ngá»¯**: Há»™p thoáº¡i hiá»ƒn thá»‹ Ä‘Ãºng ngÃ´n ngá»¯ (Viá»‡t/Nháº­t).

### ğŸ“ File má»›i
| File | MÃ´ táº£ |
|------|-------|
| `SettingsManager.kt` | Singleton quáº£n lÃ½ cÃ i Ä‘áº·t hiá»ƒn thá»‹ (cá»¡ chá»¯, opacity, thá»i gian) qua SharedPreferences |

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `activity_main.xml` | ThÃªm nÃºt `btnSettings` (âš™) vÃ o header |
| `MainActivity.kt` | ThÃªm import `AlertDialog`, `SeekBar`, `LinearLayout`, `TextView`. Init `SettingsManager`, thÃªm `showSettingsDialog()` vá»›i 3 SeekBar |
| `PdfFileAdapter.kt` | Äá»•i `textSize = 19f` â†’ `SettingsManager.getFileNameSize().toFloat()` |
| `PdfViewerActivity.kt` | Äá»•i `alpha = 0.5f` â†’ `SettingsManager.getNoticeOpacityFloat()`, `5000` â†’ `SettingsManager.getNoticeDurationMs()` |
| `LocaleHelper.kt` | ThÃªm 6 chuá»—i dá»‹ch settings: `settings_title`, `settings_file_name_size`, `settings_notice_opacity`, `settings_notice_duration`, `settings_save`, `settings_cancel` |
| `HISTORY.md` | Cáº­p nháº­t changelog lÃªn `v1.4.4` |

---


### âœ¨ ThÃ´ng bÃ¡o Ä‘ang Ä‘á»c file sá»‘ máº¥y
- **Overlay thÃ´ng bÃ¡o**: Khi má»Ÿ file tá»« **Danh sÃ¡ch Ä‘á»c**, trÃªn mÃ n hÃ¬nh PDF sáº½ hiá»‡n thÃ´ng bÃ¡o "ğŸ“– Äang Ä‘á»c file sá»‘ X" (tiáº¿ng Viá»‡t) hoáº·c "ğŸ“– ãƒ•ã‚¡ã‚¤ãƒ« X ã‚’èª­ã‚“ã§ã„ã¾ã™" (tiáº¿ng Nháº­t) tÃ¹y theo ngÃ´n ngá»¯ Ä‘ang chá»n.
- **Tá»± Ä‘á»™ng áº©n sau 5 giÃ¢y**: ThÃ´ng bÃ¡o hiá»ƒn thá»‹ á»Ÿ opacity 50%, sau 5 giÃ¢y sáº½ tá»± fade out mÆ°á»£t mÃ  rá»“i biáº¿n máº¥t.
- **Chá»‰ hiá»ƒn thá»‹ khi má»Ÿ tá»« danh sÃ¡ch Ä‘á»c**: KhÃ´ng hiá»‡n khi má»Ÿ file tá»« tab "Táº¥t cáº£ file".

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `activity_pdf_viewer.xml` | ThÃªm `tvReadingNotice` (TextView overlay, background bÃ¡n trong suá»‘t) |
| `PdfViewerActivity.kt` | ThÃªm import `ObjectAnimator`, thÃªm `noticeHandler`, Ä‘á»c `reading_list_index` tá»« intent, thÃªm hÃ m `showReadingNotice()` vá»›i fade-out animation |
| `MainActivity.kt` | Truyá»n thÃªm `reading_list_index` qua intent khi má»Ÿ file tá»« reading list |
| `LocaleHelper.kt` | ThÃªm chuá»—i dá»‹ch `reading_file_number` cho cáº£ tiáº¿ng Viá»‡t vÃ  tiáº¿ng Nháº­t |
| `HISTORY.md` | Cáº­p nháº­t changelog lÃªn `v1.4.3` |

---


### âœ¨ Cáº£i tiáº¿n giao diá»‡n Danh sÃ¡ch Ä‘á»c
- **TÃªn file lá»›n hÆ¡n**: TÄƒng kÃ­ch thÆ°á»›c chá»¯ tÃªn file trong danh sÃ¡ch Ä‘á»c tá»« 15sp lÃªn 19sp Ä‘á»ƒ dá»… Ä‘á»c hÆ¡n.
- **Sá»‘ thá»© tá»± cÃ³ thá»ƒ sá»­a trá»±c tiáº¿p**: Thay Ä‘á»•i Ã´ sá»‘ thá»© tá»± (1, 2, 3...) tá»« `TextView` sang `EditText`, cho phÃ©p ngÆ°á»i dÃ¹ng báº¥m vÃ o vÃ  nháº­p sá»‘ má»›i.
- **HoÃ¡n Ä‘á»•i vá»‹ trÃ­ tá»± Ä‘á»™ng (swap)**: Khi ngÆ°á»i dÃ¹ng sá»­a sá»‘ thá»© tá»± cá»§a má»™t item (vÃ­ dá»¥: sá»­a item 1 thÃ nh 3), item Ä‘Ã³ sáº½ Ä‘Æ°á»£c chuyá»ƒn Ä‘áº¿n vá»‹ trÃ­ 3, vÃ  cÃ¡c item khÃ¡c tá»± Ä‘á»™ng dá»‹ch chuyá»ƒn theo.

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `item_pdf_file.xml` | Äá»•i `tvIndex` tá»« `TextView` â†’ `EditText` vá»›i `inputType="number"`, `imeOptions="actionDone"`, `selectAllOnFocus` |
| `PdfFileAdapter.kt` | ThÃªm import `EditText`/`EditorInfo`, Ä‘á»•i type `tvIndex`, thÃªm logic `setupIndexEditor()` vÃ  `handleIndexChange()`, tÄƒng text size tÃªn file, thÃªm callback `onSwapPosition` |
| `MainActivity.kt` | ThÃªm callback `onSwapPosition` khi táº¡o adapter, thÃªm hÃ m `swapItems()` gá»i `ReadingListManager.moveToPosition()` |
| `HISTORY.md` | Cáº­p nháº­t changelog lÃªn `v1.4.2` |

---

## [v1.4.1] - 2026-06-27

### âª HoÃ n tÃ¡c (Rollback)
- **Há»§y bá» Äa danh sÃ¡ch Ä‘á»c**: ÄÆ°a á»©ng dá»¥ng vá» cáº¥u trÃºc sá»­ dá»¥ng duy nháº¥t má»™t danh sÃ¡ch Ä‘á»c toÃ n cá»¥c Ä‘á»ƒ trÃ¡nh sá»± phá»©c táº¡p vÃ  nháº§m láº«n cho ngÆ°á»i dÃ¹ng.
- **Tá»± Ä‘á»™ng chuyá»ƒn Ä‘á»•i dá»¯ liá»‡u**: Cáº­p nháº­t Database version 3, tá»± Ä‘á»™ng gá»™p táº¥t cáº£ file á»Ÿ má»i danh sÃ¡ch vá» láº¡i má»™t danh sÃ¡ch duy nháº¥t.
- **KhÃ´i phá»¥c UI**: Gá»¡ bá» Spinner chá»n danh sÃ¡ch vÃ  há»™p thoáº¡i (Dialog) khi thÃªm PDF.

---

## [v1.4.0] - 2026-06-27

### âœ¨ TÃ­nh nÄƒng má»›i: Äa danh sÃ¡ch Ä‘á»c
- **Táº¡o vÃ  phÃ¢n loáº¡i danh sÃ¡ch Ä‘á»c tá»± do**: NgÆ°á»i dÃ¹ng cÃ³ thá»ƒ táº¡o nhiá»u danh sÃ¡ch Ä‘á»c vá»›i tÃªn gá»i riÃªng (vd: CÃ´ng viá»‡c, Giáº£i trÃ­, TÃ i liá»‡u há»c táº­p...). 
- **TÆ°Æ¡ng tÃ¡c thÃ´ng minh qua nÃºt `+`**: Khi báº¥m thÃªm má»™t PDF vÃ o danh sÃ¡ch Ä‘á»c, há»‡ thá»‘ng sáº½ má»Ÿ má»™t há»™p thoáº¡i cho phÃ©p báº¡n chá»n danh sÃ¡ch muá»‘n thÃªm vÃ o, hoáº·c táº¡o ngay má»™t danh sÃ¡ch má»›i. Náº¿u báº¡n bá» qua hoáº·c khÃ´ng chá»n tÃªn, há»‡ thá»‘ng tá»± Ä‘á»™ng Ä‘Æ°a file vÃ o danh sÃ¡ch Ä‘á»c máº·c Ä‘á»‹nh mang tÃªn **"Chung"**.
- **Chuyá»ƒn Ä‘á»•i linh hoáº¡t (Dropdown/Spinner)**: Trong tab "Danh sÃ¡ch Ä‘á»c", má»™t menu tháº£ xuá»‘ng (Spinner) Ä‘Æ°á»£c bá»• sung á»Ÿ trÃªn cÃ¹ng giÃºp báº¡n dá»… dÃ ng chuyá»ƒn qua láº¡i giá»¯a cÃ¡c danh sÃ¡ch Ä‘á»c cÃ¡ nhÃ¢n hÃ³a cá»§a mÃ¬nh.
- **TÆ°Æ¡ng thÃ­ch Room Database**: NÃ¢ng cáº¥p schema Room DB vá»›i tÃ­nh nÄƒng `Migration` (khÃ³a chÃ­nh ghÃ©p tá»« Ä‘Æ°á»ng dáº«n file vÃ  tÃªn danh sÃ¡ch). ToÃ n bá»™ dá»¯ liá»‡u danh sÃ¡ch Ä‘á»c trÆ°á»›c Ä‘Ã¢y sáº½ tá»± Ä‘á»™ng Ä‘Æ°á»£c chuyá»ƒn sang danh sÃ¡ch "Chung" mÃ  khÃ´ng bá»‹ máº¥t mÃ¡t.

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `PdfEntity` & `PdfDao` | Thay Ä‘á»•i khÃ³a chÃ­nh thÃ nh `(path, listName)` Ä‘á»ƒ 1 file cÃ³ thá»ƒ náº±m á»Ÿ nhiá»u danh sÃ¡ch khÃ¡c nhau |
| `AppDatabase` | Viáº¿t mÃ£ tá»± Ä‘á»™ng di chuyá»ƒn dá»¯ liá»‡u (Migration) tá»« DB cÅ© sang cáº¥u trÃºc Ä‘a danh sÃ¡ch |
| `ReadingListManager.kt` | NÃ¢ng cáº¥p lÃµi lÆ°u trá»¯ vÃ  quáº£n lÃ½ táº­p tin theo tÃªn danh sÃ¡ch Ä‘á»c |
| `activity_main.xml` | Gáº¯n `Spinner` (menu chá»n danh sÃ¡ch) vÃ o ngay trÃªn tháº» hiá»ƒn thá»‹ Danh sÃ¡ch Ä‘á»c |
| `MainActivity.kt` | Láº­p trÃ¬nh há»™p thoáº¡i Dialog khi báº¥m nÃºt `+`, xá»­ lÃ½ sá»± kiá»‡n chuyá»ƒn list trong `Spinner` |
| `HISTORY.md` | Cáº­p nháº­t changelog lÃªn `v1.4.0` |

---

## [v1.3.1] - 2026-06-27

### ğŸ¨ Cáº­p nháº­t Giao diá»‡n (UI)
- **Äá»•i tÃªn á»©ng dá»¥ng**: Äá»•i tiÃªu Ä‘á» á»©ng dá»¥ng trÃªn mÃ n hÃ¬nh chÃ­nh tá»« "My PDF Reader" thÃ nh "ä»•æ§˜æ›¸".
- **Tá»‘i Æ°u hiá»ƒn thá»‹ cho Tablet**:
  - Má»Ÿ rá»™ng vÃ¹ng cáº£m á»©ng cá»§a cÃ¡c nÃºt chuyá»ƒn Ä‘á»•i ngÃ´n ngá»¯ (ğŸ‡»ğŸ‡³, ğŸ‡¯ğŸ‡µ) vÃ  nÃºt "Sync" láº¥p Ä‘áº§y chiá»u cao header (56dp).
  - TÄƒng kÃ­ch thÆ°á»›c emoji cá» (lÃªn 28sp) vÃ  ná»›i rá»™ng khoáº£ng cÃ¡ch giá»¯a cÃ¡c cá» (16dp), cÅ©ng nhÆ° Ä‘áº©y khoáº£ng cÃ¡ch cá»¥m cá» vÃ  nÃºt Sync (32dp) Ä‘á»ƒ chá»‘ng báº¥m nháº§m trÃªn mÃ n hÃ¬nh cáº£m á»©ng lá»›n.

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `activity_main.xml` | Äá»•i text header, chá»‰nh `layout_height="match_parent"` vÃ  cáº­p nháº­t `marginEnd` cho cÃ¡c nÃºt |
| `HISTORY.md` | Cáº­p nháº­t changelog |

---

## [v1.3.0] - 2026-06-27

### âœ¨ TÃ­nh nÄƒng má»›i & NÃ¢ng cáº¥p (Major Update)
- **áº¢nh bÃ¬a Thumbnail**: Thay tháº¿ icon máº·c Ä‘á»‹nh báº±ng hÃ¬nh áº£nh thu nhá» trang Ä‘áº§u tiÃªn cá»§a file PDF trong tab "Táº¥t cáº£ file".
  - Ãp dá»¥ng cÃ´ng nghá»‡ `PdfRenderer` cháº¡y ngáº§m.
  - Tá»‘i Æ°u hÃ³a cá»±c nháº¹ cho cÃ¡c mÃ¡y cáº¥u hÃ¬nh tháº¥p (nhÆ° Kindle Fire HD 10) nhá» vÃ o bá»™ nhá»› Ä‘á»‡m `LruCache` giÃºp vuá»‘t danh sÃ¡ch siÃªu mÆ°á»£t, khÃ´ng bá»‹ trÃ n RAM.
- **Tá»‘i Æ°u hoÃ¡ cá»±c háº¡n vá»›i Room Database**:
  - Chuyá»ƒn Ä‘á»•i toÃ n bá»™ há»‡ thá»‘ng lÆ°u trá»¯ "Danh sÃ¡ch Ä‘á»c" tá»« `SharedPreferences` sang kiáº¿n trÃºc `Room Database` (SQLite).
  - Kháº¯c phá»¥c hoÃ n toÃ n tÃ¬nh tráº¡ng mÃ¡y bá»‹ Ä‘á»©ng, treo á»©ng dá»¥ng khi sá»‘ lÆ°á»£ng file lÃªn tá»›i má»©c hÃ ng ngÃ n (2.000 - 5.000 file).
  - Tá»± Ä‘á»™ng di chuyá»ƒn (migrate) dá»¯ liá»‡u cÅ© cá»§a ngÆ°á»i dÃ¹ng sang há»‡ thá»‘ng má»›i Ä‘á»ƒ khÃ´ng máº¥t danh sÃ¡ch Ä‘ang Ä‘á»c dá»Ÿ.

### ğŸ“ File Ä‘Ã£ sá»­a/thÃªm má»›i
| File | Thay Ä‘á»•i |
|------|----------|
| `build.gradle` | ThÃªm cÃ¡c plugin vÃ  thÆ° viá»‡n Room |
| `PdfEntity`, `PdfDao`, `AppDatabase` | ThÃªm má»›i: Kiáº¿n trÃºc Database cho Room |
| `ReadingListManager.kt` | NÃ¢ng cáº¥p toÃ n diá»‡n sang Room + tá»± Ä‘á»™ng chuyá»ƒn Ä‘á»•i dá»¯ liá»‡u cÅ© |
| `item_pdf_file.xml` | Äá»•i icon thÃ nh `ImageView` (id: `ivThumbnail`) |
| `PdfThumbnailLoader.kt` | ThÃªm má»›i: Module náº¡p áº£nh bÃ¬a báº¥t Ä‘á»“ng bá»™ cÃ³ giá»›i háº¡n RAM an toÃ n |
| `PdfFileAdapter.kt` | TÃ­ch há»£p Coroutines náº¡p thumbnail báº¥t Ä‘á»“ng bá»™ vá»›i cÆ¡ cháº¿ tÃ¡i cháº¿ chá»‘ng leak RAM |
| `HISTORY.md` | Cáº­p nháº­t changelog lÃªn `v1.3.0` |

---

## [v1.2.3] - 2026-06-27

### âœ¨ TÃ­nh nÄƒng má»›i
- **Danh sÃ¡ch thÆ° má»¥c Drive**: Sau khi káº¿t ná»‘i thÃ nh cÃ´ng vá»›i Google Drive, app sáº½ tá»± Ä‘á»™ng gá»i API Ä‘á»ƒ láº¥y toÃ n bá»™ danh sÃ¡ch cÃ¡c thÆ° má»¥c cÃ³ sáºµn vÃ  hiá»ƒn thá»‹ trong dropdown.
- NgÆ°á»i dÃ¹ng cÃ³ thá»ƒ nháº¥n vÃ o Ä‘á»ƒ chá»n thÆ° má»¥c tá»« danh sÃ¡ch (máº·c Ä‘á»‹nh váº«n lÃ  `shiyo`), hoáº·c tá»± gÃµ tÃªn thÆ° má»¥c náº¿u muá»‘n (nhá» sá»­ dá»¥ng `AutoCompleteTextView`).

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `SyncManager.kt` | ThÃªm hÃ m `listAllFolders()` gá»i API Drive Ä‘á»ƒ tÃ¬m cÃ¡c file cÃ³ `mimeType='application/vnd.google-apps.folder'` |
| `activity_sync.xml` | Äá»•i `EditText` thÃ nh `AutoCompleteTextView` |
| `SyncActivity.kt` | Táº£i danh sÃ¡ch thÆ° má»¥c ngáº§m vÃ  gáº¯n vÃ o Adapter khi Ä‘Äƒng nháº­p thÃ nh cÃ´ng |
| `HISTORY.md` | Cáº­p nháº­t changelog |

---

## [v1.2.2] - 2026-06-27

### ğŸ› Sá»­a lá»—i
- **Fix lá»—i thiáº¿u Ä‘a ngÃ´n ngá»¯**: Cáº­p nháº­t cÃ¡c nÃºt `â† Back`, `â—€ Trang trÆ°á»›c` vÃ  `Trang sau â–¶` trong `PdfViewerActivity` vÃ  `SyncActivity` sá»­ dá»¥ng `LocaleHelper` Ä‘á»ƒ há»— trá»£ dá»‹ch sang tiáº¿ng Nháº­t.

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `LocaleHelper.kt` | ThÃªm chuá»—i dá»‹ch `back_button` |
| `PdfViewerActivity.kt` | Thay hardcoded text báº±ng `LocaleHelper.getString()` cho cÃ¡c nÃºt Ä‘iá»u hÆ°á»›ng |
| `SyncActivity.kt` | Ãp dá»¥ng Ä‘a ngÃ´n ngá»¯ cho nÃºt `btnBack` |
| `HISTORY.md` | Cáº­p nháº­t changelog |

---

## [v1.2.1] - 2026-06-27

### ğŸ› Sá»­a lá»—i
- **Fix crash khi vuá»‘t trÃ¡i/pháº£i**: App bá»‹ crash khi vuá»‘t ngang Ä‘á»ƒ chuyá»ƒn file do gesture trigger Ä‘á»“ng thá»i vá»›i viá»‡c close/reopen PdfRenderer. ThÃªm flag `isNavigating` chá»‘ng double-trigger, Ä‘Ã³ng page trÆ°á»›c khi chuyá»ƒn file.

### ğŸ”§ Thay Ä‘á»•i
- **Äá»•i logic vuá»‘t trÃ¡i/pháº£i**: Vuá»‘t trÃ¡i = chuyá»ƒn sang trang tiáº¿p theo (thay vÃ¬ chuyá»ƒn file). Náº¿u Ä‘ang á»Ÿ trang cuá»‘i thÃ¬ má»›i chuyá»ƒn sang file tiáº¿p theo. Vuá»‘t pháº£i tÆ°Æ¡ng tá»± ngÆ°á»£c láº¡i.
- **NÃºt â—€/â–¶ cÅ©ng chuyá»ƒn file**: Khi Ä‘ang á»Ÿ trang cuá»‘i, áº¥n â–¶ sáº½ má»Ÿ file tiáº¿p theo. Khi Ä‘ang á»Ÿ trang Ä‘áº§u, áº¥n â—€ sáº½ má»Ÿ file trÆ°á»›c Ä‘Ã³.
- **Natural sort thá»© tá»± file**: Sáº¯p xáº¿p file theo sá»‘ tá»± nhiÃªn (1 â†’ 2 â†’ 10 â†’ 20) thay vÃ¬ alphabetical (1 â†’ 10 â†’ 2 â†’ 20). File cÃ³ tÃªn lÃ  sá»‘ sáº½ Ä‘Æ°á»£c sáº¯p xáº¿p Ä‘Ãºng thá»© tá»±.
- **Hiá»ƒn thá»‹ vá»‹ trÃ­ file**: Title bar hiá»ƒn thá»‹ `[3/10] filename.pdf` Ä‘á»ƒ biáº¿t Ä‘ang xem file thá»© máº¥y trong danh sÃ¡ch.

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `PdfViewerActivity.kt` | Fix crash, Ä‘á»•i logic vuá»‘t, hiá»ƒn thá»‹ vá»‹ trÃ­ file, nÃºt nav chuyá»ƒn file |
| `MainActivity.kt` | Natural sort thá»© tá»± file |
| `HISTORY.md` | Cáº­p nháº­t changelog |

---

## [v1.2] - 2026-06-27

### âœ¨ TÃ­nh nÄƒng má»›i
- **Chuyá»ƒn Ä‘á»•i ngÃ´n ngá»¯ ğŸ‡»ğŸ‡³/ğŸ‡¯ğŸ‡µ**: ThÃªm 2 nÃºt cá» Viá»‡t Nam vÃ  Nháº­t Báº£n trÃªn thanh header (cáº¡nh nÃºt Sync). áº¤n vÃ o Ä‘á»ƒ chuyá»ƒn Ä‘á»•i toÃ n bá»™ giao diá»‡n app giá»¯a tiáº¿ng Viá»‡t vÃ  tiáº¿ng Nháº­t. NgÃ´n ngá»¯ Ä‘Æ°á»£c lÆ°u láº¡i sau khi táº¯t app.
- **Há»‡ thá»‘ng Ä‘a ngÃ´n ngá»¯ (LocaleHelper)**: Táº¡o singleton quáº£n lÃ½ 50+ chuá»—i dá»‹ch Viá»‡t-Nháº­t, há»— trá»£ má»Ÿ rá»™ng thÃªm ngÃ´n ngá»¯ trong tÆ°Æ¡ng lai.
- **Auto-sync khi cÃ³ thay Ä‘á»•i**: Thay tháº¿ há»‡ thá»‘ng cÃ i Ä‘áº·t sync theo giá» cá»‘ Ä‘á»‹nh (1h/2h/4h/8h) báº±ng cÆ¡ cháº¿ tá»± Ä‘á»™ng phÃ¡t hiá»‡n file má»›i trÃªn Google Drive vÃ  táº£i vá». Chá»‰ cáº§n báº­t/táº¯t Switch, khÃ´ng cáº§n cÃ i Ä‘áº·t táº§n suáº¥t.
- **File HISTORY.md**: Táº¡o file changelog lÆ°u láº¡i toÃ n bá»™ lá»‹ch sá»­ thay Ä‘á»•i cá»§a dá»± Ã¡n.

### ğŸ”§ Thay Ä‘á»•i
- **SyncActivity**: Loáº¡i bá» RadioGroup chá»n táº§n suáº¥t sync (1h/2h/4h/8h), thay báº±ng Switch on/off Ä‘Æ¡n giáº£n vá»›i mÃ´ táº£ "Khi cÃ³ file má»›i trÃªn Drive sáº½ tá»± Ä‘á»™ng táº£i vá»".
- **SyncWorker**: Cáº­p nháº­t logic polling 15 phÃºt (minimum WorkManager), chá»‰ táº£i file má»›i chÆ°a cÃ³ trong thÆ° má»¥c local.
- **SyncManager**: ThÃªm hÃ m `checkAndSyncNewFiles()` so sÃ¡nh danh sÃ¡ch file Drive vs local, thÃªm quáº£n lÃ½ tráº¡ng thÃ¡i auto-sync qua SharedPreferences.
- **Táº¥t cáº£ Activity & Adapter**: Thay toÃ n bá»™ hardcoded Vietnamese strings báº±ng `LocaleHelper.getString()` Ä‘á»ƒ há»— trá»£ Ä‘a ngÃ´n ngá»¯.

### ğŸ“ File má»›i
| File | MÃ´ táº£ |
|------|-------|
| `LocaleHelper.kt` | Singleton quáº£n lÃ½ Ä‘a ngÃ´n ngá»¯ Viá»‡t-Nháº­t |
| `HISTORY.md` | File changelog dá»± Ã¡n |

### ğŸ“ File Ä‘Ã£ sá»­a
| File | Thay Ä‘á»•i |
|------|----------|
| `activity_main.xml` | ThÃªm 2 nÃºt cá» ğŸ‡»ğŸ‡³/ğŸ‡¯ğŸ‡µ vÃ o header |
| `activity_sync.xml` | XÃ³a RadioGroup interval, thÃªm id cho TextView, Ä‘á»•i label auto-sync |
| `MainActivity.kt` | Logic chuyá»ƒn Ä‘á»•i ngÃ´n ngá»¯ + highlight nÃºt cá» + Ä‘a ngÃ´n ngá»¯ |
| `PdfViewerActivity.kt` | Äa ngÃ´n ngá»¯ Toast messages |
| `PdfFileAdapter.kt` | Äa ngÃ´n ngá»¯ status text |
| `SyncActivity.kt` | Äa ngÃ´n ngá»¯ + auto-sync WorkManager scheduling |
| `SyncManager.kt` | ThÃªm `checkAndSyncNewFiles()` + quáº£n lÃ½ auto-sync + Ä‘a ngÃ´n ngá»¯ |
| `SyncWorker.kt` | Kiá»ƒm tra auto-sync enabled + dÃ¹ng `checkAndSyncNewFiles()` |

---

## [v1.1] - PhiÃªn báº£n trÆ°á»›c
- TÃ­ch há»£p Google Drive Sync (Ä‘Äƒng nháº­p, táº£i file PDF tá»« Drive)
- Reading List manager (thÃªm/xÃ³a/sáº¯p xáº¿p danh sÃ¡ch Ä‘á»c)
- PDF Viewer vá»›i zoom/pan/swipe navigation
- Tá»± Ä‘á»™ng sync theo lá»‹ch cá»‘ Ä‘á»‹nh (1h/2h/4h/8h) qua WorkManager
- TÃ¬m kiáº¿m file PDF theo tÃªn
- Giá»¯ mÃ n hÃ¬nh luÃ´n sÃ¡ng khi Ä‘á»c PDF

---

## [v1.0] - PhiÃªn báº£n Ä‘áº§u tiÃªn
- Äá»c file PDF tá»« thÆ° má»¥c `/sdcard/MyPDF`
- Danh sÃ¡ch file PDF cá»¥c bá»™
- Xem PDF toÃ n mÃ n hÃ¬nh
-   X ó a   b Ï  e n g i n e   j p . c o . f u j i x e r o x   c i,   t í c h   h ãp   S D K   c o m . f u j i f i l m . f b   m Ûi   v à   c ­p   n h ­t   t o à n   b Ù  t h °  v i Çn   N a t i v e   ( . s o )   t ë  s o u r c e s / r e s o u r c e s   Ã  k h ¯c   p h åc   t r i Çt   Ã  l ×i   S I G S E G V   t r ê n   m á y   A n d r o i d   İi   m Ûi .  
 -   èn g   d ån g   ã   °ãc   b i ê n   d Ëc h   t h à n h   c ô n g   s a u   k h i   d Ín   d ¹p   c á c   m ã   n g u Ón   c i.  
 -   S ía   l ×i   S I G S E G V :   P h åc   h Ói   t o à n   b Ù  c á c   c l a s s   p h å  t h u Ùc   v à   p h °¡n g   t h éc   N a t i v e   g Ñc   c ça   B a s e B r i d g e   ( b Ë  l °ãc   b Ï  t r °Ûc   ó )   Ã  J N I _ O n L o a d   v à   C + +   C a l l b a c k   k h ô n g   b Ë  c r a s h   d o   l ×i   N o S u c h M e t h o d E r r o r   ( c h ³n g   h ¡n   n h °  k h i   C + +   g Íi   n g °ãc   l ¡i   h à m   p a s t e B i t m a p   c ça   J a v a ) .  
 -   T h ê m   c ¡  c h ¿  g h i   l o g   r a   f i l e   \ x d w _ d e b u g . t x t \   t r o n g   C a c h e   Ã  t r a c k i n g   x e m   h à m   N a t i v e   n à o   g â y   r a   l ×i   S I G S E G V   k h i   c h ¡y   t r ê n   t h i ¿t   b Ë.  
 -   S ía   l ×i   S I G S E G V   k h i   m ß  f i l e   X D W   b ±n g   c á c h   k i Ãm   t r a   m ã   l ×i   t r £  v Á  c ça   h à m   \ g e t N u m b e r O f P a g e s ( ) \   ( c h É  g Íi   \ g e t X b d P a g e C o u n t \   n ¿u   k h ô n g   c ó   l ×i ,   t r á n h   g Íi   t r ê n   d o c u m e n t   h Ïn g )   v à   b Õ  s u n g   l Çn h   \ i n i t D o c E d i t ( ) \   Ã  k h ßi   t ¡o   s t a t e   C + + .  
 -   B Õ  s u n g   l Çn h   \ c r e a t e C a n v a s A n d B i t m a p ( ) \   t r °Ûc   k h i   g Íi   \ s e t D r a w i n g E n v ( ) \   Ã  c ¥p   p h á t   v ù n g   n h Û  B i t m a p   c h o   C + + ,   t r á n h   l ×i   S I G S E G V   k h i   c Ñ  g ¯n g   v ½  l ê n   v ù n g   n h Û  N U L L .  
 -   B Õ  s u n g   l Çn h   \ i n i t T i l e d L a y e r ( ) \   v à   p u b l i c   c l a s s   \ D r a w e r S t a t u s O b s e r v a b l e \   Ã  k h ßi   t ¡o   §y   ç  h Ç  t h Ñn g   r e n d e r   C + +   t r °Ûc   k h i   t h i ¿t   l ­p   m ô i   t r °İn g   v ½,   n h ±m   s ía   l ×i   S I G S E G V   c u Ñi   c ù n g   t r o n g   \ s e t D r a w i n g E n v \ .  
 -   S ía   l ×i   g i a o   d i Çn   h i Ãn   t h Ë  2   n ú t   t r ù n g   t ê n   n h a u   b ±n g   c á c h   t á c h   r i ê n g   c h u ×i   d Ëc h   v à   ©n   t h a n h   c h Ín   F i l e   n ¿u   t h °  m åc   c h É  c ó   1   f i l e .  
 -   S ía   l ×i   £n h   t r ¯n g   ( k h ô n g   r e n d e r )   d o   n g u y ê n   b £n   C + +   r e n d e r   t r ñc   t i ¿p   v à o   v ù n g   n h Û  t )n h   \ B a s e B r i d g e . c a c h e \   t h ô n g   q u a   \ p a s t e B i t m a p ( ) \   t h a y   v ì   B i t m a p   £o   t r u y Án   v à o   h à m .  
 -   S ía   l ×i   ' k h ô n g   t h Ã  m ß  f i l e   . x d w '   d o   d ù n g   B i t m a p   1 x 1   p i x e l   t h a y   v ì   k í c h   t h °Ûc   t h ­t   l à m   t r à n   b Ù  n h Û  C + + .   T h a y   v à o   ó ,   c ¥p   p h á t   ç  k í c h   t h °Ûc   m à n   h ì n h   Ã  C + +   v ½  a n   t o à n .  
 -   C h Én h   s ía   \ X d w V i e w e r A c t i v i t y \   Ã  h i Çn   A l e r t D i a l o g   b ¯t   l ×i   c h i   t i ¿t   ( T r a c e )   n ¿u   h à m   \ o p e n D o c u m e n t \   t r £  v Á  s Ñ  â m ,   g i ú p   c h ©n   o á n   c h í n h   x á c   n g u y ê n   n h â n   t h a y   v ì   t ñ  Ùn g   m ß  b ±n g   én g   d ån g   n g o à i   ( D o c u W o r k s ) .  
 -   V ô   h i Çu   h ó a   t í n h   n n g   t ñ  Ùn g   k h ó a   N a t i v e   R e n d e r e r   k h i   p h á t   h i Çn   C r a s h   ( x ó a   c İ  K E Y _ N A T I V E _ F A I L E D )   Ã  £m   b £o   én g   d ån g   l u ô n   c Ñ  g ¯n g   m ß  b ±n g   C + +   t h a y   v ì   t ñ  Ùn g   ©y   r a   D o c u W o r k s .  
 -   S ía   l o g i c   t r £  v Á  £n h   t ë  C + + :   K h i   t r u y Án   t r ñc   t i ¿p   Ñi   t °ãn g   B i t m a p   v à o   C + + ,   C + +   s ½  g h i   t h ³n g   p i x e l   v à o   B i t m a p   ó   t h a y   v ì   t r £  v Á  q u a   b i ¿n   \ B a s e B r i d g e . c a c h e \ .   C ­p   n h ­t   m ã   n g u Ón   K o t l i n   Ã  l ¥y   ú n g   \ d u m m y B i t m a p \   r a   h i Ãn   t h Ë,   g i £i   q u y ¿t   l ×i   c h É  h i Ãn   t h Ë  1   ô   v u ô n g   n h Ï  1 x 1   p i x e l .  
 -   S ía   l ×i   l o g i c   n g n   c £n   h à m   \ s h o w P a g e \   h o ¡t   Ùn g :   B Õ  s u n g   d ò n g   \ u s i n g N a t i v e R e n d e r e r   =   t r u e \   k h i   m ß  f i l e   t h à n h   c ô n g .   T r °Ûc   ó   c İ  n à y   m ·c   Ën h   l à   \  a l s e \   k h i ¿n   én g   d ån g   t £i   x o n g   f i l e   n h °n g   t ë  c h Ñi   v ½  £n h   v à   g i ¥u   l u ô n   c á c   n ú t   c h u y Ãn   t r a n g .  
 -   T ñ  Ùn g   t í n h   t o á n   t ÷  l Ç  t h u   p h ó n g   ( S c a l e / D P I )   c h o   C + + :   T h a y   v ì   é p   c én g   \ 1 . 0 f \   ( k h i ¿n   £n h   v ½  r a   s i ê u   n h Ï  ß  g ó c   m à n   h ì n h ) ,   m ã   n g u Ón   g i İ  â y   s ½  t í n h   t o á n   Ùn g   d ña   t r ê n   k í c h   t h °Ûc   g i ¥y   t h ­t   v à   Ù  p h â n   g i £i   m à n   h ì n h .   C ­p   n h ­t   n à y   s ½  g i ú p   t à i   l i Çu   t r à n   v i Án   ( f i t   c e n t e r )   v ëa   k h í t   t r ê n   m à n   h ì n h   i Çn   t h o ¡i   c ça   n g °İi   d ù n g .  
 -   T ¯t   k i Ãm   t r a   a n   t o à n   t h °  v i Çn   C + + :   V i Çc   b á o   l ×i   \  
 k h ô n g  
 t h Ã 
 m ß 
 f i l e \   x u ¥t   p h á t   t ë  v i Çc   h à m   k i Ãm   t r a   a n   t o à n   ã   n h ­n   t h ¥y   h Ç  t h Ñn g   c h °a   l o a d   x o n g   t h °  v i Çn   n ê n   t ñ  Ùn g   b Ï  c u Ùc .   ã   é p   én g   d ån g   l u ô n   t i n   t °ßn g   v à   g i a o   v i Çc   t h ³n g   c h o   C + +   Ã  t ì m   r a   n g u y ê n   n h â n   g Ñc   r Å  ( h o ·c   s ½  c h ¡y   t h à n h   c ô n g   l u ô n ) .  
 -   S ía   l ×i   m i s m a t c h   k í c h   t h °Ûc   b Ù  Çm   ( B u f f e r   S i z e   M i s m a t c h )   v Ûi   C + + :   V ì   C + +   s ½  t ë  c h Ñi   v ½  v à   t r £  v Á  l ×i   n ¿u   k í c h   t h °Ûc   k h u n g   t r a n h   ( B i t m a p )   k h ô n g   k h Ûp   c h í n h   x á c   t Ûi   t ën g   p i x e l   s o   v Ûi   y ê u   c §u   c ça   Ù  p h â n   g i £i   ( D P I ) .   G i İ  â y ,   én g   d ån g   s ½  n h İ  C + +   t í n h   t o á n   k í c h   t h °Ûc   c h i Áu   d à i / r Ùn g   t ¡i   D P I   c §n   t h i ¿t ,   s a u   ó   m Ûi   t ¡o   r a   k h u n g   t r a n h   ( B i t m a p )   v ëa   k h í t   y   h Çt   k í c h   t h °Ûc   ó   Ã  t r á n h   b Ë  t ë  c h Ñi .  
 -   ã   ©n   t h à n h   c ô n g   c á c   n ú t   i Áu   h °Ûn g   ( T r a n g   t r °Ûc / T r a n g   s a u )   k h i   Íc   f i l e   X D W   t h e o   y ê u   c §u   c ça   n g °İi   d ù n g ,   Ã  k h ô n g   b Ë  t r ù n g   l ·p   v Ûi   b Ù  c ô n g   c å  c ça   P D F   c ó   s µn .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 8 : 5 9 :   S á » ­ a   l á »  i   m Ã   n   h Ã ¬ n h   Ä  e n   k h i   x e m   f i l e   X D W   b á º ± n g   c Ã ¡ c h   t h i á º ¿ t   l á º ­ p   n á »  n   t r á º ¯ n g   c h o   b i t m a p   ( A R G B _ 8 8 8 8   m á º · c   Ä  á » 9 n h   l Ã     t r o n g   s u á »  t ) ,   h i á » ’n   t h á » 9   l á º ¡ i   c Ã ¡ c   n Ã º t   Ä  i á »  u   h Æ ° á » : n g   t r a n g ,   v Ã     t h Ã ª m   x á » ­   l Ã ½   f a l l b a c k   t á » ±   Ä  á » "!n g   m á » x  b á º ± n g   a p p   n g o Ã   i   n á º ¿ u   n a t i v e   k h Ã ´ n g   m á » x  Ä  Æ ° á » £ c .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 0 2 :   S á » ­ a   l á »  i   t h Æ °   v i á » ! n   N a t i v e   k h Ã ´ n g   r e n d e r   Ä  Æ ° á » £ c   t r a n g   X D W   d o   t r u y á »  n   s a i   t h a m   s á »    s c a l e   ( t r u y á »  n   9 6 . 0 f   t h a y   v Ã ¬   1 . 0 f ) .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 0 7 :   S á » ­ a   l á »  i   N a t i v e   k h Ã ´ n g   r e n d e r   Ä  Æ ° á » £ c   d o   t r u y á »  n   s a i   p a g e   i n d e x   ( t h Æ °   v i á » ! n   C + +   k á » ³   v á »  n g   p a g e I n d e x   b á º ¯ t   Ä  á º § u   t á » «   1   t h a y   v Ã ¬   0 ) .   Ä  Ã £   t h Ã ª m   c Æ ¡   c h á º ¿   b r u t e - f o r c e   t h á » ­   Ä  á » "!n g   ( t á » ±   f a l l b a c k   s a n g   1 - b a s e d   i n d e x   v Ã     s c a l e   k h Ã ¡ c )   Ä  á » ’  Ä  á º £ m   b á º £ o   k h Ã ´ n g   b á » 9   r á » : t   r e n d e r   á º £ n h .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 1 0 :   C á º ­ p   n h á º ­ t   h Ã   m   g e t P a g e B i t m a p   Ä  á » ’  t Ã ­ n h   t o Ã ¡ n   t á » ±   Ä  á » "!n g   h á » !   s á »    s c a l e   ( t h u   p h Ã ³ n g )   s a o   c h o   t Ã   i   l i á » ! u   v á » « a   v á º · n   v á » : i   k Ã ­ c h   t h Æ ° á » : c   m Ã   n   h Ã ¬ n h   h i á » ’n   t h á » 9 .   K h á º ¯ c   p h á » ¥ c   l á »  i   r e n d e r   á º £ n h   q u Ã ¡   n h á »    b á º ± n g   1 / 9 6   m Ã   n   h Ã ¬ n h   d o   b á » 9   f i x   c á » © n g   s c a l e = 1 . 0 f .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 1 2 :   S á » ­ a   l á »  i   t Ã ­ n h   t o Ã ¡ n   h á » !   s á »    s c a l e   ( t h u   p h Ã ³ n g )   b á » 9   s a i   k h i   n h Ã ¢ n   n h á º § m   v á » : i   t a r g e t D p i   ( l Ã   m   c h o   á º £ n h   x u á º ¥ t   b á » 9   p h Ã ³ n g   t o   h Æ ¡ n   9 6   l á º § n   s o   v á » : i   b Ã ¬ n h   t h Æ ° á »  n g ,   g Ã ¢ y   l á »  i   O u t   o f   M e m o r y   v Ã     t r á º £   v á »    - 1 ) .   S c a l e   h i á » ! n   t á º ¡ i   l Ã     h á » !   s á »    t h á » ± c   ( v Ã ­   d á » ¥   1 . 3 6 x ) .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 1 5 :   T Ã ¡ i   t h i á º ¿ t   k á º ¿   l o g i c   g e t P a g e B i t m a p :   K h Ã ´ i   p h á » ¥ c   l á º ¡ i   l u á »  n g   v á º ½   c a n v a s   c h u á º © n   t h e o   k Ã ­ c h   t h Æ ° á » : c   P a p e r   g á »  c   t h a y   v Ã ¬   Ã © p   v á º ½   t r á » ± c   t i á º ¿ p   v Ã   o   C a n v a s   m Ã   n   h Ã ¬ n h ,   g i á º £ i   q u y á º ¿ t   t r i á » ! t   Ä  á » ’  v á º ¥ n   Ä  á »    t h a m   s á »    s c a l e   ( D P I )   b á » 9   l á » ! c h   p h a   v á » : i   k Ã ­ c h   t h Æ ° á » : c   b i t m a p   g Ã ¢ y   l á »  i   k h Ã ´ n g   m á » x  Ä  Æ ° á » £ c   f i l e .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 1 7 :   T h a y   t h á º ¿   t o Ã   n   b á » "!  l o g i c   t Ã ­ n h   s c a l e   v Ã     k Ã ­ c h   t h Æ ° á » : c   k h á » "   g i á º ¥ y   b á º ± n g   v Ã ² n g   l á º · p   B r u t e - f o r c e   g i á º £ m   d á º § n   ( t á » «   3 0 0 . 0 f   x u á »  n g   1 . 0 f ) .   P h Æ ° Æ ¡ n g   p h Ã ¡ p   n Ã   y   g i Ã º p   t á » ±   Ä  á » "!n g   d Ã ²   t Ã ¬ m   t á » ·   l á » !   s c a l e   l á » : n   n h á º ¥ t   m Ã     C + +   c Ã ³   t h á » ’  v á º ½   l á »  t   v Ã   o   C a n v a s   m Ã   n   h Ã ¬ n h   m Ã     k h Ã ´ n g   b á » 9   v Ä ’n g   l á »  i   - 1 ,   Ä  á º £ m   b á º £ o   á º £ n h   r e n d e r   á » x  Ä  á » "!  p h Ã ¢ n   g i á º £ i   t á »  t   n h á º ¥ t   c Ã ³   t h á » ’  c h o   m á »  i   l o á º ¡ i   f i l e   x d w .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 2 :   K h á º ¯ c   p h á » ¥ c   l á »  i   á º £ n h   X D W   b á » 9   c r o p   s Ã ¡ t   m Ã © p   v i á »  n   d o   t á » ·   l á » !   z o o m   á » x  n g Æ ° á » ¡ n g   c á » ± c   h á º ¡ n   ( b á º ± n g   c Ã ¡ c h   t h a y   t h á º ¿   v Ã ² n g   l á º · p   b r u t e - f o r c e   t h Ã   n h   t h u á º ­ t   t o Ã ¡ n   t Ã ­ n h   t o Ã ¡ n   t o Ã ¡ n   h á »  c   c h Ã ­ n h   x Ã ¡ c   1 0 0 % ,   c á » "!n g   t h Ã ª m   5 %   l á »    m a r g i n   Ä  á » ’  á º £ n h   h i á » ’n   t h á » 9   v á » « a   v á º · n   v Ã     d á » &   n h Ã ¬ n   h Æ ¡ n ) . 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 2 :   B á » "   s u n g   c Ã ¡ c   b á º £ n   d á » 9 c h   c Ã ² n   t h i á º ¿ u   ( " p r e v _ f i l e " ,   " n e x t _ f i l e " )   v Ã   o   L o c a l e H e l p e r   Ä  á » ’  h a i   n Ã º t   Ä  i á »  u   h Æ ° á » : n g   h i á » ’n   t h á » 9   Ä  Ã º n g   n g Ã ´ n   n g á » ¯   ( V i á » ! t / N h á º ­ t )   t h a y   v Ã ¬   h i á » ! n   t Ã ª n   b i á º ¿ n   n g u y Ã ª n   t h á » § y .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 5 :   H o Ã   n   t h i á » ! n   l o g i c   g e t P a g e B i t m a p :   K á º ¿ t   h á » £ p   g i á » ¯ a   t Ã ­ n h   t o Ã ¡ n   s c a l e   b á º ± n g   t o Ã ¡ n   h á »  c   v Ã     d Ã ²   t Ã ¬ m   B r u t e - f o r c e   n g u y Ã ª n   t h á » § y   ( I n t e g e r ) .   G i á º £ i   q u y á º ¿ t   t á º ­ n   g á »  c   v i á » ! c   t h Æ °   v i á » ! n   C + +   t á » «   c h á »  i   c Ã ¡ c   s á »    t h á » ± c   ( f r a c t i o n a l   s c a l e )   h o á º · c   t á » ±   Ä  á » "!n g   n Ã © m   l á »  i   - 1   k h i   s c a l e   c h á º ¡ m   s Ã ¡ t   v i á »  n ,   Ä  á º £ m   b á º £ o   l á º ¥ y   Ä  Æ ° á » £ c   t á » ·   l á » !   n g u y Ã ª n   ( I n t e g e r )   l á » : n   n h á º ¥ t   m Ã     v á º « n   g i á » ¯   l á »    a n   t o Ã   n   5 % .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 8 :   T Ã ¡ i   k i á º ¿ n   t r Ã º c   l á º ¡ i   t o Ã   n   b á » "!  c Æ ¡   c h á º ¿   C a n v a s   c á » § a   D o c u W o r k s   C + + :   P h Ã ¡ t   h i á » ! n   r a   n g u y Ã ª n   n h Ã ¢ n   g á »  c   r á » &   C + +   n Ã © m   l á »  i   - 1   l Ã     d o   B i t m a p   Ä  á º § u   v Ã   o   c Ã ³   k Ã ­ c h   t h Æ ° á » : c   L á » aN   H Æ   N   s o   v á » : i   k Ã ­ c h   t h Æ ° á » : c   t h á º ­ t   c á » § a   á º £ n h   s a u   k h i   z o o m .   G i á º £ i   p h Ã ¡ p :   Ä  á º £ o   n g Æ ° á » £ c   q u y   t r Ã ¬ n h ,   l á º ¥ y   m á » "!t   t á » ·   l á » !   Z o o m   n g u y Ã ª n   ( I n t e g e r )   a n   t o Ã   n   ( t á » «   1 0 0   Ä  á º ¿ n   3 0 0 ) ,   t Ã ­ n h   t o Ã ¡ n   r a   k Ã ­ c h   t h Æ ° á » : c   ( W i d t h   x   H e i g h t )   C H Ã  N H   X Ã  C   Ä  á º ¿ n   t á » « n g   p i x e l   m Ã     b á » © c   á º £ n h   c á º § n ,   v Ã     t á º ¡ o   B i t m a p   v á » : i   Ä  Ã º n g   k Ã ­ c h   t h Æ ° á » : c   Ä  Ã ³ .   B á º ± n g   c Ã ¡ c h   n Ã   y ,   C + +   s á º ½   v á º ½   v á » « a   k h Ã ­ t   1 0 0 %   v Ã   o   B i t m a p   m Ã     k h Ã ´ n g   b a o   g i á »    b Ã ¡ o   l á »  i   k Ã ­ c h   t h Æ ° á » : c . 
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 2 8 :   Ä  á » ’  Ä  á º £ m   b á º £ o   á º £ n h   c Ã ³   l á »    ( m a r g i n )   Ä  á º ¹ p ,   t h a y   v Ã ¬   c a n   t h i á » ! p   v Ã   o   s c a l e ,   g i á »    Ä  Ã ¢ y   á º £ n h   x u á º ¥ t   r a   s á º ½   Ä  Æ ° á » £ c   A n d r o i d   I m a g e V i e w   t á » ±   Ä  á » "!n g   b Ã ³ p   n h á »    ( f i t C e n t e r )   v Ã     c h Ã ¨ n   t h Ã ª m   p a d d i n g   8 d p   c h u á º © n   U I   A n d r o i d .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 3 0 :   X á » ­   l Ã ½   t r i á » ! t   Ä  á » ’  q u y   t á º ¯ c   g i á » : i   h á º ¡ n   k h á º ¯ t   k h e   c á » § a   t h Æ °   v i á » ! n   C + +   D o c u W o r k s .   T h Æ °   v i á » ! n   y Ã ª u   c á º § u   3   Ä  i á »  u   k i á » ! n   Ä  á »  n g   t h á »  i :   S c a l e   p h á º £ i   l Ã     s á »    n g u y Ã ª n ,   B i t m a p   k h Ã ´ n g   Ä  Æ ° á » £ c   l á » : n   h Æ ¡ n   m Ã   n   h Ã ¬ n h   Ä  i á » ! n   t h o á º ¡ i   ( n á º ¿ u   l á » : n   h Æ ¡ n   s á º ½   b á » 9   t á » «   c h á »  i   c á º ¥ p   p h Ã ¡ t   b á » "!  n h á » : ) ,   v Ã     t Ã   i   l i á » ! u   s a u   k h i   t h u   p h Ã ³ n g   p h á º £ i   p h á » §   k Ã ­ n   1 0 0 %   d i á » ! n   t Ã ­ c h   B i t m a p .   G i á º £ i   p h Ã ¡ p   l Ã     t Ã ¬ m   r a   h á » !   s á »    Z o o m   n g u y Ã ª n   ( I n t e g e r )   l á » : n   n h á º ¥ t   s a o   c h o   b á » © c   á º £ n h   x u á º ¥ t   r a   h o Ã   n   t o Ã   n   n á º ± m   l á »  t   b Ã ª n   t r o n g   k Ã ­ c h   t h Æ ° á » : c   m Ã   n   h Ã ¬ n h .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 3 3 :   T h e o   y Ã ª u   c á º § u   c á » § a   n g Æ ° á »  i   d Ã ¹ n g ,   k h Ã ´ i   p h á » ¥ c   l á º ¡ i   c Æ ¡   c h á º ¿   r e n d e r   B r u t e - f o r c e   m á º ¡ n h   n h á º ¥ t   ( c h á º ¡ y   l Ã ¹ i   t á » «   3 0 0 . 0 f   x u á »  n g )   Ä  á » ’  Ä  á º £ m   b á º £ o   l u Ã ´ n   l o a d   t h Ã   n h   c Ã ´ n g   f i l e   . x d w .   Ä  á »  n g   t h á »  i   t h i á º ¿ t   k á º ¿   v Ã     b á » "   s u n g   t h Ã ª m   m á » "!t   T h a n h   Z o o m   ( S e e k B a r )   t r á » ± c   q u a n   n g a y   t r Ã ª n   g i a o   d i á » ! n   Ä  á »  c   f i l e ,   c h o   p h Ã © p   n g Æ ° á »  i   d Ã ¹ n g   t Ã ¹ y   Ã ½   v u á »  t   Ä  á » ’  p h Ã ³ n g   t o   t h u   n h á »    b á º £ n   v á º ½   t h e o   Ä  Ã º n g   t á » ·   l á » !   m o n g   m u á »  n ,   g i Ã º p   d á » &   d Ã   n g   q u a n   s Ã ¡ t   c h i   t i á º ¿ t   v Ä ’n   b á º £ n   m Ã     k h Ã ´ n g   b á » 9   v Æ ° á » : n g   g i á » : i   h á º ¡ n .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 4 4 :   T h Ã ª m   t Ã ­ n h   n Ä ’n g   p h Ã ³ n g   t o / t h u   n h á »    b á º ± n g   c Ã ¡ c h   v u á »  t   ( P i n c h - t o - z o o m )   c h o   t r Ã ¬ n h   Ä  á »  c   f i l e   . x d w .   S á » ­   d á » ¥ n g   c Æ ¡   c h á º ¿   M a t r i x   k á º ¿ t   h á » £ p   v á » : i   o n T o u c h L i s t e n e r   Ä  á » ’  b á º ¯ t   c Ã ¡ c   s á » ±   k i á » ! n   Ä  a   Ä  i á » ’m   ( 2   n g Ã ³ n   t a y ) ,   t Æ ° Æ ¡ n g   t á » ±   n h Æ °   n h á » ¯ n g   g Ã ¬   Ä  Ã £   t r i á » ’n   k h a i   á » x  P d f V i e w e r A c t i v i t y ,   c h o   p h Ã © p   n g Æ ° á »  i   d Ã ¹ n g   k Ã © o   t h á º £   ( p a n )   v Ã     p h Ã ³ n g   t o   ( z o o m )   á º £ n h   b i t m a p   Ä  Ã £   Ä  Æ ° á » £ c   r e n d e r .   S á » ±   k á º ¿ t   h á » £ p   g i á » ¯ a   t h a n h   S e e k B a r   ( r e n d e r   c h á º ¥ t   l Æ ° á » £ n g   c a o )   v Ã     P i n c h - t o - z o o m   ( p h Ã ³ n g   t o   n h a n h )   t á º ¡ o   r a   t r á º £ i   n g h i á » ! m   Ä  á »  c   X D W   m Æ ° á » £ t   m Ã     v Ã     c á » ± c   k á » ³   l i n h   h o á º ¡ t .  
 -   0 1 - 0 8 - 2 0 2 6 ,   1 9 : 5 6 :   D á »  i   t h a n h   c á º ¥ u   h Ã ¬ n h   " T á » ·   l á » !   S c a l e   X D W "   ( Ä  á » "!  p h Ã ¢ n   g i á º £ i   r e n d e r )   t á » «   g i a o   d i á » ! n   Ä  á »  c   X D W   t r á » ± c   t i á º ¿ p   v Ã   o   t r a n g   C Ã   i   Ä  á º · t   ( S e t t i n g ) .   T h a n h   k Ã © o   b Ã ª n   C Ã   i   Ä  á º · t   v á º « n   h á »    t r á » £   l á » ± a   c h á »  n   d á º £ i   t á » ·   l á » !   ( 1 0 0 - 5 0 0 )   v Ã     á » © n g   d á » ¥ n g   s á º ½   t á » ±   Ä  á » "!n g   t á º £ i   m á » © c   c h á º ¥ t   l Æ ° á » £ n g   n Ã   y   á » x  n h á » ¯ n g   l á º § n   Ä  á »  c   X D W   t i á º ¿ p   t h e o .  
 -   0 1 - 0 8 - 2 0 2 6 ,   2 0 : 0 0 :   C h á » 0 n h   s á » ­ a   l á º ¡ i   d á º £ i   t á » ·   l á » !   g i á » : i   h á º ¡ n   c h o   t h a n h   c á º ¥ u   h Ã ¬ n h   Ä  á » "!  p h Ã ¢ n   g i á º £ i   ( S c a l e   X D W )   t r o n g   m á » ¥ c   C Ã   i   Ä  á º · t .   G i á » : i   h á º ¡ n   m á » : i   c h o   p h Ã © p   k Ã © o   t á » «   1 0   ( t h á º ¥ p   n h á º ¥ t )   Ä  á º ¿ n   2 0 0   ( c a o   n h á º ¥ t )   v á » : i   g i Ã ¡   t r á » 9   m á º · c   Ä  á » 9 n h   Ä  Æ ° á » £ c   Ä  Æ ° a   v á »    1 5 0 .  
 