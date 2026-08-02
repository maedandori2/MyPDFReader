# Keep all data classes and models for Gson and Room serialization
-keep class com.mypdf.reader.** { *; }
-keepclassmembers class com.mypdf.reader.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# XDW JNI - Keep all classes in the Fuji Xerox package because
# native .so libraries reference them by exact package/class/field names.
# If ProGuard renames or removes them, the app crashes at native level.
-keep class jp.co.fujixerox.** { *; }
-keepclassmembers class jp.co.fujixerox.** { *; }
-dontwarn jp.co.fujixerox.**

-keep class com.fujifilm.fb.** { *; }
-keepclassmembers class com.fujifilm.fb.** { *; }
-dontwarn com.fujifilm.fb.**

