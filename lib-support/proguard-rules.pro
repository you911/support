-ignorewarnings                                     # 是否忽略警告
-optimizationpasses 5                               # 指定代码的压缩级别(在0~7之间，默认为5)
-dontusemixedcaseclassnames                         # 是否使用大小写混合(windows大小写不敏感，建议加入)
-dontskipnonpubliclibraryclasses                    # 是否混淆非公共的库的类
-dontskipnonpubliclibraryclassmembers               # 是否混淆非公共的库的类的成员
-dontpreverify                                      # 混淆时是否做预校验(Android不需要预校验，去掉可以加快混淆速度)
-verbose                                            # 混淆时是否记录日志(混淆后会生成映射文件)

#指定外部模糊字典
-obfuscationdictionary dictionary1.txt
#指定class模糊字典
-classobfuscationdictionary dictionary1.txt
#指定package模糊字典
-packageobfuscationdictionary dictionary2.txt

#混淆时所采用的算法(谷歌推荐算法)
-optimizations !code/simplification/arithmetic,!field,!class/merging,!code/allocation/variable

#添加支持的jar(引入libs下的所有jar包)
-libraryjars libs(*.jar;)

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile

#保持注解不被混淆
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

#保持泛型不被混淆
-keepattributes Signature
#保持反射不被混淆
-keepattributes EnclosingMethod
#保持异常不被混淆
-keepattributes Exceptions
#保持内部类不被混淆
-keepattributes Exceptions,InnerClasses
#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

#------------------------------------默认保留区--------------------------------------#
#保持基本组件不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

#Support包规则
-dontwarn android.support.**
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

#保留在Activity中的方法参数是view的方法(避免布局文件里面onClick被影响)
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保持R(资源)下的所有类及其方法不能被混淆
-keep class **.R$* { *; }


-keep class tech.wcw.support.os.** {*;}
