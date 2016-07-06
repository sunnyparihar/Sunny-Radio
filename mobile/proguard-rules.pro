# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\android setup\android studio sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
  public *;
}

# The following rules are used to strip any non essential Google Play Services classes and method.

# For Google Play Services
-keep public class com.google.android.gms.ads.**{
   public *;
}

-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient

-keep public class com.google.android.gms.common.** {
 public *;
}
-keep public class com.google.android.gms.ads.** {
 public *;
}
-keep public class com.google.android.gms.** {
 public *;
}
-dontwarn com.google.android.gms.**
-keep public class com.google.ads.** {
 public *;
}

 -keepclassmembers class * {
    @android.webkit.JavascriptInterface public *;
}
-keep class org.lucasr.twowayview.** { *; }