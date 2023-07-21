plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
}

buildscript {
    dependencies {
//        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.17.3")
//        classpath("com.squareup.sqldelight:gradle-plugin:1.5.5")
    }
}

//allprojects {
//    apply(plugin = "kotlinx-atomicfu")
//}
