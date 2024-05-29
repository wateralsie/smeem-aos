package com.sopt.smeem.presentation.mypage

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.sopt.smeem.presentation.compose.theme.SmeemTheme
import com.sopt.smeem.presentation.mypage.navigation.MyPageNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createOpenAnimation()
        setContent {
            SmeemTheme {
                val navController = rememberNavController()
                MyPageNavHost(navController = navController)
            }
        }
    }

    override fun onDestroy() {
        createCloseAnimation()
        super.onDestroy()
    }

    private fun createOpenAnimation() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN, 0, 0
            )
        } else {
            overridePendingTransition(0, 0)
        }
    }

    private fun createCloseAnimation() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE, 0, 0
            )
        } else {
            overridePendingTransition(0, 0)
        }
    }

}

