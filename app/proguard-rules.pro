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

# Moshi 반영(리플렉션/어댑터용 메타데이터 보존)
-keep class kotlin.Metadata { *; }
-keep class com.squareup.moshi.** { *; }
-keep class com.squareup.okio.** { *; }

# Retrofit 인터페이스(리플렉션으로 찾음)
-keep interface com.squareup.retrofit2.* { *; }
-dontwarn javax.annotation.**

# OkHttp/Okio 경고 무시(필요시)
-dontwarn okio.**