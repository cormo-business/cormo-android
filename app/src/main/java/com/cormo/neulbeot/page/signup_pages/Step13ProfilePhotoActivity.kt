package com.cormo.neulbeot.page.signup_pages

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.signup_pages.api.SignupService
import com.cormo.neulbeot.page.home.HomeActivity

class Step13ProfilePhotoActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var ivProfile: ImageView
    private lateinit var ivEdit: ImageView
    private lateinit var tvNickname: TextView
    private lateinit var btnComplete: Button
    private lateinit var btnStart: Button
    private lateinit var progress: ProgressBar

    private var imagePath: String? = null
    private var submitting = false
    private var completed = false

    // 갤러리에서 이미지 선택
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uri: Uri? = data?.data
            if (uri != null) {
                val path = uriToPath(uri)
                imagePath = path
                // 미리보기
                contentResolver.openInputStream(uri)?.use {
                    val bmp = BitmapFactory.decodeStream(it)
                    ivProfile.setImageBitmap(bmp)
                }
                ivEdit.visibility = View.VISIBLE
                updateCompleteEnabled()
                // userProvider(로컬)에 프로필 경로만 저장 (닉네임은 건드리지 않음)
                val current = UserStore.user
                if (current != null) {
                    UserStore.user = current.copy(profilePath = path)
                } else {
                    val nick = SignupFormStore.nickName.orEmpty()
                    UserStore.user = UserModel(nickname = nick, profilePath = path)
                }
            }
        }
    }

    private fun uriToPath(uri: Uri): String {
        // 간단히 Uri 문자열 그대로 보관 (실제 파일경로 변환 필요시 ContentResolver 쿼리 작성)
        return uri.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step13_profile_photo)

        btnBack = findViewById(R.id.btnBack)
        ivProfile = findViewById(R.id.ivProfile)
        ivEdit = findViewById(R.id.ivEdit)
        tvNickname = findViewById(R.id.tvNickname)
        btnComplete = findViewById(R.id.btnComplete)
        btnStart = findViewById(R.id.btnStart)
        progress = findViewById(R.id.progress)

        btnBack.setOnClickListener { finish() }

        // Step12에서 저장된 닉네임(기본 '사용자' 금지 → 빈문자면 그냥 빈칸)
        tvNickname.text = SignupFormStore.nickName.orEmpty()

        // 이미지 탭 → 갤러리
        ivProfile.setOnClickListener { openGallery() }
        ivEdit.setOnClickListener { openGallery() }

        btnComplete.setOnClickListener {
            // ‘완료’ → 회원가입 API 호출 → 성공 시 ‘늘액션 시작하기’ 버튼 노출
            if (!submitting && imagePath != null) {
                registerAndComplete()
            }
        }

        btnStart.setOnClickListener {
            // 홈으로 이동 (스택 제거)
            startActivity(Intent(this, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
        }

        updateCompleteEnabled()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun updateCompleteEnabled() {
        val canProceed = imagePath != null
        btnComplete.isEnabled = canProceed
        btnComplete.setBackgroundColor(if (canProceed) 0xFF00C8E5.toInt() else 0xFFB3E5FC.toInt())
    }

    private fun registerAndComplete() {
        submitting = true
        showLoading(true)

        // Flutter의 SignupApi.register와 동일 필드로 전송
        // (여기서는 이미지 업로드 없음, 서버에도 안보냄)
        SignupService.register(
            context = this,
            username = SignupFormStore.username.orEmpty(),
            password = SignupFormStore.password.orEmpty(),
            birth = SignupFormStore.birth.orEmpty(),
            gender = SignupFormStore.gender.orEmpty(), // FEMALE/MALE
            nickName = SignupFormStore.nickName.orEmpty(),
            job = SignupFormStore.job.orEmpty(),
            sedentary = SignupFormStore.sedentary ?: 0,
            healthJson = SignupFormStore.health,
            exerciseJson = SignupFormStore.exercise
        ) { ok, codeOrMsg ->
            submitting = false
            showLoading(false)
            if (ok) {
                // 토스트(선택)
                Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()

                // 스택 제거 + 홈 진입
                val intent = Intent(this, HomeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
                // 혹시 모를 잔여 액티비티 제거
                finish()
            } else {
                Toast.makeText(this, "회원가입 실패: $codeOrMsg", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progress.visibility = if (show) View.VISIBLE else View.GONE
        findViewById<View>(R.id.content).alpha = if (show) 0.5f else 1f
        btnComplete.isEnabled = !show && imagePath != null
    }
}
