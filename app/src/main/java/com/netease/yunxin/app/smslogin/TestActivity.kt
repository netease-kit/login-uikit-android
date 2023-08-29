/*
 * Copyright (c) 2022 NetEase, Inc. All rights reserved.
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */

package com.netease.yunxin.app.smslogin

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.netease.yunxin.app.smslogin.databinding.ActivityTestLoginSampleBinding
import com.netease.yunxin.kit.smslogin.NESMSLoginSDK
import com.netease.yunxin.kit.smslogin.model.CustomUISupport
import com.netease.yunxin.kit.smslogin.model.HyperlinkConfig
import com.netease.yunxin.kit.smslogin.model.NEResponseInfo
import kotlin.properties.Delegates
import org.json.JSONObject
import java.security.MessageDigest

/*
 * Copyright (c) 2022 NetEase, Inc. All rights reserved.
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */

class TestActivity : AppCompatActivity() {
    /**
     * 用户自己的appKey，从控制台获得
     */
    private val appKey = ""

    /**
     * 短信模版id，从控制台获得
     */
    private val templateId = 0

    /**
     * 用户自己的appSecret，从控制台获得
     */
    private val appSecret = ""

    /**
     * 用户自己的token过期时间，单位为秒
     */
    private val ttl = 6000L

    private val binding by lazy {
        ActivityTestLoginSampleBinding.inflate(layoutInflater)
    }

    private var infoSource: String by Delegates.observable("") { _, _, new ->
        binding.tvConsole.text = new
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        NESMSLoginSDK
            .configTemplateId(templateId)
            .configForgetPasswordListener { info, finishAction ->
                finishAction.invoke(NEResponseInfo(true, "重置成功"))
            }
            .configRegisterUserListener { info, finishAction ->
                finishAction.invoke(NEResponseInfo(true, "注册成功"))
            }
            .configPasswordLoginListener { info, finishAction ->
                finishAction.invoke(NEResponseInfo(true, "登录成功"))
            }
            .configSMSCodeLoginListener { info, finishAction ->
                finishAction.invoke(NEResponseInfo(true, "登录成功"))
            }
            .configCustomUI(
                mapOf(
                    CustomUISupport.KEY_PROTOCOL_LIST to listOf(
                        HyperlinkConfig(
                            "用户隐私",
                            "https://xxx/privacy"
                        ),
                        HyperlinkConfig(
                            "用户服务协议",
                            "https://xxx/user"
                        )
                    )
                )
            )
            .setup(this, appKey) { phone, notify ->
                if (phone == null) {
                    notify.invoke(null)
                    return@setup
                }
                notify.invoke(getToken(phone))
            }
        binding.btLogin.setOnClickListener {
            NESMSLoginSDK.startLogin(this)
        }
        binding.btClear.setOnClickListener {
            infoSource = ""
        }
    }

    /**
     * 实际 token 获取，需要用户通过自己的服务端获取，appSecret 不能暴露在客户端，此处仅做为示例演示
     */
    private fun getToken(phone: String): String {
        val currentTime = System.currentTimeMillis()
        // 计算 appSecret + nonce + currentTime 的 sha1 的值
        val signature = sha1("$appKey$phone$currentTime$ttl$appSecret")
        val json = JSONObject().apply {
            put("appKey", appKey)
            put("currentTime", currentTime)
            put("ttl", ttl)
            put("signature", signature)
        }
        return Base64.encodeToString(json.toString().toByteArray(), Base64.NO_WRAP)
    }

    private fun sha1(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-1")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
