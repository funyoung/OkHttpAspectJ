# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class sogouime.security.** {*;}
-keep public class com.sogou.bu.basic.ui.AlertProgressDialog extends android.app.AlertDialog {*;}
-keep public class com.sogou.bu.basic.settings.SettingManager {
    public static synchronized com.sogou.bu.basic.settings.SettingManager getInstance(android.content.Context);
    public boolean getAllowDataConnection();
    public boolean getCaptureCancelStatus();
    public void setCaptureCancelStatus(boolean);
    public java.lang.String getVersionName();
    public java.lang.String getAndroidID();
}
-keep class * extends com.sogou.bu.basic.settings.modal.BaseSettingBean {*;}
-keep class com.sogou.common_components.vibratesound.vibrator.BaseVibrator {*;}
-keep class com.sogou.common_components.vibratesound.vibrator.IVibrator {*;}