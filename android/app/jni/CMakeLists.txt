cmake_minimum_required(VERSION 3.22.1)

project(David LANGUAGES C CXX)

# APP_VERSION comes from ../.cxx-version/david_version.h, written by build.gradle.

get_filename_component(DAVID_ROOT "${CMAKE_CURRENT_LIST_DIR}/../../.." ABSOLUTE)

set(SDL_SHARED ON CACHE BOOL "Build SDL as a shared library" FORCE)
set(SDL_STATIC OFF CACHE BOOL "Disable the unused SDL static library" FORCE)
set(SDL_TESTS OFF CACHE BOOL "Disable SDL tests" FORCE)
add_subdirectory("${DAVID_ROOT}/android-sdl" "${CMAKE_BINARY_DIR}/SDL" EXCLUDE_FROM_ALL)

file(GLOB_RECURSE DAVID_C_SOURCES CONFIGURE_DEPENDS
    "${DAVID_ROOT}/app/src/*.c"
    "${DAVID_ROOT}/thermite/src/*.c")
file(GLOB_RECURSE DAVID_CXX_SOURCES CONFIGURE_DEPENDS
    "${DAVID_ROOT}/app/src/*.cpp"
    "${DAVID_ROOT}/thermite/src/*.cpp")

list(FILTER DAVID_CXX_SOURCES EXCLUDE REGEX "imgui_impl_glfw\\.cpp$")

add_library(main SHARED ${DAVID_C_SOURCES} ${DAVID_CXX_SOURCES})

target_compile_features(main PRIVATE c_std_11 cxx_std_17)
target_compile_definitions(main PRIVATE
    SETTINGS_VERSION="2.8"
    CIMGUI_NO_EXPORT
    CGLM_FORCE_DEPTH_ZERO_TO_ONE
    MG_ENABLE_IPV6=1
    DAVID_SDL_JNI=1)
target_include_directories(main PRIVATE
    "${DAVID_ROOT}/app/src"
    "${CMAKE_CURRENT_LIST_DIR}/../.cxx-version"
    "${DAVID_ROOT}/thermite/include"
    "${DAVID_ROOT}/thermite/src"
    "${DAVID_ROOT}/android-sdl/include")
target_compile_options(main PRIVATE
    $<$<COMPILE_LANGUAGE:C>:-Wall;-Wextra;-Wno-unused-parameter>
    $<$<COMPILE_LANGUAGE:CXX>:-Wall;-Wextra;-Wno-unused-parameter>)

find_library(ANDROID_LIB android REQUIRED)
find_library(LOG_LIB log REQUIRED)
find_library(VULKAN_LIB vulkan REQUIRED)

target_link_libraries(main PRIVATE
    SDL3::SDL3
    ${ANDROID_LIB}
    ${LOG_LIB}
    ${VULKAN_LIB})
