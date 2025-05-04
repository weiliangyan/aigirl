# 保留基本配置
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions

# Kotlin 相关
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Compose 相关
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material.** { *; }

# Room 相关
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# TensorFlow Lite 相关
-keep class org.tensorflow.** { *; }
-keep class org.tensorflow.lite.** { *; }

# 数据模型
-keep class com.emotionalai.companion.model.** { *; }
-keep class com.emotionalai.companion.data.** { *; }

# 序列化相关
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留自定义View
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留R文件
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保留WebView
-keepclassmembers class * extends android.webkit.WebView {
    public *;
}

# 保留JavaScript接口
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# 保留自定义Application
-keep public class * extends android.app.Application

# 保留Activity
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment

# 保留Service
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# 保留自定义控件
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留自定义控件get/set方法
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# 保留自定义View的get/set方法
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get*();
}

# 保留自定义View的onDraw方法
-keepclassmembers class * extends android.view.View {
    protected void onDraw(android.graphics.Canvas);
}

# 保留自定义View的onMeasure方法
-keepclassmembers class * extends android.view.View {
    protected void onMeasure(int, int);
}

# 保留自定义View的onLayout方法
-keepclassmembers class * extends android.view.View {
    protected void onLayout(boolean, int, int, int, int);
}

# 保留自定义View的onTouchEvent方法
-keepclassmembers class * extends android.view.View {
    public boolean onTouchEvent(android.view.MotionEvent);
}

# 保留自定义View的onClick方法
-keepclassmembers class * extends android.view.View {
    public void onClick(android.view.View);
}

# 保留自定义View的onLongClick方法
-keepclassmembers class * extends android.view.View {
    public boolean onLongClick(android.view.View);
}

# 保留自定义View的onKey方法
-keepclassmembers class * extends android.view.View {
    public boolean onKey(android.view.View, int, android.view.KeyEvent);
}

# 保留自定义View的onKeyDown方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyDown(int, android.view.KeyEvent);
}

# 保留自定义View的onKeyUp方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyUp(int, android.view.KeyEvent);
}

# 保留自定义View的onKeyLongPress方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyLongPress(int, android.view.KeyEvent);
}

# 保留自定义View的onKeyMultiple方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyMultiple(int, int, android.view.KeyEvent);
}

# 保留自定义View的onKeyShortcut方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyShortcut(int, android.view.KeyEvent);
}

# 保留自定义View的onKeyDown方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyDown(int, android.view.KeyEvent);
}

# 保留自定义View的onKeyUp方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyUp(int, android.view.KeyEvent);
}

# 保留自定义View的onKeyLongPress方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyLongPress(int, android.view.KeyEvent);
}

# 保留自定义View的onKeyMultiple方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyMultiple(int, int, android.view.KeyEvent);
}

# 保留自定义View的onKeyShortcut方法
-keepclassmembers class * extends android.view.View {
    public boolean onKeyShortcut(int, android.view.KeyEvent);
} 