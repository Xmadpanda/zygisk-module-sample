plugins {
    alias(libs.plugins.android.lib)
}

android {
    namespace  = "module.dpzdev.zygisk"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments += "-DCMAKE_PROJECT_NAME=Sample"
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

afterEvaluate {
    android.libraryVariants.forEach { variant ->
        val buildType: String = variant.name.replaceFirstChar { it.uppercase() }

        // Task untuk menyalin file
        tasks.register<Copy>("copyLibs$buildType") {
            dependsOn("assemble${buildType}")
            println("Task : Module $buildType")

            val archTypes = listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            val srcBase = file("${project.layout.buildDirectory.get()}/intermediates/stripped_native_libs/${buildType.lowercase()}/strip${buildType}DebugSymbols/out/lib")

            archTypes.forEach { archType ->
                val srcFile = file("$srcBase/$archType/libSample.so")
                val trgDir = file("${project.layout.projectDirectory}/magisk/zygisk/$archType")
                val trgFile = file("$trgDir/$archType.so")

                if (srcFile.exists()) {
                    from(srcFile) {
                        into(trgDir)
                        rename { archType + ".so" } // Rename file sesuai arsitektur
                    }
                } else {
                    println("File tidak ditemukan : ${srcFile.toString()}")
                }
            }
        }

        variant.assembleProvider.get().finalizedBy("copyLibs$buildType")

        // Task untuk mengompres folder
        tasks.register<Zip>("compressMagisk") {
            dependsOn("copyLibs$buildType")
            from(file("${project.layout.projectDirectory}/magisk")) {
                include("**/*") // Menyertakan semua file dalam folder magisk
            }
            destinationDirectory.set(file("${project.layout.projectDirectory}/magisk"))
            archiveFileName.set("magisk.zip") // Nama file zip
        }

        // Menjalankan tugas kompres setelah menyalin libs
        tasks.named("copyLibs$buildType").get().finalizedBy("compressMagisk")
    }
} 
