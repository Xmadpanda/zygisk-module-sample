cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME "samplemodule")
if(DEFINED ENV{CMAKE_PROJECT_NAME})
        set(PROJECT_NAME $ENV{CMAKE_PROJECT_NAME})
endif()

project(${PROJECT_NAME})

add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        example.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log)