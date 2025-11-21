package com.cormo.neulbeot.page.exercise.vm

import android.app.Application
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


    fun saveJJuka(
    ) {

        val userId = TokenStorage(getApplication()).getUserId() ?: -1

        viewModelScope.launch(Dispatchers.IO) {

            var result = repo.saveRecord(
                request = SaveRecordRequest(userId)
            )

            withContext(Dispatchers.Main) {
                result.onSuccess { b ->
                    // 성공시 일어날 일
                    _memberId.value = b.memberId
                    Log.d(TAG, "ExerciseViewModel - saveJJuka() 성공 called")

                }.onFailure { e ->
                    Log.d(TAG, "ExerciseViewModel - saveJJuka() 실패 ${e.message} called")
                }
            }
        }
    }

    fun saveSquart() {
        val userId = TokenStorage(getApplication()).getUserId() ?: -1

        viewModelScope.launch(Dispatchers.IO) {

            var result = repo.saveRecord(
                request = SaveRecordRequest(userId)
            )

            withContext(Dispatchers.Main) {
                result.onSuccess { b ->
                    // 성공시 일어날 일
                    _memberId.value = b.memberId
                    Log.d(TAG, "ExerciseViewModel - saveSquart() 성공 called")

                }.onFailure { e ->
                    Log.d(TAG, "ExerciseViewModel - saveSquart() 실패 ${e.message} called")
                }
            }
        }    }
}