package com.cormo.neulbeot.page.home.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.exercise.ExActivity

class ProfileFragment: Fragment(R.layout.challenge_page)  {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val btnStart = v.findViewById<TextView>(R.id.btn_start)
        btnStart.setOnClickListener {
//            startActivity(Intent(requireContext(), ExerciseActivity::class.java))
            startActivity(Intent(requireContext(), ExActivity::class.java))
        }
    }
}