plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("io.realm.kotlin") version "1.10.0"
}

kotlin {
    androidTarget()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        val fwTarget = if (it.name == "iosArm64") "ios-arm64" else "ios-arm64_x86_64-simulator"
        val fwDir = File(projectDir, "src/nativeInterop/frameworks/yuli_ios.xcframework/$fwTarget").absolutePath
        val compilerLinkerOpts = listOf("-F$fwDir", "-framework", "yuli_ios")

        it.compilations.getByName("main") {
            val yuli_ios by cinterops.creating {
                defFile("src/nativeInterop/cinterop/yuli_ios.def")
                includeDirs("$fwDir/yuli_ios.framework/Headers")
                compilerOpts(compilerLinkerOpts)
            }
        }
        it.binaries.all {
            linkerOpts(compilerLinkerOpts)
        }
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    tasks.named("linkDebugFrameworkIosSimulatorArm64").configure {
        outputs.cacheIf { false }
    }

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

