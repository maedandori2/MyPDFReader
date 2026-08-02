package com.mypdf.reader;

/**
 * Trích xuất thông tin từ trang đầu PDF bằng ML Kit OCR (Japanese).
 * Tìm các trường: 品名, 自社品番, 自社品名
 *
 * Sử dụng bounding box (vị trí pixel) để xác định giá trị nằm
 * ở ô bên phải của key trong bảng, thay vì parse text thuần.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u00013B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J*\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u0018\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0082@\u00a2\u0006\u0002\u0010\u001aJ4\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\u0017H\u0002J \u0010\u001e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u001f\u001a\u00020\u00052\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\"0!H\u0002J \u0010#\u001a\u0004\u0018\u00010\"2\u0006\u0010\u001f\u001a\u00020\u00052\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\"0!H\u0002J\u0010\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u0005H\u0002J*\u0010\'\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010(\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0002\u0010\u0015Jq\u0010)\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00050!2K\u0010+\u001aG\u0012\u0013\u0012\u00110\u0007\u00a2\u0006\f\b-\u0012\b\b.\u0012\u0004\b\b(/\u0012\u0013\u0012\u00110\u0007\u00a2\u0006\f\b-\u0012\b\b.\u0012\u0004\b\b(0\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b-\u0012\b\b.\u0012\u0004\b\b(\u001c\u0012\u0004\u0012\u0002010,H\u0086@\u00a2\u0006\u0002\u00102R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u001b\u0010\n\u001a\u00020\u000b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\r\u00a8\u00064"}, d2 = {"Lcom/mypdf/reader/PdfTextExtractor;", "", "<init>", "()V", "TAG", "", "RENDER_WIDTH", "", "SAME_ROW_THRESHOLD_RATIO", "", "recognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "getRecognizer", "()Lcom/google/mlkit/vision/text/TextRecognizer;", "recognizer$delegate", "Lkotlin/Lazy;", "extractFromFirstPage", "", "context", "Landroid/content/Context;", "pdfPath", "(Landroid/content/Context;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "runOcr", "Lcom/google/mlkit/vision/text/Text;", "bitmap", "Landroid/graphics/Bitmap;", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "extractByBoundingBox", "fileName", "ocrResult", "findValueForKey", "key", "elements", "", "Lcom/mypdf/reader/PdfTextExtractor$OcrElement;", "findKeyElement", "isMetadataKey", "", "text", "extractFromXdwFirstPage", "xdwPath", "extractBatch", "filePaths", "onProgress", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "current", "total", "", "(Landroid/content/Context;Ljava/util/List;Lkotlin/jvm/functions/Function3;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "OcrElement", "app_debug"})
public final class PdfTextExtractor {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "PdfTextExtractor";
    private static final int RENDER_WIDTH = 1500;
    private static final double SAME_ROW_THRESHOLD_RATIO = 0.6;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy recognizer$delegate = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.mypdf.reader.PdfTextExtractor INSTANCE = null;
    
    private PdfTextExtractor() {
        super();
    }
    
    private final com.google.mlkit.vision.text.TextRecognizer getRecognizer() {
        return null;
    }
    
    /**
     * Trích xuất metadata từ trang đầu của file PDF.
     * @return Map<String, String> với các key tìm thấy (品名, 自社品番, 自社品名) và giá trị là đường dẫn ảnh.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object extractFromFirstPage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String pdfPath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.Map<java.lang.String, java.lang.String>> $completion) {
        return null;
    }
    
    /**
     * Chạy ML Kit Text Recognition trên Bitmap
     * Trả về Text object có chứa bounding box cho từng element
     */
    private final java.lang.Object runOcr(android.graphics.Bitmap bitmap, kotlin.coroutines.Continuation<? super com.google.mlkit.vision.text.Text> $completion) {
        return null;
    }
    
    /**
     * Trích xuất metadata dựa trên vị trí bounding box.
     * Cắt (crop) vùng ảnh bên phải của từ khoá và lưu thành file.
     */
    private final java.util.Map<java.lang.String, java.lang.String> extractByBoundingBox(android.content.Context context, java.lang.String fileName, android.graphics.Bitmap bitmap, com.google.mlkit.vision.text.Text ocrResult) {
        return null;
    }
    
    /**
     * Tìm giá trị cho 1 key:
     * 1. Tìm element chứa key text
     * 2. Tìm element nằm bên phải key, cùng hàng (Y center gần nhau)
     * 3. Chọn element gần nhất bên phải
     *
     * Xử lý đặc biệt: "自社品番" và "自社品名" có thể bị OCR tách thành
     * nhiều element, nên cũng tìm element nào text chứa key.
     */
    private final java.lang.String findValueForKey(java.lang.String key, java.util.List<com.mypdf.reader.PdfTextExtractor.OcrElement> elements) {
        return null;
    }
    
    /**
     * Tìm element chứa key text.
     * Ưu tiên exact match, sau đó match chứa key.
     * Xử lý trường hợp "自社品番"/"自社品名" có thể bị tách.
     */
    private final com.mypdf.reader.PdfTextExtractor.OcrElement findKeyElement(java.lang.String key, java.util.List<com.mypdf.reader.PdfTextExtractor.OcrElement> elements) {
        return null;
    }
    
    /**
     * Kiểm tra text có phải là key metadata không
     */
    private final boolean isMetadataKey(java.lang.String text) {
        return false;
    }
    
    /**
     * Trích xuất metadata từ trang đầu của file XDW.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object extractFromXdwFirstPage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String xdwPath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.Map<java.lang.String, java.lang.String>> $completion) {
        return null;
    }
    
    /**
     * Trích xuất metadata cho nhiều file, với callback progress
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object extractBatch(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> filePaths, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.lang.Integer, ? super java.lang.String, kotlin.Unit> onProgress, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    /**
     * Dữ liệu 1 element OCR với bounding box
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0015"}, d2 = {"Lcom/mypdf/reader/PdfTextExtractor$OcrElement;", "", "text", "", "box", "Landroid/graphics/Rect;", "<init>", "(Ljava/lang/String;Landroid/graphics/Rect;)V", "getText", "()Ljava/lang/String;", "getBox", "()Landroid/graphics/Rect;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    static final class OcrElement {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String text = null;
        @org.jetbrains.annotations.NotNull()
        private final android.graphics.Rect box = null;
        
        public OcrElement(@org.jetbrains.annotations.NotNull()
        java.lang.String text, @org.jetbrains.annotations.NotNull()
        android.graphics.Rect box) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getText() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Rect getBox() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Rect component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.mypdf.reader.PdfTextExtractor.OcrElement copy(@org.jetbrains.annotations.NotNull()
        java.lang.String text, @org.jetbrains.annotations.NotNull()
        android.graphics.Rect box) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}