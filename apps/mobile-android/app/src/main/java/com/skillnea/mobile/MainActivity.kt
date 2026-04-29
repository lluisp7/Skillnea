package com.skillnea.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.skillnea.mobile.view.SkillneaApp
import com.skillnea.mobile.view.theme.SkillneaTheme

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
