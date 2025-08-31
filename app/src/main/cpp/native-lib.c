
#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include "aes.h"

#define LOG_TAG "NDK-AuthKey"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Hardcoded 256-bit key + 128-bit IV for demo; in real apps, use a keystore.
static const uint8_t SECRET_KEY[32] = {
    0x60,0x3d,0xeb,0x10,0x15,0xca,0x71,0xbe,
    0x2b,0x73,0xae,0xf0,0x85,0x7d,0x77,0x81,
    0x1f,0x35,0x2c,0x07,0x3b,0x61,0x08,0xd7,
    0x2d,0x98,0x10,0xa3,0x09,0x14,0xdf,0xf4
};
static const uint8_t IV[16] = {
    0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,
    0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f
};

static void to_hex(const uint8_t* in, size_t len, char* out) {
    static const char* hex = "0123456789abcdef";
    for (size_t i = 0; i < len; ++i) {
        out[2*i]   = hex[(in[i] >> 4) & 0xF];
        out[2*i+1] = hex[in[i] & 0xF];
    }
    out[2*len] = '\0';
}

// PKCS7 padding
static uint8_t* pkcs7_pad(const uint8_t* data, size_t len, size_t block, size_t* outLen) {
    size_t pad = block - (len % block);
    *outLen = len + pad;
    uint8_t* out = (uint8_t*)malloc(*outLen);
    memcpy(out, data, len);
    for (size_t i = 0; i < pad; ++i) out[len + i] = (uint8_t)pad;
    return out;
}

JNIEXPORT jstring JNICALL
Java_com_technonext_ndkauth_NativeCrypto_generateAuthKey(JNIEnv* env, jobject thiz, jstring payload) {
    (void)thiz;
    const char* in = (*env)->GetStringUTFChars(env, payload, 0);
    size_t in_len = strlen(in);

    size_t padded_len = 0;
    uint8_t* padded = pkcs7_pad((const uint8_t*)in, in_len, 16, &padded_len);

    struct AES_ctx ctx;
    AES_init_ctx_iv(&ctx, SECRET_KEY, IV);

    // CBC encrypt in-place
    AES_CBC_encrypt_buffer(&ctx, padded, (uint32_t)padded_len);

    // Hex encode ciphertext
    size_t hex_len = padded_len * 2;
    char* hex = (char*)malloc(hex_len + 1);
    to_hex(padded, padded_len, hex);

    // Clean up
    free(padded);
    (*env)->ReleaseStringUTFChars(env, payload, in);

    jstring out = (*env)->NewStringUTF(env, hex);
    free(hex);
    return out;
}
