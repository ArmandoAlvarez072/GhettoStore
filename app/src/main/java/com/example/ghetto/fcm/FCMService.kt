package com.example.ghetto.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ghetto.Constants
import com.example.ghetto.R
import com.example.ghetto.product.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        registerNewTokenLocal(newToken)
    }

    private fun registerNewTokenLocal(newToken: String){
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.edit {
            putString(Constants.PROP_TOKEN, newToken)
                .apply()
        }

        Log.i("new token", newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let{
            val imgUrl =  it.imageUrl //"https://images.mediotiempo.com/7WLUMOM_OUTb2ciadbt63WBo4D4=/936x566/uploads/media/2021/07/07/bicho-pusieron-cristiano-espana-foto.jpg"
            if (imgUrl == null){
                sendNotification(it)
            } else {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imgUrl)
                    .into(object : CustomTarget<Bitmap?>(){
                        override fun onLoadCleared(placeholder: Drawable?) {}

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            sendNotification(it, resource)
                        }
                    })
            }



        }
    }

    private fun sendNotification(notification: RemoteMessage.Notification, bitmap: Bitmap? = null){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.notification_channel_id_default)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(notification.body))

        bitmap?.let{
            notificationBuilder
                .setLargeIcon(bitmap)
                .setStyle(NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null))
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,
                getString(R.string.notification_channel_name_default),
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}