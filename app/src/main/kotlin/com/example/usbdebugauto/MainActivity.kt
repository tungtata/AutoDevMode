package com.example.usbdebugauto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.usbdebugauto.ui.MainScreen
import com.example.usbdebugauto.ui.theme.USBDebugAutoTheme
import com.example.usbdebugauto.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContent {
            USBDebugAutoTheme {
                MainScreen(viewModel = viewModel, context = this)
            }
        }
    }
}
