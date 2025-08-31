package com.technonext.ndkauth

object NativeCrypto {
    init {
        System.loadLibrary("crypto-lib")
    }
    external fun generateAuthKey(payload: String): String
}
