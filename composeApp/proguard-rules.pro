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

# Suppress warnings for optional server-side/logging/appengine/grpc classes not present at runtime
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

# If HttpClient shaded classes emit notes, ignore
-dontwarn org.apache.http.**


# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# Compose runtime classes keep (usually safe)
-keep class androidx.compose.** { *; }

# Keep enums and data classes reflective names
-keepclassmembers enum * { *; }


# Additional suppressions to resolve unresolved references reported by ProGuard
-dontwarn kotlin.concurrent.atomics.**
-dontwarn kotlin.jvm.internal.EnhancedNullability
-dontwarn org.checkerframework.**
-dontwarn com.google.common.**


-keep class org.immutables.value.** { *; }
-dontwarn org.immutables.value.**

-dontwarn kotlinx.coroutines.slf4j.**
-dontwarn io.github.oshai.kotlinlogging.logback.internal.**
-dontwarn ch.qos.logback.**
-keep class io.github.oshai.kotlinlogging.logback.internal.** { *; }


-keep class com.google.api.services.youtube.YouTubeRequest { *; }
-keep class * extends com.google.api.services.youtube.YouTubeRequest { *; }
-keepclassmembers class * extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest {
    <fields>;
    <methods>;
}

-keep class com.google.api.client.util.Data { *; }
-keepclassmembers class com.google.api.client.util.Data {
    static <fields>;
}

-keepclassmembers class java.lang.Boolean {
    public static final java.lang.Boolean TRUE;
    public static final java.lang.Boolean FALSE;
}
