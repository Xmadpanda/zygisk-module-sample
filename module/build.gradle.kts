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

        variant.assembleProvider.get().finalizedBy("copyLibs$buildType")

    }
}