cmake_minimum_required(VERSION 3.22.1)

# Добавление библиотеки native-lib
add_library(
        native-lib
        SHARED
        native-lib.cpp
)

# Подключение библиотеки логирования Android
find_library(
        log-lib
        log
)

# Линковка библиотеки с логированием
target_link_libraries(
        native-lib
        ${log-lib}
)