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

#define REFRACT_LIB_VERSION "1.0.1"

// Logging macros
#define LOG_TAG    "librefract"
#define LOG_D(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOG_I(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOG_E(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

// Set to 1 to enable debug log traces
#define DEBUG 1

#define NATIVERENDERER_CLASS	"com/ijuru/refract/renderer/jni/NativeRenderer"
#define FUNCTION_CLASS			"com/ijuru/refract/renderer/Function"
#define COMPLEX_CLASS			"com/ijuru/refract/renderer/Complex"
#define MAPPING_CLASS			"com/ijuru/refract/renderer/Mapping"

// Cached Java entities
jclass nativerenderer_class, function_class, complex_class, mapping_class;
jfieldID nativerenderer_renderer_fid, complex_re_fid, complex_im_fid;
jmethodID function_ordinal_mid, function_values_mid, complex_cid, mapping_ordinal_mid, mapping_values_mid;

/**
 * Called by JVM as library is being loaded
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved)
 {
	// Get JNI environment
	JNIEnv* env;
	if ((*jvm)->GetEnv(jvm, (void**)&env, JNI_VERSION_1_4))
		return JNI_ERR;

	// Cache Java classes
	jclass cls = (*env)->FindClass(env, NATIVERENDERER_CLASS);
	nativerenderer_class = (*env)->NewGlobalRef(env, cls);
	nativerenderer_renderer_fid = (*env)->GetFieldID(env, nativerenderer_class, "renderer", "J");

	cls = (*env)->FindClass(env, FUNCTION_CLASS);
	function_class = (*env)->NewGlobalRef(env, cls);
	function_ordinal_mid = (*env)->GetMethodID(env, function_class, "ordinal", "()I");

	cls = (*env)->FindClass(env, COMPLEX_CLASS);
	complex_class = (*env)->NewGlobalRef(env, cls);
	complex_re_fid = (*env)->GetFieldID(env, complex_class, "re", "D");
	complex_im_fid = (*env)->GetFieldID(env, complex_class, "im", "D");

	cls = (*env)->FindClass(env, MAPPING_CLASS);
	mapping_class = (*env)->NewGlobalRef(env, cls);
	mapping_ordinal_mid = (*env)->GetMethodID(env, mapping_class, "ordinal", "()I");

	LOG_D("Loaded library (version %s)", REFRACT_LIB_VERSION);

	return JNI_VERSION_1_4;
}

/**
 * Called by JVM as library is being unloaded
 */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* jvm, void* reserved) {
	JNIEnv *env;
	if ((*jvm)->GetEnv(jvm, (void**)&env, JNI_VERSION_1_4))
		return;

	(*env)->DeleteGlobalRef(env, nativerenderer_class);
	(*env)->DeleteGlobalRef(env, function_class);
	(*env)->DeleteGlobalRef(env, complex_class);
	(*env)->DeleteGlobalRef(env, mapping_class);

	LOG_D("Unloaded library");
}

/**
 * Gets the renderer field of a NativeRenderer object
 */
static renderer_t* get_renderer(JNIEnv* env, jobject this) {
	return (renderer_t*)(intptr_t)((*env)->GetLongField(env, this, nativerenderer_renderer_fid));
}

/**
 * Sets the renderer field of a NativeRenderer object
 */
static void set_renderer(JNIEnv* env, jobject this, renderer_t* renderer) {
	(*env)->SetLongField(env, this, nativerenderer_renderer_fid, (jlong)(intptr_t)renderer);
}

/**
 * Initializes the renderer
 */
JNIEXPORT jboolean JNICALL Java_com_ijuru_refract_renderer_jni_NativeRenderer_allocate(JNIEnv* env, jobject this, jint width, jint height) {
	// Allocate renderer object
	renderer_t* renderer = (renderer_t*)malloc(sizeof (renderer_t));
	if (!renderer)
		return false;

	// Initialize it
	if (refract_renderer_init(renderer, width, height)) {
		// Store pointer on Java object
		set_renderer(env, this, renderer);

		LOG_D("Renderer #%d: allocated resources (%dx%d)", renderer->id, renderer->width, renderer->height);
		return true;
	}
	else {
		LOG_E("Renderer #%d: unable to allocate resources", renderer->id);
		return false;
	}
}

/**
 * Resizes the renderer
 */
JNIEXPORT jboolean JNICALL Java_com_ijuru_refract_renderer_jni_NativeRenderer_resize(JNIEnv* env, jobject this, jint width, jint height) {
	renderer_t* renderer = get_renderer(env, this);

	LOG_D("Renderer #%d: resizing (%dx%d) -> (%dx%d)", renderer->id, renderer->width, renderer->height, (int)width, (int)height);

	return refract_renderer_resize(renderer, (int)width, (int)height);
}

/**
 * Gets the width
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_jni_NativeRenderer_getWidth(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	return (jint)renderer->width;
}

/**
 * Gets the height
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_jni_NativeRenderer_getHeight(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	return (jint)renderer->height;
}

/**
 * Sets the palette
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_jni_NativeRenderer_setPalette(JNIEnv* env, jobject this, jobject palette, jint size, jfloat bias, jint set_color) {
	renderer_t* renderer = get_renderer(env, this);

	// Free existing palette
	refract_palette_free(&renderer->palette);

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
	int points = (*env)->GetArrayLength(env, *colors);

	refract_palette_init(&renderer->palette, size, RGB_TO_ABGR((int)set_color));
	refract_palette_gradient(&renderer->palette, (color_t*)colorvals, (float*)anchorvals, points, (float)bias);

	(*env)->ReleaseIntArrayElements(env, *colors, colorvals, 0);
	(*env)->ReleaseFloatArrayElements(env, *anchors, anchorvals, 0);

	LOG_D("Renderer #%d: updated palette", renderer->id);
}

/**
 * Iterates the renderer by the specified number of iterations
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_jni_NativeRenderer_iterate(JNIEnv* env, jobject this, jobject function, jobject offset, jdouble zoom, jint iters) {
	renderer_t* renderer = get_renderer(env, this);

	// Gather parameters from Java objects
	params_t params;
	params.func = (func_t)(*env)->CallIntMethod(env, function, function_ordinal_mid);
	params.offset.re = (float_t)((*env)->GetDoubleField(env, offset, complex_re_fid));
	params.offset.im = (float_t)((*env)->GetDoubleField(env, offset, complex_im_fid));
	params.zoom = (float_t)zoom;

	return (jint)refract_renderer_iterate(renderer, &params, (iterc_t)iters);
}

/**
 * Renders the iterations buffer to a pixel buffer
 */
JNIEXPORT jboolean JNICALL Java_com_ijuru_refract_renderer_jni_NativeRenderer_render(JNIEnv* env, jobject this, jobject bitmap, jobject mapping) {
	renderer_t* renderer = get_renderer(env, this);

	mapping_t pal_mapping = (mapping_t)(*env)->CallIntMethod(env, mapping, mapping_ordinal_mid);

	AndroidBitmapInfo info;
	AndroidBitmap_getInfo(env, bitmap, &info);

	color_t* pixels;
	int ret;

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void**)&pixels)) < 0) {
		LOG_E("AndroidBitmap_lockPixels() failed: error=%d", ret);
		return (jboolean)false;
	}

	bool result = refract_renderer_render(renderer, pixels, info.stride, pal_mapping);

	AndroidBitmap_unlockPixels(env, bitmap);
	return (jboolean)result;
}

/**
 * Frees the renderer for the given renderer
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_jni_NativeRenderer_free(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	refract_renderer_free(renderer);

	LOG_D("Renderer #%d: freed resources", renderer->id);

	// Free renderer object itself
	SAFE_FREE(renderer);

	set_renderer(env, this, NULL);
}
