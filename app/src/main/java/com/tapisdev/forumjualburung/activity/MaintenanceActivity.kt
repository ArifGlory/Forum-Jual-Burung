package com.tapisdev.forumjualburung.activity

import android.content.Intent
import android.os.Bundle
import com.tapisdev.forumjualburung.MainActivity
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity

class MaintenanceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val i = Intent(applicationContext,MainActivity::class.java)
        startActivity(i)
        finish()
    }
}
