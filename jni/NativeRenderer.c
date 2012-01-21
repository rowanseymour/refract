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

#define NATIVERENDERER_CLASS	"com/ijuru/refract/renderer/NativeRenderer"
#define FUNCTION_CLASS			"com/ijuru/refract/Function"
#define COMPLEX_CLASS			"com/ijuru/refract/Complex"

// Cached Java entities
jclass nativerenderer_class, complex_class, function_class;
jfieldID nativerenderer_renderer_fid, complex_re_fid, complex_im_fid;
jmethodID function_ordinal_mid, function_values_mid, complex_cid;

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
	function_values_mid = (*env)->GetStaticMethodID(env, function_class, "values", "()[L" FUNCTION_CLASS ";");

	cls = (*env)->FindClass(env, COMPLEX_CLASS);
	complex_class = (*env)->NewGlobalRef(env, cls);
	complex_cid = (*env)->GetMethodID(env, complex_class, "<init>", "(DD)V");
	complex_re_fid = (*env)->GetFieldID(env, complex_class, "re", "D");
	complex_im_fid = (*env)->GetFieldID(env, complex_class, "im", "D");

	LOG_D("Loaded library");

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
JNIEXPORT jboolean JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_allocate(JNIEnv* env, jobject this, jint width, jint height) {
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
JNIEXPORT jboolean JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_resize(JNIEnv* env, jobject this, jint width, jint height) {
	renderer_t* renderer = get_renderer(env, this);

	LOG_D("Renderer #%d: resizing (%dx%d) -> (%dx%d)", renderer->id, renderer->width, renderer->height, (int)width, (int)height);

	return refract_renderer_resize(renderer, (int)width, (int)height);
}

/**
 * Gets the width
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_getWidth(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	return (jint)renderer->width;
}

/**
 * Gets the height
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_getHeight(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	return (jint)renderer->height;
}

/**
 * Gets the iteration function
 */
JNIEXPORT jobject JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_getFunction(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	jobject values_obj = (*env)->CallStaticObjectMethod(env, function_class, function_values_mid);
	return (*env)->GetObjectArrayElement(env, values_obj, (int)renderer->params.func);
}

/**
 * Sets the iteration function
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setFunction(JNIEnv* env, jobject this, jobject function) {
	renderer_t* renderer = get_renderer(env, this);

	func_t func = (func_t)(*env)->CallIntMethod(env, function, function_ordinal_mid);

	refract_renderer_setfunction(renderer, func);
}

/**
 * Gets the offset in complex space
 */
JNIEXPORT jobject JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_getOffset(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	return (*env)->NewObject(env, complex_class, complex_cid, (jdouble)renderer->params.offset.re, (jdouble)renderer->params.offset.im);
}

/**
 * Sets the offset in complex space
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setOffset(JNIEnv* env, jobject this, jobject offset) {
	renderer_t* renderer = get_renderer(env, this);

	float_t re = (float_t)((*env)->GetDoubleField(env, offset, complex_re_fid));
	float_t im = (float_t)((*env)->GetDoubleField(env, offset, complex_im_fid));
	complex_t o = { re, im };

	refract_renderer_setoffset(renderer, o);
}

/**
 * Sets the iteration function
 */
JNIEXPORT jdouble JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_getZoom(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	return (jdouble)renderer->params.zoom;
}

/**
 * Sets the iteration function
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setZoom(JNIEnv* env, jobject this, jdouble zoom) {
	renderer_t* renderer = get_renderer(env, this);

	refract_renderer_setzoom(renderer, (float_t)zoom);
}

/**
 * Sets the palette
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setPalette(JNIEnv* env, jobject this, jobject palette, jint size) {
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

	refract_palette_init(&renderer->palette, (color_t*)colorvals, (float*)anchorvals, points, size);

	(*env)->ReleaseIntArrayElements(env, *colors, colorvals, 0);
	(*env)->ReleaseFloatArrayElements(env, *anchors, anchorvals, 0);

	LOG_D("Renderer #%d: updated palette", renderer->id);
}

/**
 * Gets the palette offset
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_getPaletteOffset(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);
	return (jint)renderer->palette.offset;
}

/**
 * Sets the palette offset
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_setPaletteOffset(JNIEnv* env, jobject this, jint offset) {
	renderer_t* renderer = get_renderer(env, this);
	renderer->palette.offset = (int)offset;
}

/**
 * Iterates the renderer by the specified number of iterations
 */
JNIEXPORT jint JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_iterate(JNIEnv* env, jobject this, jint iters) {
	renderer_t* renderer = get_renderer(env, this);

	return (jint)refract_renderer_iterate(renderer, (iterc_t)iters);
}

/**
 * Renders the iterations buffer to a pixel buffer
 */
JNIEXPORT jboolean JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_render(JNIEnv* env, jobject this, jobject bitmap) {
	renderer_t* renderer = get_renderer(env, this);

	AndroidBitmapInfo info;
	AndroidBitmap_getInfo(env, bitmap, &info);

	color_t* pixels;
	int ret;

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void**)&pixels)) < 0) {
		LOG_E("AndroidBitmap_lockPixels() failed: error=%d", ret);
		return (jboolean)false;
	}

	refract_renderer_render(renderer, pixels, info.stride);

	AndroidBitmap_unlockPixels(env, bitmap);
	return (jboolean)true;
}

/**
 * Frees the renderer for the given renderer
 */
JNIEXPORT void JNICALL Java_com_ijuru_refract_renderer_NativeRenderer_free(JNIEnv* env, jobject this) {
	renderer_t* renderer = get_renderer(env, this);

	refract_renderer_free(renderer);

	LOG_D("Renderer #%d: freed resources", renderer->id);

	// Free renderer object itself
	free(renderer);

	set_renderer(env, this, NULL);
}
