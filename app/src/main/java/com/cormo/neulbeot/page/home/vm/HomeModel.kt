package com.cormo.neulbeot.page.home.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cormo.neulbeot.page.home.api.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeModel(app: Application) : AndroidViewModel(app) {

    private val repo = HomeRepository(app)

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    private val _level = MutableLiveData<Int>()
    val level: LiveData<Int> = _level

    private val _point = MutableLiveData<Int>()
    val point: LiveData<Int> = _point

    private val _attendanceNum = MutableLiveData<Int>()
    val attendanceNum: LiveData<Int> = _attendanceNum

    private val _profilePath = MutableLiveData<String?>()
    val profilePath: LiveData<String?> = _profilePath

    private val _levelProgress = MutableLiveData<Int>()
    val levelProgress: LiveData<Int> = _levelProgress

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var loaded = false


    fun loadIfNeeded() {
        if (loaded) return
        loaded = true


        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.initHome()
            Log.d("로그", result.toString())

            withContext(Dispatchers.Main) {
                result.onSuccess { b ->
                    _nickname.value = b.nickname
                    _level.value = b.level
                    _point.value = b.point
                    _attendanceNum.value = b.attendanceNum
                    _profilePath.value = b.profilePath
                    _levelProgress.value = b.levelProgress
                }.onFailure { e ->
                    _error.value = "초기화 실패: ${e.message ?: "unknown"}"
                    loaded = false   // 실패 시 다시 시도 가능하게
                }
            }
        }
    }
}