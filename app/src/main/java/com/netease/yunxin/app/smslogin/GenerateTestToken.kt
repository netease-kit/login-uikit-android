/*
 * Copyright (c) 2022 NetEase, Inc. All rights reserved.
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */

package com.netease.yunxin.app.smslogin

import android.util.Base64
import com.google.gson.Gson
import java.security.MessageDigest

object GenerateTestToken {
    /**
     * token过期时间，建议不要设置的太长
     *
     *
     * 时间单位：秒 过期时间：10 * 60 = 600 = 10 分钟
     */
    private const val EXPIRE_TIME = 600L

    /**
     * 获取用户token
     */
    fun genTestToken(phone: String, appKey: String, appSecret: String): String {
        val curTime = System.currentTimeMillis()
        val params: MutableMap<String, Any?> = HashMap()
        params["signature"] = getSignature(
            appKey, phone, curTime.toString(), EXPIRE_TIME.toString(), appSecret
        )
        params["curTime"] = curTime
        params["ttl"] = EXPIRE_TIME
        val result = Gson().toJson(params)
        return Base64.encodeToString(result.toByteArray(), Base64.NO_WRAP)
    }

    private val HEX_DIGITS = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    )

    /**
     * 生成signature，将App Key、App Secret、phone、curTime、ttl五个字段拼成一个字符串，进行sha1编码
     *
     * @param appKey 应用 App Key
     * @param phone 用户手机
     * @param curTime 当前时间
     * @param ttl 有效期，单位是秒
     * @param appSecret 应用 AppSecret
     * @return
     */
    private fun getSignature(
        appKey: String, phone: String, curTime: String, ttl: String, appSecret: String
    ): String? {
        return encode("sha1", appKey + phone + curTime + ttl + appSecret)
    }

    private fun encode(algorithm: String, value: String?): String? {
        return if (value == null) {
            null
        } else try {
            val messageDigest = MessageDigest.getInstance(algorithm)
            messageDigest.update(value.toByteArray(charset("UTF-8")))
            getFormattedText(messageDigest.digest())
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getFormattedText(bytes: ByteArray): String {
        val len = bytes.size
        val buf = StringBuilder(len * 2)
        for (j in 0 until len) {
            buf.append(HEX_DIGITS[bytes[j].toInt() shr 4 and 0x0f])
            buf.append(HEX_DIGITS[bytes[j].toInt() and 0x0f])
        }
        return buf.toString()
    }
}