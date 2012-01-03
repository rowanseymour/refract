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
#include "palette.h"

// Logging macros
#define LOG_TAG    "librefract"
#define LOG_I(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOG_E(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

// Set to 1 to enable debug log traces
#define DEBUG 0

#define PALETTE_SIZE	256

/**
 * Gets the context field of a FractalRenderer object
 */
static refract_context* get_context(JNIEnv* env, jobject obj) {
	jclass this_class = (*env)->GetObjectClass(env, obj);
	jfieldID fid_context = (*env)->GetFieldID(env, this_class, "context", "J");
	return (refract_context*)(intptr_t)((*env)->GetLongField(env, obj, fid_context));
}

/**
 * Allocates the context for the given renderer
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_allocate(JNIEnv* env, jobject obj, jint width, jint height) {
	refract_context* context = refract_init(width, height);
	jclass this_class = (*env)->GetObjectClass(env, obj);

	if (context) {
		jfieldID fid_context = (*env)->GetFieldID(env, this_class, "context", "J");
		(*env)->SetLongField(env, obj, fid_context, (jlong)(intptr_t)context);

		LOG_I("Allocated renderer internal resources");
	}
	else {
		LOG_E("Unable to allocate renderer internal resources");
	}
}

/**
 * Sets the palette
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setPalette(JNIEnv* env, jobject obj, jintArray colors, jfloatArray anchors) {
	refract_context* context = get_context(env, obj);

	// Free existing palette
	if (context->palette) {
		refract_palette_free(context->palette);
		context->palette = NULL;
	}

	jint* colorvals = (*env)->GetIntArrayElements(env, colors, NULL);
	jfloat* anchorvals = (*env)->GetFloatArrayElements(env, anchors, NULL);
	int points = (*env)->GetArrayLength(env, colors);

	context->palette = refract_palette_init((color_t*)colorvals, (float*)anchorvals, points, PALETTE_SIZE);

	(*env)->ReleaseIntArrayElements(env, colors, colorvals, 0);
	(*env)->ReleaseFloatArrayElements(env, anchors, anchorvals, 0);

	LOG_I("Updated renderer palette");

	for (int p = 0; p < PALETTE_SIZE; ++p) {
		LOG_I("Palette[%d]=%08x", p, context->palette->colors[p]);
	}
}

/**
 * Updates (i.e. renders a frame) the refract context for the given FractalRenderer
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_render(JNIEnv* env, jobject obj, jobject bitmap, jdouble real, jdouble imag, jdouble zoom) {
	refract_context* context = get_context(env, obj);

	AndroidBitmapInfo info;
	AndroidBitmap_getInfo(env, bitmap, &info);

	color_t* pixels;
	int ret;

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void**)&pixels)) < 0) {
		LOG_E("AndroidBitmap_lockPixels() failed: error=%d", ret);
		return -1;
	}

	refract_render(context, pixels, info.stride, (float_t)real, (float_t)imag, (float_t)zoom);

	AndroidBitmap_unlockPixels(env, bitmap);

	return context->cache_max_iters;
}

/**
 * Frees the context for the given renderer
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_free(JNIEnv* env, jobject obj) {
	refract_context* context = get_context(env, obj);
	refract_free(context);

	LOG_I("Freed renderer internal resources");
}
