LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := refract
LOCAL_SRC_FILES := FractalRenderer.c refract.c 
LOCAL_LDLIBS    := -lm -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
