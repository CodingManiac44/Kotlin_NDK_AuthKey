cmake_minimum_required(VERSION 3.22.1)
project("crypto-lib")

add_library(crypto-lib SHARED
    native-lib.c
    aes.c
)

find_library(log-lib log)

target_link_libraries(crypto-lib
    ${log-lib})
