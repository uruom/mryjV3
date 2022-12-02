// Copyright (c) 2019 PaddlePaddle Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include "Native.h"
#include "Pipeline.h"
#include <android/bitmap.h>

#ifdef __cplusplus
extern "C" {
#endif

/*
Native 实现 Java 与 C++ 端代码互传的桥梁功能，将 Java 数值转换为 c++ 数值，调用 c++ 端的完成人脸关键点检测功能 注意： Native 文件生成方法：
 cd app/src/java/com/baidu/paddle/lite/demo/face_keypoints_detection
 # 在当前目录会生成包含 Native 方法的头文件，用户可以将其内容拷贝至 `cpp/Native.cc` 中
 javac -classpath D:\dev\android-sdk\platforms\android-29\android.jar -encoding utf8 -h . Native.java
s
*/

/*
 * Class:     com_baidu_paddle_lite_demo_object_detection_Native
 * Method:    nativeInit
 * Signature:
 * (Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;II[F[FF)J
 */
JNIEXPORT jlong JNICALL
Java_com_baidu_paddle_lite_demo_object_1detection_Native_nativeInit(
        JNIEnv *env, jclass thiz, jstring jModelDir, jstring jLabelPath,
        jint cpuThreadNum, jstring jCPUPowerMode, jint inputWidth, jint inputHeight,
        jfloatArray jInputMean, jfloatArray jInputStd, jfloat scoreThreshold) {
    std::string modelDir = jstring_to_cpp_string(env, jModelDir);
    std::string labelPath = jstring_to_cpp_string(env, jLabelPath);
    std::string cpuPowerMode = jstring_to_cpp_string(env, jCPUPowerMode);
    std::vector<float> inputMean = jfloatarray_to_float_vector(env, jInputMean);
    std::vector<float> inputStd = jfloatarray_to_float_vector(env, jInputStd);
    return reinterpret_cast<jlong>(
            new Pipeline(modelDir, labelPath, cpuThreadNum, cpuPowerMode, inputWidth,
                         inputHeight, inputMean, inputStd, scoreThreshold));
}

/*
 * Class:     com_baidu_paddle_lite_demo_object_detection_Native
 * Method:    nativeRelease
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL
Java_com_baidu_paddle_lite_demo_object_1detection_Native_nativeRelease(
        JNIEnv *env, jclass thiz, jlong ctx) {
    if (ctx == 0) {
        return JNI_FALSE;
    }
    Pipeline *pipeline = reinterpret_cast<Pipeline *>(ctx);
    delete pipeline;
    return JNI_TRUE;
}

/*
 * Class:     com_baidu_paddle_lite_demo_object_detection_Native
 * Method:    nativeProcess
 * Signature: (JIIIILjava/lang/String;)Z
 */
JNIEXPORT jintArray JNICALL
Java_com_baidu_paddle_lite_demo_object_1detection_Native_nativeProcess(
        JNIEnv *env, jclass thiz, jlong ctx, jobject jARGB8888ImageBitmap,
        jstring jsavedImagePath) {
    int nullcarray[0];
    jintArray nullarray = cpp_array_to_jintarray(env,nullcarray,0);


    if (ctx == 0) {
        return nullarray;
    }

    // Convert the android bitmap(ARGB8888) to the OpenCV RGBA image. Actually,
    // the data layout of AGRB8888 is R, G, B, A, it's the same as CV RGBA image,
    // so it is unnecessary to do the conversion of color format, check
    // https://developer.android.com/reference/android/graphics/Bitmap.Config#ARGB_8888
    // to get the more details about Bitmap.Config.ARGB8888
    auto t = GetCurrentTime();
    void *bitmapPixels;
    AndroidBitmapInfo bitmapInfo;
    if (AndroidBitmap_getInfo(env, jARGB8888ImageBitmap, &bitmapInfo) < 0) {
        LOGE("Invoke AndroidBitmap_getInfo() failed!");
        return nullarray;
    }
    if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Only Bitmap.Config.ARGB8888 color format is supported!");
        return nullarray;
    }
    if (AndroidBitmap_lockPixels(env, jARGB8888ImageBitmap, &bitmapPixels) < 0) {
        LOGE("Invoke AndroidBitmap_lockPixels() failed!");
        return nullarray;
    }
    cv::Mat bmpImage(bitmapInfo.height, bitmapInfo.width, CV_8UC4, bitmapPixels);
    cv::Mat rgbaImage;
    bmpImage.copyTo(rgbaImage);
    if (AndroidBitmap_unlockPixels(env, jARGB8888ImageBitmap) < 0) {
        LOGE("Invoke AndroidBitmap_unlockPixels() failed!");
        return nullarray;
    }
    LOGD("Read from bitmap costs %f ms", GetElapsedTime(t));

    std::string savedImagePath = jstring_to_cpp_string(env, jsavedImagePath);
    Pipeline *pipeline = reinterpret_cast<Pipeline *>(ctx);
    std::vector<Object> modified = pipeline->Process(rgbaImage, savedImagePath);
    if (!modified.empty()) {
        // Convert the OpenCV RGBA image to the android bitmap(ARGB8888)
        if (rgbaImage.type() != CV_8UC4) {
            LOGE("Only CV_8UC4 color format is supported!");
            return nullarray;
        }
        t = GetCurrentTime();
        if (AndroidBitmap_lockPixels(env, jARGB8888ImageBitmap, &bitmapPixels) <
            0) {
            LOGE("Invoke AndroidBitmap_lockPixels() failed!");
            return nullarray;
        }
        cv::Mat bmpImage(bitmapInfo.height, bitmapInfo.width, CV_8UC4,
                         bitmapPixels);
        rgbaImage.copyTo(bmpImage);
        if (AndroidBitmap_unlockPixels(env, jARGB8888ImageBitmap) < 0) {
            LOGE("Invoke AndroidBitmap_unlockPixels() failed!");
            return nullarray;//JNI_FALSE;
        }
        LOGD("Write to bitmap costs %f ms", GetElapsedTime(t));
    }

    int *arr = new int[modified.size()*3];  //初始化数组大小  New
    for (int i = 0 ;i<modified.size();++i)
    {
        arr[3*i] = modified.at(i).class_id;
        arr[3*i+1] = modified.at(i).x;
        arr[3*i+2] = modified.at(i).y;
        printf("%d",arr[3*i]);
    }

    jintArray results = cpp_array_to_jintarray(env,arr,modified.size()*3);


    delete [] arr;                         // 释放
    return results;
}

#ifdef __cplusplus
}
#endif
