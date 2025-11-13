package com.cormo.neulbeot.page.exercise.vm

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cormo.neulbeot.auth.TokenStorage
import com.cormo.neulbeot.page.exercise.api.ExerciseRepository
import com.cormo.neulbeot.page.exercise.api.SaveRecordRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciseViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ExerciseRepository(app)
    private val TAG = "로그"

    private val _memberId = MutableLiveData<Long>()
    val memberId: LiveData<Long> = _memberId


    fun save(
        name: String,
        score: Int
    ) {

        val nickname = TokenStorage(getApplication()).getNickname() ?: "닉네임"

        viewModelScope.launch(Dispatchers.IO) {

            var result = repo.saveRecord(
                request = SaveRecordRequest(nickname)
            )

            withContext(Dispatchers.Main) {
                result.onSuccess { b ->
                    // 성공시 일어날 일
                    _memberId.value = b.memberId
                    Log.d(TAG, "HomeModel - initHome() 성공 called")

                }.onFailure { e ->
                    Log.d(TAG, "HomeModel - initHome() 실패 ${e.message} called")
                }
            }
        }
    }
}