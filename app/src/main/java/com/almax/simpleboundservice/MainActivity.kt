package com.almax.simpleboundservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.almax.simpleboundservice.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var service: BoundService
    private var isBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()
    }

    private fun setupUi() {
        binding.apply {
            btnStart.setOnClickListener {
                if (isBound) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        service.getUpdates()
                            .flowOn(Dispatchers.IO)
                            .catch { e ->
                                Log.e(TAG, "Error: ${e.message}")
                            }
                            .collect { update ->
                                Log.d(TAG, "$update")
                            }
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        "Service not bound",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, BoundService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as BoundService.BoundServiceBinder
            service = serviceBinder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }
}

const val TAG = "MainActivityTAG"