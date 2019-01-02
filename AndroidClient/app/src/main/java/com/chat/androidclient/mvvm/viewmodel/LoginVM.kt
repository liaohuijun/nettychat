package com.chat.androidclient.mvvm.viewmodel

import android.content.Intent
import com.blankj.utilcode.util.SPUtils
import com.chat.androidclient.event.LoginResponseEvent
import com.chat.androidclient.im.ChatIM
import com.chat.androidclient.mvvm.model.Constant
import com.chat.androidclient.mvvm.model.LoginRequest
import com.chat.androidclient.mvvm.procotol.response.LoginResponse
import com.chat.androidclient.mvvm.view.activity.LoginActivity
import com.chat.androidclient.mvvm.view.activity.MainActivity
import org.greenrobot.eventbus.Subscribe

/**
 * Created by 李培生 on 2018/12/21 10:34.
 */
class LoginVM(var view: LoginActivity) : BaseViewModel() {
   var name:String=""
   var pass:String=""
    init {
        name=SPUtils.getInstance().getString(Constant.LoginUserName)
        pass=SPUtils.getInstance().getString(Constant.LoginUserPass)
        view.setNameAndPassToView(name,pass)
    }
    fun login(name: String, pass: String) {
        ChatIM.instance.cmd(LoginRequest(name, pass))
        this.name=name
        this.pass=pass
    }
    
    /**
     * 登陆的结果
     */
    @Subscribe
    fun loginResponse(event: LoginResponseEvent) {
        if (event.msg.error) {
            view.showMsg("登陆失败${event.msg.errorInfo}")
        }
        else {
            val response = event.msg as LoginResponse
            view.showMsg("登陆成功")
            SPUtils.getInstance().put(Constant.LoginUserName,name)
            SPUtils.getInstance().put(Constant.LoginUserPass,pass)
            SPUtils.getInstance().put(Constant.id,response.userId!!)
            view.startActivity(Intent(view, MainActivity::class.java))
            view.finish()
        }
    }
    
}