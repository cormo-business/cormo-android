package com.cormo.neulbeot.page.home.vm

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cormo.neulbeot.page.home.api.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeModel(app: Application) : AndroidViewModel(app) {

    val TAG: String = "로그"
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

    private val _errorToken = MutableLiveData<String?>()
    val errorToken: LiveData<String?> = _errorToken
    

    fun initHome() {

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
                    _errorToken.value = "초기화 실패: ${e.message ?: "unknown"}"
                    Log.d(TAG, "HomeModel - initHome()${e.message} called")
                }
            }
        }
    }

    fun attendance(){
        val authPrefs = getApplication<Application>().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val checkAttendance = authPrefs.getBoolean("attendance", false)

        if(checkAttendance){
            Log.d(TAG, "attendance 존재함")
            Toast.makeText(getApplication(), "이미 출석했습니다!", Toast.LENGTH_SHORT).show()
        }else{
            viewModelScope.launch (Dispatchers.IO){

                val result = repo.attendance()
                Log.d(TAG, "attendance 없음 호출한다${result}")
                withContext(Dispatchers.Main){
                    result.onSuccess {
                        // 성공 모달 띄우기
                        // 로컬 스토리지 저장하기
                        Toast.makeText(getApplication(), "출석 성공", Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        // 이미 출석했다고 띄우기
                        Toast.makeText(getApplication(), "이미 출석했습니다ㅠㅠ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        initHome()
    }
}