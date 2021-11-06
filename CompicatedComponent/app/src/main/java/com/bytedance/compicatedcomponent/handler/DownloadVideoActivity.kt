package com.bytedance.compicatedcomponent.handler

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.TextView
import com.bytedance.compicatedcomponent.R

class DownloadVideoActivity : Activity() {

    companion object {

        const val STATUS_START_DOWNLOAD = 0
        const val STATUS_DOWNLOADING = 1
        const val STATUS_FINISH_DOWNLOAD = 2
        const val KEY_PROGRESS = "progress"
    }

    var downloadTextView: TextView? = null

    //Handler本质上是一个接口，里面传的参数就是一个callback到handleMessage函数上（callback 是一种特殊的函数,这个函数被作为参数传给另一个函数去调用）
    val handler: Handler = Handler(Looper.getMainLooper()) { msg ->//重写了handler的handlemessage的操作，是消息消费者
        when (msg.what) {
            STATUS_DOWNLOADING -> {
                downloadTextView?.text = "${msg.data[KEY_PROGRESS]}%"
            }
            STATUS_START_DOWNLOAD -> {
                downloadTextView?.text = "开始下载..."
            }
            STATUS_FINISH_DOWNLOAD -> {
                downloadTextView?.text = "下载完成!!!"
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_video)
        findViewById<Button>(R.id.bt_download).setOnClickListener {
            val downloadThread = DownloadVideoThread()//点击下载按钮，启动下载线程
            downloadThread.start()
        }
        downloadTextView = findViewById(R.id.tv_progress)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
//下载线程
    inner class DownloadVideoThread : Thread() {
//主要就是复写run方法
        override fun run() {
            super.run()
            startDownload()
            downloadVideo()
            finishDownload()
        }

        private fun downloadVideo() {
            var count = 10
            while (count > 0) {
                sleep(1000)
                count --
                updateDownloadProgress(100 * (10 - count) / 10)
            }
        }

        private fun startDownload() {
            val msg = Message.obtain()//通过handler去发送一个消息，这里获取一个message实例
            msg.what = STATUS_START_DOWNLOAD//消息类型，int类型表示
            handler.sendMessage(msg)//传到消息队列中，消息队列是不会暴露给开发者的
        }

        private fun finishDownload() {
            val msg = Message.obtain()
            msg.what = STATUS_FINISH_DOWNLOAD
            handler.sendMessage(msg)
        }

        private fun updateDownloadProgress(progress: Int) {//体现了消息消费者模型，一个消息被消费者收到后会执行是什么操作
            val msg = Message.obtain()
            msg.what = STATUS_DOWNLOADING
            msg.data = Bundle().apply {//消息另外携带了一个数据data，通过bundle把数据组织在一起
                putInt(KEY_PROGRESS, progress)
            }
            handler.sendMessage(msg)
        }
    }
}