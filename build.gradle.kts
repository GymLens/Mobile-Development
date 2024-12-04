plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Menambahkan plugin google-services
    id("com.google.gms.google-services") version "4.3.15" apply false  // Sesuaikan dengan versi yang diinginkan
}
