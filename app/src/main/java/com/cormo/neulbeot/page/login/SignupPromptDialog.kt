package com.cormo.neulbeot.page.login

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SignupPromptDialog(
    private val onConfirm: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return AlertDialog.Builder(requireContext())
            .setMessage("가입되지 않은 전화번호입니다.\n회원가입 하시겠습니까?")
            .setPositiveButton("회원가입 하기") { _, _ -> onConfirm() }
            .setNegativeButton("닫기", null)
            .create()
    }

    companion object {
        fun show(
            host: androidx.fragment.app.FragmentActivity,
            onConfirm: () -> Unit
        ) {
            SignupPromptDialog(onConfirm).show(
                host.supportFragmentManager,
                "SignupPromptDialog"
            )
        }
    }
}
