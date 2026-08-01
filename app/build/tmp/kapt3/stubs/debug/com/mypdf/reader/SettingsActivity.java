package com.mypdf.reader;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0014J\b\u0010\f\u001a\u00020\tH\u0002J\b\u0010\r\u001a\u00020\tH\u0002J \u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u0007H\u0002J\b\u0010\u0013\u001a\u00020\tH\u0002J,\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0016\u001a\u00020\u00072\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\t0\u0018H\u0002J$\u0010\u0019\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u00072\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\t0\u0018H\u0002J\b\u0010\u001a\u001a\u00020\tH\u0002J\u0010\u0010\u001b\u001a\u00020\t2\u0006\u0010\u001c\u001a\u00020\u0007H\u0002J\b\u0010\u001d\u001a\u00020\tH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/mypdf/reader/SettingsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/mypdf/reader/databinding/ActivitySettingsBinding;", "currentDownloadUrl", "", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "setupUI", "updateColorButtons", "setColorButtonAppearance", "button", "Landroid/widget/TextView;", "hexColor", "labelText", "setupListeners", "showColorPickerDialog", "title", "currentHex", "onColorSelected", "Lkotlin/Function1;", "showCustomHexDialog", "checkUpdate", "startDownload", "url", "initializeSyncState", "app_debug"})
public final class SettingsActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.mypdf.reader.databinding.ActivitySettingsBinding binding;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentDownloadUrl;
    
    public SettingsActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupUI() {
    }
    
    private final void updateColorButtons() {
    }
    
    private final void setColorButtonAppearance(android.widget.TextView button, java.lang.String hexColor, java.lang.String labelText) {
    }
    
    private final void setupListeners() {
    }
    
    private final void showColorPickerDialog(java.lang.String title, java.lang.String currentHex, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onColorSelected) {
    }
    
    private final void showCustomHexDialog(java.lang.String currentHex, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onColorSelected) {
    }
    
    private final void checkUpdate() {
    }
    
    private final void startDownload(java.lang.String url) {
    }
    
    private final void initializeSyncState() {
    }
}