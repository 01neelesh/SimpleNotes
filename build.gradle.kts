// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.9.1" apply false
    id ("com.google.gms.google-services") version "4.4.2" apply false

}
buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.9.1")
        classpath ("com.google.gms:google-services:4.4.2")
    }
}

