
# Keep Gson classes and model types (use reflection)
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.** { *; }
-keep class com.google.gson.internal.** { *; }
-keep class com.google.json.** { *; }

# Keep Google API client classes which may use reflection
-keep class com.google.** { *;}
-keep class * extends com.google.api.client.json.** { *; }
-keep class * extends com.google.api.client.util.** { *; }
-keep class * extends com.google.api.services.** { *; }

# Keep all classes with @Key annotations and their fields
-keep class * {
  @com.google.api.client.util.Key <fields>;
}

# Keep all YouTube API model classes
-keep class com.google.api.services.youtube.model.** { *; }

# Keep all fields and methods in classes that have @Key annotated fields
-keepclassmembers class * {
  @com.google.api.client.util.Key *;
}

# Keep all classes that extend YouTubeRequest and all their members
-keep class * extends com.google.api.services.youtube.YouTubeRequest { *; }

# Keep the YouTubeRequest class itself completely
-keep class com.google.api.services.youtube.YouTubeRequest { *; }

# Additionally, keep all nested classes and inner classes
-keep class com.google.api.services.youtube.YouTubeRequest$* { *; }
-keep class * extends com.google.api.services.youtube.YouTubeRequest$* { *; }

# Apache HTTP / Commons utils used by google-api-client
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.logging.** { *; }

# Suppress warnings for optional server-side/logging/appengine/grpc classes
-dontwarn javax.servlet.**
-dontwarn org.apache.avalon.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.commons.logging.LogSource
-dontwarn org.apache.log4j.**
-dontwarn org.apache.log.**
-dontwarn com.google.appengine.**
-dontwarn com.google.apphosting.**
-dontwarn io.grpc.override.**
-dontwarn io.opencensus.**
-dontwarn com.google.common.util.concurrent.MoreExecutors
-dontwarn org.apache.http.**
-dontwarn com.sun.net.httpserver.**

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# Compose runtime classes
-keep class androidx.compose.** { *; }

# Keep enums and data classes
-keepclassmembers enum * { *; }

# Additional suppressions
-dontwarn kotlin.concurrent.atomics.**
-dontwarn kotlin.jvm.internal.EnhancedNullability
-dontwarn org.checkerframework.**
-dontwarn com.google.common.**
-dontwarn org.immutables.value.**
-dontwarn kotlinx.coroutines.slf4j.**
-dontwarn io.github.oshai.kotlinlogging.logback.internal.**
-dontwarn ch.qos.logback.**
-dontwarn org.jetbrains.kotlinx.jupyter.**
-dontwarn org.jetbrains.kotlinx.dataframe.jupyter.**
-dontwarn kotlinx.datetime.**
-dontwarn org.jetbrains.kotlinx.dataframe.impl.**
-dontwarn io.grpc.**
-dontwarn io.opencensus.**
-dontwarn android.os.**
-dontwarn sun.misc.**
-dontwarn org.apache.log4j.**

# Keep immutables
-keep class org.immutables.value.** { *; }

# Keep logging
-keep class io.github.oshai.kotlinlogging.logback.internal.** { *; }

# Keep Google API request classes
-keep class com.google.api.services.youtube.YouTubeRequest { *; }
-keep class * extends com.google.api.services.youtube.YouTubeRequest { *; }
-keepclassmembers class * extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest {
    <fields>;
    <methods>;
}

# Keep Data utility class
-keep class com.google.api.client.util.Data { *; }
-keepclassmembers class com.google.api.client.util.Data {
    static <fields>;
}

# Keep Boolean constants
-keepclassmembers class java.lang.Boolean {
    public static final java.lang.Boolean TRUE;
    public static final java.lang.Boolean FALSE;
}
