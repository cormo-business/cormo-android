package com.cormo.neulbeot.api.signup

import android.content.Context
import android.util.Log
import com.cormo.neulbeot.auth.TokenStorage
import com.cormo.neulbeot.core.ApiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.lang.reflect.Type

object SignupApi {

    // ⚠️ 지양: 전역 고정 service
    // private val service: SignupService = ApiClient.retrofit.create(SignupService::class.java)

    // ✅ 항상 ApiClient.retrofit(context)로 만든 동일 설정의 Retrofit 사용
    private fun service(context: Context): SignupService =
        ApiClient.retrofit(context).create(SignupService::class.java)

    // 라벨→코드 (백업용)
    private val painCode = mapOf(
        "목" to "NECK","어깨" to "SHOULDER","등" to "BACK","손가락" to "FINGER","손목" to "WRIST",
        "팔꿈치" to "ELBOW","골반" to "PELVIS","무릎" to "KNEE","발목" to "ANKLE","발가락" to "TOE","없음" to "NONE"
    )
    private val diseaseCode = mapOf(
        "당뇨" to "DIABETES","고혈압" to "HYPERTENSION","없음" to "NONE"
    )

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())   // ✅ moshi-kotlin 어댑터
        .build()

    /** healthJson 예: {"diseases":["DIABETES"],"pains":["NECK"]} or 한글 라벨 */
    private fun extractHealthCodes(healthJson: String?): List<String> {
        if (healthJson.isNullOrBlank()) return emptyList()
        return try {
            val mapType: Type = Types.newParameterizedType(
                Map::class.java, String::class.java, Any::class.java
            )
            @Suppress("UNCHECKED_CAST")
            val raw = moshi.adapter<Map<String, Any>>(mapType).fromJson(healthJson) ?: return emptyList()

            val diseases = (raw["diseases"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
            val pains = (raw["pains"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

            val dCodes = diseases.map { diseaseCode[it] ?: it } // 이미 코드면 그대로
            val pCodes = pains.map { painCode[it] ?: it }

            (dCodes + pCodes).filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** exerciseJson 예: {"exercises":["FITNESS","BOWLING"],"other":""} */
    private fun extractExerciseCodes(exerciseJson: String?): List<String> {
        if (exerciseJson.isNullOrBlank()) return emptyList()
        return try {
            val mapType: Type = Types.newParameterizedType(
                Map::class.java, String::class.java, Any::class.java
            )
            val raw = moshi.adapter<Map<String, Any>>(mapType).fromJson(exerciseJson) ?: return emptyList()
            @Suppress("UNCHECKED_CAST")
            (raw["exercises"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 회원가입 호출 (콜백은 Main 스레드 보장)
     */
    fun register(
        context: Context,
        username: String,
        password: String,
        birth: String,
        gender: String,
        nickName: String,
        job: String,
        sedentary: Int,
        healthJson: String?,
        exerciseJson: String?,
        callback: (ok: Boolean, codeOrMsg: String) -> Unit
    ) {
        val body = SignupRequest(
            username = username.trim(),
            password = password.trim(),
            birth = birth.trim(),
            gender = gender.trim(),
            nickName = nickName.trim(),
            job = job.trim(),
            sedentary = sedentary,
            health = extractHealthCodes(healthJson),
            exercises = extractExerciseCodes(exerciseJson)
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res: Response<Unit> = service(context).register(body)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) {
                        // ✅ 헤더에서 토큰 꺼내기
                        val access = res.headers()["accessToken"]
                        val refresh = res.headers()["refreshToken"]

                        // ✅ 저장
                        if (!access.isNullOrBlank()) {
                            TokenStorage(context).saveTokens(access = access, refresh = refresh)
                        }
                        callback(true, "${res.code()}")
                    } else {
                        callback(false, "${res.code()}")
                    }
                }
            } catch (t: Throwable) {
                Log.e("SignupApi", "error", t)
                withContext(Dispatchers.Main) {
                    callback(false, t.message ?: "network_error")
                }
            }
        }
    }
}