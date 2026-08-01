package com.mypdf.reader;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0002J\b\u0010\t\u001a\u00020\bH\u0002J\u0012\u0010\n\u001a\u00020\b2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0014J \u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0006H\u0002J\b\u0010\u0012\u001a\u00020\bH\u0002J\b\u0010\u0013\u001a\u00020\bH\u0002J,\u0010\u0014\u001a\u00020\b2\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00062\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\b0\u0018H\u0002J$\u0010\u0019\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\u00062\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\b0\u0018H\u0002J\u0010\u0010\u001a\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\u0006H\u0002J\b\u0010\u001c\u001a\u00020\bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/mypdf/reader/SettingsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/mypdf/reader/databinding/ActivitySettingsBinding;", "currentDownloadUrl", "", "checkUpdate", "", "initializeSyncState", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setColorButtonAppearance", "button", "Landroid/widget/TextView;", "hexColor", "labelText", "setupListeners", "setupUI", "showColorPickerDialog", "title", "currentHex", "onColorSelected", "Lkotlin/Function1;", "showCustomHexDialog", "startDownload", "url", "updateColorButtons", "app_debug"})
public final class SettingsActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.mypdf.reader.databinding.ActivitySettingsBinding binding;
    @org.jetbrains.annotations.Nullable
    private java.lang.String currentDownloadUrl;
    
    public SettingsActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
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