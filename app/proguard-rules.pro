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



# model classes depend on firebase naming conventions.
-keepclassmembers class watersport_training.model.** { *; }
-keepclassmembers class watersport_training.dto.** { *; }
-keepnames class nl.multimedia-engineer.watersport_training.model.** { *; }
-keepclassmembers class nl.multimedia-engineer.watersport_training.model.** { *; }
-keepclassmembers class nl.multimedia-engineer.watersport_training.dto.** { *; }


# Firebase
-keepnames class com.firebase.** { *; }
-keepnames class com.shaded.fasterxml.jackson.** { *; }
-keepnames class org.shaded.apache.** { *; }
-keepnames class javax.servlet.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.commons.logging.impl.**

#Croperion
-dontwarn com.mikelau.croperino.**
-keep class com.mikelau.croperino.** { *; }
-keep interface com.mikelau.croperino.** { *; }


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


# Glide for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#
#-keepclassmembers class * implements android.os.Parcelable {
#    static ** CREATOR;
#}

#Firebase
# Add this global rule
-keepattributes Signature

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**