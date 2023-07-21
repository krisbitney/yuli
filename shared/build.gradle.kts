plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
//    id("com.squareup.sqldelight")
}

kotlin {
    android()
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64()
    ).forEach {
        it.compilations.getByName("main") {
            cinterops {
                val yuli_ios by creating {
//                    val fwTarget = if (it.name == "iosArm64") "ios-arm64" else "ios-arm64_x86_64-simulator"
                    val fwDir = "src/nativeInterop/frameworks/yuli_ios.xcframework/ios-arm64_x86_64-simulator/yuli_ios.framework"
                    defFile("src/nativeInterop/cinterop/yuli_ios.def")
                    includeDirs("$fwDir/Headers")
                    compilerOpts("-F$fwDir", "-framework", "yuli_ios")
                }
            }
        }
    }

    cocoapods {
        version = "1.0.0"
        summary = "Yuli app"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    val coroutinesVersion = "1.7.2"
//    val sqlDelightVersion = "1.5.5"
    val dateTimeVersion = "0.4.0"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
//                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$dateTimeVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
//                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
                implementation("com.github.instagram4j:instagram4j:2.0.7")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
//                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
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
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

//val downloads: Provider<Directory> = project.layout.buildDirectory.dir("downloads")
//val frameworks = File("src/nativeInterop/frameworks")
//val downloadYuliIosApi = tasks.register<Download>("downloadYuliIosApi") {
//    src("https://raw.githubusercontent.com/krisbitney/yuli-ios-api/main/yuli_ios.zip")
//    dest(downloads.get().asFile)
//    overwrite(false)
//}
//val unpackYuliIosApi = tasks.register<Copy>("unpackYuliIosApi") {
//    from(zipTree("${downloads.get().asFile}/yuli_ios.zip"))
//    into(frameworks)
//    dependsOn(downloadYuliIosApi)
//}
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.CInteropProcess>().configureEach {
//    dependsOn(unpackYuliIosApi)
//    mustRunAfter(unpackYuliIosApi)
//}
//tasks.build {
//    dependsOn(unpackYuliIosApi)
//    mustRunAfter(unpackYuliIosApi)
//}
