# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/zak/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-keep class com.qnexis.navfac.to14.** {*;}

-keep class javax.annotation.Nullable.** {*;}

#autovalue
-keep class javax.annotation.javax.lang.model.util.SimpleElementVisitor6.** {*;}
-keep class javax.lang.model.util.SimpleTypeVisitor6.** {*;}
-keep class org.jdom.*.** {*;}

# LeakCanary
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }

#-keep class XXXX.** {*;}
#-keep class XXXX.** {*;}
#-keep class XXXX.** {*;}
#-keep class XXXX.** {*;}
#-keep class XXXX.** {*;}
#-keep class XXXX.** {*;}





-dontwarn **.*

#allow debugging
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable



#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

## Joda Time 2.3
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }


##guava
-dontwarn sun.misc.**
-dontwarn com.google.common.collect.MinMaxPriorityQueue
#-injars path/to/myapplication.jar
#-injars lib/guava-r07.jar
#-libraryjars lib/jsr305.jar
#-outjars myapplication-dist.jar
#
#-dontoptimize
#-dontobfuscate
#-dontwarn sun.misc.Unsafe
#-dontwarn com.google.common.collect.MinMaxPriorityQueue
#
#-keepclasseswithmembers public class * {
#    public static void main(java.lang.String[]);
#}

#dagger
-dontwarn dagger.internal.codegen.**
-dontwarn dagger.shaded.auto.common.**


#support library
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

# For RxJava:
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.** { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------


#Icepick
-dontwarn icepick.**
-keep class **$$Icicle { *; }
-keepnames class * { @icepick.Icicle *;}
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}

#PDF
-keep class com.shockwave.**

