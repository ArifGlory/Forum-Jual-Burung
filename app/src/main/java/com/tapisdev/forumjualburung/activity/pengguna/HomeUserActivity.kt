package com.tapisdev.forumjualburung.activity.pengguna

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.fragment.UserInformasiFragment
import com.tapisdev.forumjualburung.fragment.UserTokoFragment
import com.tapisdev.forumjualburung.fragment.UserProfilFragment
import kotlinx.android.synthetic.main.activity_home_user.*

class HomeUserActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_user)

        nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val fragment = UserTokoFragment.newInstance()
        addFragment(fragment)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_toko -> {
                val fragment = UserTokoFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_informasi -> {
                val fragment = UserInformasiFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tutorial -> {

            }
            R.id.navigation_profil -> {
                val fragment = UserProfilFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
            .commit()
    }
}
