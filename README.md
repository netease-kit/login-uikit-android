
# 概述


网易云信 LoginKit 登录组件封装了云信短信能力，提供发送短信、验证短信验证码以及验证结果抄送功能。通过该组件您可以将短信验证能力快速集成至自身应用中，实现短信验证登录的场景。

**Demo 效果图：**
<img src="https://yx-web-nosdn.netease.im/common/c42aafff418acc8bc2149547515dbfa1/登录.png" width=900 />

## 技术支持

网易云信提供多种服务，包括客服、技术支持、热线服务、全流程数据监控等，建议扫码添加我们的技术支持，协助接入、测试以及定制需求。
| 微信咨询 | 在线咨询 | 电话咨询 |
| :--: | :--: | :--: |
| <img src="https://yx-web-nosdn.netease.im/common/bbe362067ecfb9c0aafa41b3c2a9e90a/扫码咨询.png" width=100 /> | [点击在线咨询](https://qiyukf.com/client?k=10f5d3378752b7b73aa90daf2bd4fc8a&u=&d=96oshbhiofhds8zt8ej3&uuid=emoobdra0otce0bnxjfz&gid=0&sid=0&qtype=0&welcomeTemplateId=0&dvctimer=0&robotShuntSwitch=0&hc=0&robotId=0&pageId=1691392062019BDH8BhIoTI&shuntId=0&ctm=LS0xNjkxMzkyMTI1NDg4&wxwId=&language=&isShowBack=0&shortcutTemplateId=&t=%25E7%25BD%2591%25E6%2598%2593%25E4%25BA%2591%25E4%25BF%25A1%2520-%2520IM%25E5%258D%25B3%25E6%2597%25B6%25E9%2580%259A%25E8%25AE%25AF%25E4%25BA%2591%2520-%25E9%259F%25B3%25E8%25A7%2586%25E9%25A2%2591%25E9%2580%259A%25E8%25AF%259D) | 4009-000-123 |


## 更新日志

【V1.0.0】

- 支持发送短信验证码
- 支持验证短信验证码
- 完成登录组件 UI 能力
- 实现主题颜色自定义
- 实现内部主要 UI 通过父view查询方式获取子view进行深度 UI 自定义

## 准备工作

提前获取应用 AppKey 和短信模版 ID。

1. 创建云信账号。

    如果您还没有网易云信账号，请访问[注册](https://app.netease.im/regist)。如果您已经有网易云信账号，请直接[登录](https://app.netease.im/login)。具体请参考云信控制台的[注册与登录文档](https://doc.yunxin.163.com/console/docs/DU0ODE4NTA?platform=console)。


2. 创建应用。

    创建应用是体验或使用网易云信各款产品和服务的首要前提，您可以参考[创建应用文档](https://doc.yunxin.163.com/console/docs/TIzMDE4NTA?platform=console)在网易云信控制台创建一个应用，并查看该应用的 App Key。



3. 开通短信并购买资源包。

    由于短信为资源型产品，开通功能后还需要购买相应的资源包短信条数才可正常使用，否则短信将会由于无资源包而无法正常使用，具体请参考[开通短信并购买资源包](https://doc.yunxin.163.com/sms/docs/TE1ODQ0NDY?platform=server#3-开通短信并购买资源包)。

4. 申请短信签名并创建短信模板。

    申请和创建后等待云信审核，审核通过后获取短信模版 ID，具体请参考[申请签名](https://doc.yunxin.163.com/sms/docs/TE1ODQ0NDY?platform=server#4-申请短信签名)和[创建模板](https://doc.yunxin.163.com/sms/docs/TE1ODQ0NDY?platform=server#5-创建短信正文模板)。

## 组件引入

在您的项目的 build.gradle 中，以添加依赖的形式添加登录组件。

```
dependencies { 
    implementation("com.netease.yunxin.kit.smslogin:smsloginkit:1.0.0")
}
```

## 组件初始化

接口：

```kotlin
// 配置短信模版 id 并初始化
NESMSLoginSDK.configTemplateId(templateId).setup(this, appKey) { 
   phone, notify ->
   // phone 为空通知组件获取失败，返回 null
   if (phone == null) {
     notify.invoke(null)
     return@setup
   }
   // 网络请求获取对应的token信息
   CoroutineScope(Dispatchers.IO).launch(errorCatcher) {
     // 获取短信发送和验证token示例，实际需要用户自己实现，
     val result = NetWorkRequester.getToken(mapOf("mobile" to phone))
     notify.invoke(result?.data)
   }
 }
```

参数说明：
```kotlin
// templateId 短信模版 id
fun configTemplateId(templateId: Int)
// context Android 上下文
// appKey 应用 appKey
// tokenRequester 应用通过组件给出的 phone 内容自己在服务端获取对应的安全 token 信息并返回给组件
fun setup(context: Context, appKey: String, tokenRequester: (String?, (String?) -> Unit) -> Unit)
```

## 配置回调

接口：

```kotlin
// 配置短信验证码登录回调
fun configSMSCodeLoginListener(listener: (NESMSCodeLoginInfo, (NEResponseInfo) -> Unit) -> Unit)
// 配置账号密码登录回调
fun configPasswordLoginListener(listener: (NEPasswordLoginInfo, (NEResponseInfo) -> Unit) -> Unit)
// 配置用户注册回调
fun configRegisterUserListener(listener: (NERegisterUserInfo, (NEResponseInfo) -> Unit) -> Unit)
// 配置忘记密码重置回调
fun configForgetPasswordListener(listener: (NEForgetPasswordInfo, (NEResponseInfo) -> Unit) -> Unit)
```

参数说明：

```kotlin
// 短信验证码登录回调信息
class NESMSCodeLoginInfo(
  // 手机号
  val phone: String?, 
  // 验证码
  val code: String?, 
  // 验证码验证成功后的 token
  val verifyCodeToken: String?)

// 账号密码登录回调信息
class NEPasswordLoginInfo(
  // 手机号
  val phone: String?, 
  // 密码
  val password: String?)

// 用户注册回调信息
class NERegisterUserInfo(
  // 手机号
  val phone: String?, 
  // 验证码
  val code: String?, 
  // 密码
  val password: String?, 
  // 验证码验证成功后的 token
  val verifyCodeToken: String?)

// 忘记密码重置回调信息
class NEForgetPasswordInfo(
  // 手机号
  val phone: String?, 
  // 验证码
  val code: String?, 
  // 密码
  val password: String?,
  // 验证码验证成功后的 token
  val verifyCodeToken: String?)

// 用户登录/注册/重置密码后通知组件的信息
class NEResponseInfo(
  // 操作是否成功
  val success: Boolean,
  // 错误提示信息
  val errorMsg: String? = null,
  // 是否关闭当前页面（登录/注册/重置密码）
  val canFinishPage: Boolean = true,
  // 是否需要展示未注册提示弹窗
  val needToRegister: Boolean = false,
  // 是否需要展示重复注册提示弹窗
  val isRepeatedRegister: Boolean = false
)
```

示例代码：

>以下示例仅提供逻辑参考，具体请根据实际情况进行修改。

- 密码登录回调示例

    ```kotlin
    NESMSLoginSDK.configPasswordLoginListener { 
    info, finishAction ->
    CoroutineScope(Dispatchers.IO).launch(errorCatcher) {
        val result = NetWorkRequester.loginByPassword(info)
        if (result == null || result.code != SMS_LOGIN_ERROR_CODE_SUCCESS) {
        when (result?.code) {
            errorCodeUserNotExist -> {
            finishAction.invoke(NEResponseInfo(false, "", canFinishPage = false, needToRegister = true))
            }
            errorCodeUserExist -> {
            finishAction.invoke(NEResponseInfo( false, "", canFinishPage = false, isRepeatedRegister = true))
            }
            else -> {
            finishAction.invoke(NEResponseInfo(false, "错误提示信息", canFinishPage = false ))
            }
        }
        } else {
        finishAction.invoke(NEResponseInfo(true, "登录成功"))
        }  
    }
    }
    ```

- 短信登录回调示例

    ```kotlin
    NESMSLoginSDK.configSMSCodeLoginListener { 
    info, finishAction ->
    CoroutineScope(Dispatchers.IO).launch(errorCatcher) {
        val result = NetWorkRequester.loginBySMSCode(info)
        if (result == null || result.code != SMS_LOGIN_ERROR_CODE_SUCCESS) {
        when (result?.code) {
            errorCodeUserNotExist -> {
            finishAction.invoke(NEResponseInfo(false, "", canFinishPage = false, needToRegister = true))
            }
            errorCodeUserExist -> {
            finishAction.invoke(NEResponseInfo( false, "", canFinishPage = false, isRepeatedRegister = true))
            }
            else -> {
            finishAction.invoke(NEResponseInfo(false, "错误提示信息", canFinishPage = false ))
            }
        }
        } else {
        finishAction.invoke(NEResponseInfo(true, "登录成功"))
        }  
    }
    }
    ```

- 重置密码回调示例

    ```kotlin
    NESMSLoginSDK.configForgetPasswordListener { 
    info, finishAction ->
    CoroutineScope(Dispatchers.IO).launch(errorCatcher) {
        val result = NetWorkRequester.resetPassword(info)
        if (result == null || result.code != SMS_LOGIN_ERROR_CODE_SUCCESS) {
        when (result?.code) {
            errorCodeUserNotExist -> {
            finishAction.invoke(NEResponseInfo(false, "", canFinishPage = false, needToRegister = true))
            }
            errorCodeUserExist -> {
            finishAction.invoke(NEResponseInfo( false, "", canFinishPage = false, isRepeatedRegister = true))
            }
            else -> {
            finishAction.invoke(NEResponseInfo(false, "错误提示信息", canFinishPage = false ))
            }
        }
        } else {
        finishAction.invoke(NEResponseInfo(true, "重置成功"))
        }  
    }
    }
    ```

- 注册回调示例

    ```kotlin
    NESMSLoginSDK.configRegisterUserListener { 
    info, finishAction ->
    CoroutineScope(Dispatchers.IO).launch(errorCatcher) {
        val result = NetWorkRequester.register(info)
        if (result == null || result.code != SMS_LOGIN_ERROR_CODE_SUCCESS) {
        when (result?.code) {
            errorCodeUserNotExist -> {
            finishAction.invoke(NEResponseInfo(false, "", canFinishPage = false, needToRegister = true))
            }
            errorCodeUserExist -> {
            finishAction.invoke(NEResponseInfo( false, "", canFinishPage = false, isRepeatedRegister = true))
            }
            else -> {
            finishAction.invoke(NEResponseInfo(false, "错误提示信息", canFinishPage = false ))
            }
        }
        } else {
        finishAction.invoke(NEResponseInfo(true, "注册成功"))
        }  
    }
    }
    ```

## 自定义配置

接口：

```kotlin
object CustomUISupport {
  // 配置登录/注册页面底部隐私协议内容列表
  const val KEY_PROTOCOL_LIST = "protocol_list"
  // 配置非点击内容
  const val KEY_PROTOCOL_COMMON = "protocol_common"
  // 配置多个协议时中间的分隔字符
  const val KEY_PROTOCOL_MID = "protocol_mid"
  // 是否展示底部协议
  const val KEY_SHOW_PROTOCOL = "show_protocol"
  
  // 配置短信验证码登录标题
  const val KEY_PAGE_TITLE_LOGIN_CODE = "page_title_login_code"
  // 配置账号密码登录标题
  const val KEY_PAGE_TITLE_LOGIN_PASSWORD = "page_title_login_password"
  // 配置忘记密码重置页面标题
  const val KEY_PAGE_TITLE_FORGET_PASSWORD = "page_title_forget_password"
  // 配置注册页面标题
  const val KEY_PAGE_TITLE_REGISTER = "page_title_register"
  
  // 配置协议同意确认提示弹窗标题
  const val KEY_DIALOG_TITLE_PROTOCOL = "title_protocol"
  // 配置协议同意确认提示弹窗内容
  const val KEY_DIALOG_CONTENT_PROTOCOL = "content_protocol"
  // 配置协议同意确认提示弹窗左侧按钮内容
  const val KEY_DIALOG_BUTTON_LEFT_PROTOCOL = "button_left_protocol"
  // 配置协议同意确认提示弹窗右侧按钮内容
  const val KEY_DIALOG_BUTTON_RIGHT_PROTOCOL = "button_right_protocol"
  
  // 配置需要注册提示弹窗标题
  const val KEY_DIALOG_TITLE_REGISTER = "title_register"
  // 配置需要注册提示弹窗内容
  const val KEY_DIALOG_CONTENT_REGISTER = "content_register"
  // 配置需要注册提示弹窗左侧按钮内容
  const val KEY_DIALOG_BUTTON_LEFT_REGISTER = "button_left_register"
  // 配置需要注册提示弹窗右侧按钮内容
  const val KEY_DIALOG_BUTTON_RIGHT_REGISTER = "button_right_register"
  
	// 配置登录提示弹窗标题
  const val KEY_DIALOG_TITLE_LOGIN = "title_login"
  // 配置登录提示弹窗内容
  const val KEY_DIALOG_CONTENT_LOGIN = "content_login"
  // 配置登录提示弹窗左侧按钮内容
  const val KEY_DIALOG_BUTTON_LEFT_LOGIN = "button_left_login"
  // 配置登录提示弹窗右侧按钮内容
  const val KEY_DIALOG_BUTTON_RIGHT_LOGIN = "button_right_login"
}
```

示例：
```kotlin
NESMSLoginSDK.configCustomUI(
  mapOf(
    CustomUISupport.KEY_PROTOCOL_LIST to listOf(
      HyperlinkConfig("隐私","https://privacy"),
      HyperlinkConfig("用户协议", "https://user")
    ),
    CustomUISupport.KEY_SHOW_PROTOCOL to true,
    CustomUISupport.KEY_DIALOG_TITLE_REGISTER to "用户注册"
  )
)
```

## 自定义颜色

组件内颜色固定如下，若需要修改，则声明相同的资源 id 进行覆盖。

```xml
<resources>
    <color name="sms_login_color_transparent">#00000000</color>
    <color name="sms_login_color_999999">#FF999999</color>
    <color name="sms_login_color_222222">#FF222222</color>
    <color name="sms_login_color_2155ee">#FF2155EE</color>
    <color name="sms_login_color_dcdfe5">#FFDCDFE5</color>
    <color name="sms_login_color_e74646">#FFE74646</color>
    <color name="sms_login_color_f1f1f1">#FFF1F1F1</color>
    <color name="sms_login_color_cccccc">#FFCCCCCC</color>
    <color name="sms_login_color_white">#FFFFFFFF</color>
    <color name="sms_login_color_black">#FF000000</color>
</resources>

```

## 自定义其他内容

参数说明：

```kotlin
// 页面名称和页面根部父布局
fun configLogoHelper(helper: (String, ViewGroup) -> Unit)
```

示例：

```kotlin
NESMSLoginSDK.configLogoHelper { 
  pageName, viewGroup ->
  when (pageName) {
    // 登录页面 activity
    Constants.PAGE_NAME_ACTIVITY_LOGIN -> {
    }
    // 注册页面 activity
    Constants.PAGE_NAME_ACTIVITY_REGISTER -> {
    }
    // 忘记密码页面 activity
    Constants.PAGE_NAME_ACTIVITY_FORGET_PASSWORD -> {
    }
    // 密码登录页面 fragment
    Constants.PAGE_NAME_LOGIN_FRAGMENT_PASSWORD -> {
    }
    // 验证码登录页面 fragment
    Constants.PAGE_NAME_LOGIN_FRAGMENT_SMS_CODE -> {
    }
  }
}
```



