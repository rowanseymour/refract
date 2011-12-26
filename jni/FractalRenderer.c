/**
 * Copyright 2011 Rowan Seymour
 *
 * This file is part of Refract.
 *
 * Refract is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Refract is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Refract. If not, see <http://www.gnu.org/licenses/>.
 */

#include <android/log.h>
#include <android/bitmap.h>

#include "refract.h"

#define LOG_TAG    "librefract"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

// Set to 1 to enable debug log traces
#define DEBUG 0

/**
 * Gets the context field of a FractalRenderer object
 */
static refract_context* get_context(JNIEnv* env, jobject obj) {
	jclass this_class = (*env)->GetObjectClass(env, obj);
	jfieldID fid_context = (*env)->GetFieldID(env, this_class, "context", "J");
	return (refract_context*)(intptr_t)((*env)->GetLongField(env, obj, fid_context));
}

JNIEXPORT void JNICALL Java_com_ijuru_refract_FractalRenderer_allocateInternal(JNIEnv* env, jobject obj) {
	jclass this_class = (*env)->GetObjectClass(env, obj);
	jfieldID fid_bitmap = (*env)->GetFieldID(env, this_class, "bitmap", "Landroid/graphics/Bitmap;");
	jobject obj_bitmap = (*env)->GetObjectField(env, obj, fid_bitmap);

	AndroidBitmapInfo info;
	AndroidBitmap_getInfo(env, obj_bitmap, &info);

	refract_context* context = refract_init(info.width, info.height, info.stride);

	jfieldID fid_context = (*env)->GetFieldID(env, this_class, "context", "J");
	(*env)->SetLongField(env, obj, fid_context, (jlong)(intptr_t)context);
}

JNIEXPORT void JNICALL Java_com_ijuru_refract_FractalRenderer_updateInternal(JNIEnv* env, jobject obj) {
	jclass this_class = (*env)->GetObjectClass(env, obj);
	jfieldID fid_bitmap = (*env)->GetFieldID(env, this_class, "bitmap", "Landroid/graphics/Bitmap;");
	jobject obj_bitmap = (*env)->GetObjectField(env, obj, fid_bitmap);

	void* pixels;
	int ret;

	if ((ret = AndroidBitmap_lockPixels(env, obj_bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed: error=%d", ret);
	}

	refract_context* context = get_context(env, obj);
	refract_render(context, pixels);

	AndroidBitmap_unlockPixels(env, obj_bitmap);
}

JNIEXPORT void JNICALL Java_com_ijuru_refract_FractalRenderer_freeInternal(JNIEnv* env, jobject obj) {
	refract_context* context = get_context(env, obj);
	refract_free(context);
}
