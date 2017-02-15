#!/bin/bash
adb -d shell 'run-as  com.flir.flirone cat /data/data/com.flir.flirone/databases/heat_images_info.db> /sdcard/heat_images_info.db'
adb pull /sdcard/heat_images_info.db .
