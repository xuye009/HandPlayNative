LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES := touch/start.c
LOCAL_MODULE := s
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_EXECUTABLE)