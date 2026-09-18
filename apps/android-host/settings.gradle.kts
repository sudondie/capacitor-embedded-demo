/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
val snapshotVersion : String? = System.getenv("COMPOSE_SNAPSHOT_ID")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/org/jetbrains/kotlin/kotlin-compose-compiler-plugin/2.0.0-RC2-200/") }
    }
}
dependencyResolutionManagement {
    // Capacitor and Capgo are Gradle subprojects and declare the same trusted
    // Google/Maven Central repositories as their generated shell normally does.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        snapshotVersion?.let {
            println("https://androidx.dev/snapshots/builds/$it/artifacts/repository/") 
            maven { url = uri("https://androidx.dev/snapshots/builds/$it/artifacts/repository/") }
            maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/org/jetbrains/kotlin/kotlin-compose-compiler-plugin/2.0.0-RC2-200/") }
        }

        google()
        mavenCentral()
    }
}
rootProject.name = "Jetsnack"
include(":app")

// This host owns the Android shell. Unlike a generated Capacitor project it
// must wire Capacitor plugins explicitly from the embedded web feature.
include(":capacitor-android")
project(":capacitor-android").projectDir = file("../profile-shell/node_modules/@capacitor/android/capacitor")

include(":capgo-capacitor-updater")
project(":capgo-capacitor-updater").projectDir = file("../profile-shell/node_modules/@capgo/capacitor-updater/android")

include(":capacitor-geolocation")
project(":capacitor-geolocation").projectDir = file("../profile-shell/node_modules/@capacitor/geolocation/android")
