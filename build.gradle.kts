buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("gradle.plugin.com.onesignal:onesignal-gradle-plugin:0.14.0")
    }
}
plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.android.library") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.dagger.hilt.android") version "2.45" apply false
}

var keyId = ""
var password = ""
var secretKeyRingFile = ""
var ossrhUsername = ""
var ossrhPassword = ""
var sonatypeStagingProfileId = ""
val secretPropsFile: File = project.rootProject.file("gradle.properties")

if (secretPropsFile.exists()) {
    val keystoreProperties = java.util.Properties()
    val stream = java.io.FileInputStream(secretPropsFile)
    keystoreProperties.load(stream)
    sonatypeStagingProfileId = keystoreProperties.getProperty("sonatypeStagingProfileId")
    ossrhUsername = keystoreProperties.getProperty("ossrhUsername")
    ossrhPassword = keystoreProperties.getProperty("ossrhPassword")
    keyId = keystoreProperties.getProperty("signing.keyId")
    password = keystoreProperties.getProperty("signing.password")
    secretKeyRingFile = keystoreProperties.getProperty("signing.secretKeyRingFile")
} else {
    // Use system environment variables
    ossrhUsername = System.getenv("OSSRH_USERNAME")
    ossrhPassword = System.getenv("OSSRH_PASSWORD")
    sonatypeStagingProfileId = System.getenv("SONATYPE_STAGING_PROFILE_ID")
    keyId = System.getenv("SIGNING_KEY_ID")
    password = System.getenv("SIGNING_PASSWORD")
    secretKeyRingFile = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
}

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(sonatypeStagingProfileId)
            username.set(ossrhUsername)
            password.set(ossrhPassword)
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

