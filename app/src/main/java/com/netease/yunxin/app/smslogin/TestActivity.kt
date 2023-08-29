/*
 * Copyright (c) 2022 NetEase, Inc. All rights reserved.
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */

package com.netease.yunxin.app.smslogin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.netease.yunxin.app.smslogin.databinding.ActivityTestLoginSampleBinding
import com.netease.yunxin.kit.smslogin.NESMSLoginSDK
import com.netease.yunxin.kit.smslogin.model.CustomUISupport
import com.netease.yunxin.kit.smslogin.model.HyperlinkConfig
import com.netease.yunxin.kit.smslogin.model.NEResponseInfo

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

    private val binding by lazy {
        ActivityTestLoginSampleBinding.inflate(layoutInflater)
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
                notify.invoke(GenerateTestToken.genTestToken(phone, appKey, appSecret))
            }
        binding.btLogin.setOnClickListener {
            NESMSLoginSDK.startLogin(this)
        }
    }
}
