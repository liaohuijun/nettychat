package com.chat.androidclient.mvvm.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.text.Editable
import android.text.TextWatcher
import com.blankj.utilcode.util.SPUtils
import com.chat.androidclient.R
import com.chat.androidclient.event.MessageEvent
import com.chat.androidclient.event.RefreshConversationEvent
import com.chat.androidclient.greendao.DaoMaster
import com.chat.androidclient.greendao.MessageResponseDao
import com.chat.androidclient.im.ChatIM
import com.chat.androidclient.mvvm.model.Constant
import com.chat.androidclient.mvvm.model.Conversation
import com.chat.androidclient.mvvm.procotol.request.SendMessageRequest
import com.chat.androidclient.mvvm.procotol.response.MessageResponse
import com.chat.androidclient.mvvm.view.activity.ChatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by lps on 2018/12/28 14:00.
 */
class ChatVM(var view: ChatActivity) : BaseViewModel() {
    var id: Long = view.intent.getLongExtra(ChatActivity.ID, -1)
    val devSession = DaoMaster.newDevSession(view, Constant.DBNAME)
    val msgDao = devSession.messageResponseDao
    val conversationDao = devSession.conversationDao
    private var builder: NotificationCompat.Builder? = null
    private var notification: NotificationManager? = null
    
    
    fun loadMessageFromDB() {
        val qb = msgDao.queryBuilder()
        val condition1 = qb.and(MessageResponseDao.Properties.FromUserId.eq(id), MessageResponseDao.Properties.ToUserId.eq(getMyId()))
        val condition2 = qb.and(MessageResponseDao.Properties.FromUserId.eq(getMyId()), MessageResponseDao.Properties.ToUserId.eq(id))
        qb.whereOr(condition1, condition2)
        
        val list = qb.list()
        view.addMessages(list)
    }
    
    fun sendMsg(msg: String) {
        ChatIM.instance.cmd(SendMessageRequest(id, msg))
//清空输入框
        view.clearInput()
//        todo insert db
        val message = MessageResponse()
        message.fromUserId = getMyId()
        message.message = msg
        message.toUserId = id
        message.time = System.currentTimeMillis()
        msgDao.insertOrReplace(message)
//         refresh list
        view.addMessage(message)
        // 最近会话列表DB 刷新这个好友
        val conversation = Conversation()
        conversation.fromId = id
        conversation.lastcontent = msg
        conversation.time = System.currentTimeMillis()
        conversationDao.insertOrReplace(conversation)
        //通知最近会话列表更新
        EventBus.getDefault().post(RefreshConversationEvent())
    }
    
    //收到后台推送过来的消息
    @Subscribe
    fun ReciveMessage(event: MessageEvent) {
        val response = event.msg as MessageResponse
        
        if (response.fromUserId == SPUtils.getInstance().getLong(Constant.UserId)) {
        
        }
        else {
            //写入聊天消息的db
            response.toUserId = SPUtils.getInstance().getLong(Constant.id)
            response.time = System.currentTimeMillis()
            msgDao.insert(response)
            //如果是当前聊天对象发给我的
            if (response.fromUserId == id) {
                //更新RecyclerView
                view.addMessage(response)
            }
            else {
                //发送通知
                notification(response)
            }
            
        }
    }
    
    fun notification(response: MessageResponse) {
        if (builder == null)
            builder = NotificationCompat.Builder(view, "recivemessage")
        builder!!.setContentTitle("收到新的消息")
                .setContentText(response.message)
                .setLargeIcon(BitmapFactory.decodeResource(view.resources, R.drawable.otherhead))
                .setSmallIcon(R.drawable.otherhead)
                .setWhen(System.currentTimeMillis())
        if (notification == null)
            notification = view.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChanel()
        }
        notification!!.notify(0, builder!!.build())
    }
    
    var channel: NotificationChannel? = null
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChanel() {
        if (channel == null) {
            channel = NotificationChannel("recivemessage", "process", NotificationManager.IMPORTANCE_LOW)
            builder!!.setOnlyAlertOnce(true)
            notification!!.createNotificationChannel(channel)
        }
    }
    
    private fun getMyId() = SPUtils.getInstance().getLong(Constant.id)
    fun getInputWatcher(): TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (s.length > 0) {
                view.canClickSendBtn(true)
            }
            else {
                view.canClickSendBtn(false)
                
            }
        }
        
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}