import com.android.utils.cxx.io.hasExtensionIgnoreCase

plugins {
    alias(libs.plugins.android.lib)
}

val libName = "dummy"

android {
    namespace  = "module.dpzdev.zygisk"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments += "-DCMAKE_PROJECT_NAME=${libName}"
            }
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

afterEvaluate() {
    android.libraryVariants.forEach { variant ->
        val buildType: String = variant.name.replaceFirstChar { it.uppercase() }
        tasks.register<Copy>("copyLibs$buildType") {
            dependsOn("assemble${buildType}")
            println("Task : Module $buildType")

            val trg = file("${project.layout.projectDirectory}/magisk/libs")
            val src = file("${project.layout.buildDirectory.get()}/intermediates/stripped_native_libs" +
                    "/${buildType.lowercase()}/strip${buildType}DebugSymbols/out/lib")
            if (src.exists()) {
                from(src)
/*
module/build/intermediates/stripped_native_libs/$type/strip$TypeDebugSymbols/out/lib/$type
*/
                into(trg)
            } else {
                println("File tidak ditemukan : ${src.toString()}")
            }
        }
        tasks.register<Copy>("renameLibs$buildType") {
            dependsOn("copyLibs${buildType}")
            val archs = listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            archs.forEach() { arch ->
                val libs = "${project.layout.buildDirectory.get()}/magisk/libs/$arch/lib$libName.so"
                val oldName = "lib$libName.so"
                val newName = "$arch.so"
                val destinationFolder = "${project.layout.buildDirectory}/magisk/zygisk"
                val src = file(libs)
                val dst = file(destinationFolder)
                from(src) {
                    include(oldName)
                    rename(oldName, newName)
                }
                into(dst)
            }

        }

        variant.assembleProvider.get().finalizedBy("renameLibs$buildType")

    }
}