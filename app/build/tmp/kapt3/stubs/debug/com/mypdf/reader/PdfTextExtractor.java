package com.mypdf.reader;

/**
 * Trích xuất thông tin từ trang đầu PDF bằng ML Kit OCR (Japanese).
 * Tìm các trường: 品名, 自社品番, 自社品名
 *
 * Sử dụng bounding box (vị trí pixel) để xác định giá trị nằm
 * ở ô bên phải của key trong bảng, thay vì parse text thuần.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001.B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002Jl\u0010\u000f\u001a\u00020\u00042\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\b0\u00112K\u0010\u0012\u001aG\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0016\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0017\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0018\u0012\u0004\u0012\u00020\u00190\u0013H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001aJ\u001c\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J%\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u001c2\u0006\u0010 \u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010!J \u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010$\u001a\u00020\b2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020#0\u0011H\u0002J \u0010&\u001a\u0004\u0018\u00010\b2\u0006\u0010$\u001a\u00020\b2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020#0\u0011H\u0002J\u0010\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020\bH\u0002J\u001b\u0010*\u001a\u0004\u0018\u00010\u001e2\u0006\u0010+\u001a\u00020,H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010-R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\f\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006/"}, d2 = {"Lcom/mypdf/reader/PdfTextExtractor;", "", "()V", "RENDER_WIDTH", "", "SAME_ROW_THRESHOLD_RATIO", "", "TAG", "", "recognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "getRecognizer", "()Lcom/google/mlkit/vision/text/TextRecognizer;", "recognizer$delegate", "Lkotlin/Lazy;", "extractBatch", "filePaths", "", "onProgress", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "current", "total", "fileName", "", "(Ljava/util/List;Lkotlin/jvm/functions/Function3;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "extractByBoundingBox", "", "ocrResult", "Lcom/google/mlkit/vision/text/Text;", "extractFromFirstPage", "pdfPath", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "findKeyElement", "Lcom/mypdf/reader/PdfTextExtractor$OcrElement;", "key", "elements", "findValueForKey", "isMetadataKey", "", "text", "runOcr", "bitmap", "Landroid/graphics/Bitmap;", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "OcrElement", "app_debug"})
public final class PdfTextExtractor {
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = "PdfTextExtractor";
    private static final int RENDER_WIDTH = 1500;
    private static final double SAME_ROW_THRESHOLD_RATIO = 0.6;
    @org.jetbrains.annotations.NotNull
    private static final kotlin.Lazy recognizer$delegate = null;
    @org.jetbrains.annotations.NotNull
    public static final com.mypdf.reader.PdfTextExtractor INSTANCE = null;
    
    private PdfTextExtractor() {
        super();
    }
    
    private final com.google.mlkit.vision.text.TextRecognizer getRecognizer() {
        return null;
    }
    
    /**
     * Trích xuất metadata từ trang đầu của file PDF.
     * @return Map<String, String> với các key tìm thấy (品名, 自社品番, 自社品名)
     */
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object extractFromFirstPage(@org.jetbrains.annotations.NotNull
    java.lang.String pdfPath, @org.jetbrains.annotations.NotNull
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
     *
     * Logic:
     * 1. Thu thập tất cả element OCR với bounding box
     * 2. Tìm element chứa key (品名, 自社品番, 自社品名)
     * 3. Tìm element nằm ngay bên PHẢI key, cùng dòng (Y gần nhau)
     * 4. Element đó là giá trị cần lấy
     */
    private final java.util.Map<java.lang.String, java.lang.String> extractByBoundingBox(com.google.mlkit.vision.text.Text ocrResult) {
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
     * Trích xuất metadata cho nhiều file, với callback progress
     */
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object extractBatch(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> filePaths, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.lang.Integer, ? super java.lang.String, kotlin.Unit> onProgress, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    /**
     * Dữ liệu 1 element OCR với bounding box
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0014"}, d2 = {"Lcom/mypdf/reader/PdfTextExtractor$OcrElement;", "", "text", "", "box", "Landroid/graphics/Rect;", "(Ljava/lang/String;Landroid/graphics/Rect;)V", "getBox", "()Landroid/graphics/Rect;", "getText", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    static final class OcrElement {
        @org.jetbrains.annotations.NotNull
        private final java.lang.String text = null;
        @org.jetbrains.annotations.NotNull
        private final android.graphics.Rect box = null;
        
        public OcrElement(@org.jetbrains.annotations.NotNull
        java.lang.String text, @org.jetbrains.annotations.NotNull
        android.graphics.Rect box) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getText() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.graphics.Rect getBox() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.graphics.Rect component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.mypdf.reader.PdfTextExtractor.OcrElement copy(@org.jetbrains.annotations.NotNull
        java.lang.String text, @org.jetbrains.annotations.NotNull
        android.graphics.Rect box) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
}