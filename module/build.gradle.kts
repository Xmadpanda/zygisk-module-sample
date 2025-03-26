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
