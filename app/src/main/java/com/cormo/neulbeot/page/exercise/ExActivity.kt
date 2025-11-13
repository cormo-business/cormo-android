package com.cormo.neulbeot.page.exercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R

class ExActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acrivity_main_frame)

        // 처음 실행 시에만 ExStartFragment 붙이기
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_main_frame, ExStartFragment())
                .commit()
        }
    }
}