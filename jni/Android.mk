LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := refract
LOCAL_SRC_FILES := NativeRenderer.c refract.c iterate.c
LOCAL_CFLAGS    := -std=c99
LOCAL_LDLIBS    := -lm -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
