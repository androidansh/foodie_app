buildscript {
    val agp_version by extra("8.2.2")
    val agp_version1 by extra("8.2.2")
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
    repositories {
        mavenCentral()
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

