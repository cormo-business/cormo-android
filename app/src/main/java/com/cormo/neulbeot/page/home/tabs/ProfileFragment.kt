package com.cormo.neulbeot.page.home.tabs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R
import com.cormo.neulbeot.auth.AuthRepository
import com.cormo.neulbeot.page.login.LoginMethodActivity

class ProfileFragment: Fragment(R.layout.profile_page)  {

    private val repository by lazy { AuthRepository(requireContext()) }


    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val btnLogout = v.findViewById<LinearLayout>(R.id.btn_logout)

        // 로그아웃 로직
        btnLogout.setOnClickListener {
            repository.logout()
            val intent = Intent(requireContext(), LoginMethodActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            activity?.finishAffinity()

        }


    }

}