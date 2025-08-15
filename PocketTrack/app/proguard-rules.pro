# Keep model classes used by Room and Gson (if used later)
-keep class androidx.room.** { *; }
-keep class com.pockettrack.data.** { *; }
-dontwarn org.jetbrains.annotations.**