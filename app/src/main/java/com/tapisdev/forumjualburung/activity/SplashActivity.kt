package com.tapisdev.forumjualburung.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tapisdev.forumjualburung.MainActivity
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.admin.DashboardAdminActivity
import com.tapisdev.forumjualburung.activity.pengguna.HomeUserActivity
import com.tapisdev.forumjualburung.activity.penjual.HomePenjualActivity
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.UserPreference

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mUserPref = UserPreference(this)

        if (auth.currentUser != null){

            settingsRef.document("maintenance").get().addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    var mode = task.result?.get("mode")
                    if (mode != null) {
                        if (mode.equals("1")){
                            auth.signOut()
                            val i = Intent(applicationContext,MaintenanceActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                    }
                }
            }

            Log.d("userpref"," jenis user : "+mUserPref.getJenisUser())
            if (mUserPref.getJenisUser() != null){
                if (mUserPref.getJenisUser().equals("admin")){
                    val i = Intent(applicationContext,DashboardAdminActivity::class.java)
                    startActivity(i)
                }else if(mUserPref.getJenisUser().equals("pengguna")){
                    val i = Intent(applicationContext,HomeUserActivity::class.java)
                    startActivity(i)
                }else if(mUserPref.getJenisUser().equals("penjual")){
                    val i = Intent(applicationContext,HomePenjualActivity::class.java)
                    startActivity(i)
                }
                else{
                    val i = Intent(applicationContext,MainActivity::class.java)
                    startActivity(i)
                }
            }else{
                val i = Intent(applicationContext,MainActivity::class.java)
                startActivity(i)
            }
        }else{
            val i = Intent(applicationContext,MainActivity::class.java)
            startActivity(i)
        }
    }
}
