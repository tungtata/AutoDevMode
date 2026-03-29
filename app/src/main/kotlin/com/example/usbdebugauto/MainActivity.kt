package com.tungtata.usbdebugauto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.tungtata.usbdebugauto.ui.MainScreen
import com.tungtata.usbdebugauto.ui.theme.USBDebugAutoTheme
import com.tungtata.usbdebugauto.viewmodel.MainViewModel

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
