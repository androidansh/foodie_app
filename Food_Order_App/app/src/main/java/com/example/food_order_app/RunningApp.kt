package com.example.food_order_app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class RunningApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel("order","Track Order", NotificationManager.IMPORTANCE_HIGH)
        val channel2 = NotificationChannel("food","Foods Notification", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationMgr.createNotificationChannel(channel)
        notificationMgr.createNotificationChannel(channel2)
    }
}