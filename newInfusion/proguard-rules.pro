# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:/install_path/android-sdk_r23.0.5-windows/android-sdk-windows/tools/proguard/proguard-android.txt
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

#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#混淆时是否记录日志
-verbose

# 我自己加的  指定代码的压缩级别
-optimizationpasses 5

#优化  不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify

# 我自己加的  混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService

# 我自己加的 保护注解
-keepattributes *JsonProperty*

# 我自己加的
#忽略警告
-ignorewarning

#####################记录生成的日志数据,gradle build时在本项目根目录输出################
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 我自己加的  保持自定义控件类不被混淆
-keepclasseswithmembernames class * {
   public <init>(android.content.Context, android.util.AttributeSet);
}
# 我自己加的  保持自定义控件类不被混淆
-keepclasseswithmembernames class * {
   public <init>(android.content.Context, android.util.AttributeSet, int);
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#我自己加的  保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
#保持枚举 enum 类不被混淆 如果混淆报错
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

#如果引用了v4或者v7包
-dontwarn android.support.**

#我自己加的  关于混淆library
#-libraryjars libs/base-release.aar
-keep class com.google.**

