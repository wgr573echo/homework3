package com.bytedance.compicatedcomponent.handler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.bytedance.compicatedcomponent.R

class HandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_handler_activity)//开屏广告界面模拟

//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            jump2MainPage()
//        }, 3000)



//        val skipButton = findViewById<View>(R.id.tv_skip)
//        skipButton.setOnClickListener {
//            handler.removeCallbacksAndMessages(null)//在点击跳过直接跳转至主界面之前需要把handler里面的消息清空，
//            不然写好的3s取消息机制会重复进行一次主界面跳转
//            jump2MainPage()
//        }

        val handler = Handler(Looper.getMainLooper()) { msg ->
            if (msg.what == 1) {
                jump2MainPage()
            }
            true//？？？
        }
        val msg = Message.obtain(handler, 1)
        handler.sendMessageDelayed(msg, 3000)

    }

    private fun jump2MainPage() {
        startActivity(Intent(this, HomepageActivity::class.java))
    }
}