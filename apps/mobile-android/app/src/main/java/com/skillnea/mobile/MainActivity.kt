package com.skillnea.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.skillnea.mobile.ui.SkillneaApp
import com.skillnea.mobile.ui.theme.SkillneaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillneaTheme {
                SkillneaApp()
            }
        }
    }
}
