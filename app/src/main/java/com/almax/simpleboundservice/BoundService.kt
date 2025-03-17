package com.almax.simpleboundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BoundService : Service() {

    private val binder: Binder = BoundServiceBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class BoundServiceBinder : Binder() {
        fun getService() = this@BoundService
    }

    fun getUpdates(): Flow<Int> {
        var update = 0
        return flow {
            if (update == 10) {
                stopSelf()
            }
            while (update < 10) {
                emit(update)
                delay(2000L)
                update += 1
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }
}