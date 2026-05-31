package com.clndr.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.glance.appwidget.GlanceAppWidgetReceiver

object PinWidgetHelper {

    fun requestPin(ctx: Context, provider: Class<out GlanceAppWidgetReceiver>) {
        val mgr = AppWidgetManager.getInstance(ctx)
        val comp = ComponentName(ctx, provider)
        if (mgr.isRequestPinAppWidgetSupported) {
            val callback = PendingIntent.getBroadcast(
                ctx,
                0,
                Intent(ACTION_PIN_RESULT).setPackage(ctx.packageName),
                PendingIntent.FLAG_IMMUTABLE,
            )
            mgr.requestPinAppWidget(comp, null, callback)
        } else {
            Toast.makeText(
                ctx,
                "Long-press your home screen and add the clndr widget from the picker.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private const val ACTION_PIN_RESULT = "com.clndr.app.PIN_RESULT"
}
