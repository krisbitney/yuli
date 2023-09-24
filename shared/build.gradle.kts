plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("native.cocoapods")
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

    val decomposeVersion = "2.1.0"
    val mviKotlinVersion = "3.2.1"

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
                implementation("io.realm.kotlin:library-base:1.10.0")
                implementation("com.arkivanov.decompose:decompose:$decomposeVersion-compose-experimental")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion-compose-experimental")
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
            }
        }
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
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

tasks.register("copyFramework") {
    doLast {
        val srcFile = File("$projectDir/src/nativeInterop/frameworks/yuli_ios.xcframework/ios-arm64_x86_64-simulator/yuli_ios.framework/yuli_ios")
        val destDir = File("$buildDir/bin/iosSimulatorArm64/debugTest/Frameworks/yuli_ios.framework")
        destDir.mkdirs() // Create destination directory if it doesn't exist
        srcFile.copyTo(File(destDir, "yuli_ios"), overwrite = true)
    }
}
tasks.getByName("iosSimulatorArm64Test").dependsOn("copyFramework")