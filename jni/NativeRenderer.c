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

#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <android/bitmap.h>

#include "inc/refract.h"

// Logging macros
#define LOG_TAG    "librefract"
#define LOG_D(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOG_I(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOG_E(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

// Set to 1 to enable debug log traces
#define DEBUG 1

#define PALETTE_SIZE	128

/*jclass renderer_class;*/

/**
 * Called by JVM as library is being loaded
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved)
 {
	/*JNIEnv* env;
	if ((*jvm)->GetEnv(jvm, (void**)&env, JNI_VERSION_1_4)) {
		return JNI_ERR;
	}

	jclass cls = (*env)->FindClass(env, "com/ijuru/refract/NativeRenderer");
	if (cls == NULL)
		return JNI_ERR;

	renderer_class = (*env)->NewWeakGlobalRef(env, cls);
	if (renderer_class == NULL)
		return JNI_ERR;*/

	return JNI_VERSION_1_4;
 }

/**
 * Gets the context field of a Renderer object
 */
static refract_context* get_context(JNIEnv* env, jobject renderer) {
	jclass this_class = (*env)->GetObjectClass(env, renderer);
	jfieldID fid_context = (*env)->GetFieldID(env, this_class, "context", "J");
	return (refract_context*)(intptr_t)((*env)->GetLongField(env, renderer, fid_context));
}

/**
 * Called by JVM as library is being unloaded
 */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* jvm, void* reserved) {
	//JNIEnv *env;
	//if ((*jvm)->GetEnv(jvm, (void**)&env, JNI_VERSION_1_4))
		//return;

	//(*env)->DeleteWeakGlobalRef(env, renderer_class);
 }

/**
 * Allocates the context for the given renderer
 */
JNIEXPORT jboolean JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_allocate(JNIEnv* env, jobject renderer, jint width, jint height) {
	refract_context* context = refract_init((uint16_t)width, (uint16_t)height);

	if (context) {
		jclass this_class = (*env)->GetObjectClass(env, renderer);
		jfieldID fid_context = (*env)->GetFieldID(env, this_class, "context", "J");
		(*env)->SetLongField(env, renderer, fid_context, (jlong)(intptr_t)context);

		LOG_D("Allocated renderer internal resources");
		return true;
	}
	else {
		LOG_E("Unable to allocate renderer internal resources");
		return false;
	}
}

/**
 * Sets the iteration function
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setFunction(JNIEnv* env, jobject renderer, jobject function) {
	refract_context* context = get_context(env, renderer);

	jclass enum_class = (*env)->GetObjectClass(env, function);
	jmethodID ordinal_method = (*env)->GetMethodID(env, enum_class, "ordinal", "()I");
	func_t func = (func_t)(*env)->CallIntMethod(env, function, ordinal_method);

	context->params.func = func;
	context->cache_valid = false;
}

/**
 * Sets the iteration function
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setOffset(JNIEnv* env, jobject renderer, jdouble re, jdouble im) {
	refract_context* context = get_context(env, renderer);
	context->params.offset.re = (float_t)re;
	context->params.offset.im = (float_t)im;
	context->cache_valid = false;
}

/**
 * Sets the iteration function
 */
JNIEXPORT jdouble JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_getZoom(JNIEnv* env, jobject renderer) {
	refract_context* context = get_context(env, renderer);
	return (jdouble)context->params.zoom;
}

/**
 * Sets the iteration function
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setZoom(JNIEnv* env, jobject renderer, jdouble zoom) {
	refract_context* context = get_context(env, renderer);
	context->params.zoom = (float_t)zoom;
	context->cache_valid = false;
}

/**
 * Sets the palette
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setPalette(JNIEnv* env, jobject renderer, jobject palette) {
	refract_context* context = get_context(env, renderer);

	// Free existing palette
	if (context->palette) {
		refract_palette_free(context->palette);
		context->palette = NULL;

		LOG_D("Freed renderer palette");
	}

	// Get palette object fields
	jclass palette_class = (*env)->GetObjectClass(env, palette);
	jfieldID fid_colors = (*env)->GetFieldID(env, palette_class, "colors", "[I");
	jfieldID fid_anchors = (*env)->GetFieldID(env, palette_class, "anchors", "[F");
	jobject obj_colors = (*env)->GetObjectField(env, palette, fid_colors);
	jobject obj_anchors = (*env)->GetObjectField(env, palette, fid_anchors);

	jintArray* colors = (jintArray*)&obj_colors;
	jfloatArray* anchors = (jfloatArray*)&obj_anchors;

	// Get array elements
	jint* colorvals = (*env)->GetIntArrayElements(env, *colors, NULL);
	jfloat* anchorvals = (*env)->GetFloatArrayElements(env, *anchors, NULL);
	uint16_t points = (*env)->GetArrayLength(env, *colors);

	context->palette = refract_palette_init((color_t*)colorvals, (float*)anchorvals, points, PALETTE_SIZE);

	(*env)->ReleaseIntArrayElements(env, *colors, colorvals, 0);
	(*env)->ReleaseFloatArrayElements(env, *anchors, anchorvals, 0);

	LOG_D("Updated renderer palette");
}

/**
 * Updates (i.e. renders a frame) the refract context for the given FractalRenderer
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_iterate(JNIEnv* env, jobject obj, jint iters) {
	refract_context* context = get_context(env, obj);

	// Because multiple threads might modify this class, cache the parameters for these iterations
	params_t params = context->params;
	bool use_cache = context->cache_valid;
	context->cache_valid = true;

	refract_iterate(context, (iterc_t)iters, params, use_cache);

	return context->cache_max_iters;
}

/**
 * Updates (i.e. renders a frame) the refract context for the given FractalRenderer
 */
JNIEXPORT jboolean JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_render(JNIEnv* env, jobject obj, jobject bitmap) {
	refract_context* context = get_context(env, obj);

	AndroidBitmapInfo info;
	AndroidBitmap_getInfo(env, bitmap, &info);

	color_t* pixels;
	int ret;

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void**)&pixels)) < 0) {
		LOG_E("AndroidBitmap_lockPixels() failed: error=%d", ret);
		return (jboolean)false;
	}

	refract_render(context, pixels, info.stride);

	AndroidBitmap_unlockPixels(env, bitmap);
	return (jboolean)true;
}

/**
 * Frees the context for the given renderer
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_free(JNIEnv* env, jobject obj) {
	refract_context* context = get_context(env, obj);
	refract_free(context);

	LOG_D("Freed renderer internal resources");
}
