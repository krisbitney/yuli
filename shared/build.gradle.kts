plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("io.realm.kotlin") version "1.10.0"
}

kotlin {
    androidTarget()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    cocoapods {
        name = "shared"
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "13.0"

        pod("yuli_ios") {
            version = "1.0.0"
            source = path(project.file("/Users/kris/XcodeProjects/yuli_ios"))
            extraOpts = listOf("-compiler-option", "-fmodules")
        }
    }

    val decomposeVersion = extra["decompose.version"] as String
    val mviKotlinVersion = extra["mvi.version"] as String
    val ktorVersion = "2.3.4"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
                implementation("io.realm.kotlin:library-base:1.10.0")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin:$mviKotlinVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin-main:$mviKotlinVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$mviKotlinVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
                implementation("com.github.instagram4j:instagram4j:2.0.7")
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
            }
        }
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
        val androidInstrumentedTest by getting
        val iosSimulatorArm64Test by getting
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "io.github.krisbitney.yuli.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlin {
        jvmToolchain(11)
    }
}

// print stdout during tests
tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }
}
