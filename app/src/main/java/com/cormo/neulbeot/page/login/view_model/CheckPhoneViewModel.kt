package com.cormo.neulbeot.page.login.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cormo.neulbeot.data.model.Result
import com.cormo.neulbeot.data.repository.MemberRepository
import kotlinx.coroutines.launch

// 프리젠테이션 레이어 (ViewModel + Activity)
// LiveData: 읽기 전용 (값 변경 불가, 관찰만 가능)
// MutableLiveData: 값 변경 가능 (setValue/postValue로 변경)
// LiveData 값이 변경되면, observe 중인 UI(View)가 자동으로 갱신됨
class CheckPhoneViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MemberRepository(app.applicationContext)

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _registered = MutableLiveData<Boolean?>()
    val registered: LiveData<Boolean?> = _registered
    // null = 통신 오류 등

    fun checkRegistered(usernameDigits: String) {
        viewModelScope.launch {
            _loading.value = true
            when (val r = repo.isRegistered(usernameDigits)) {
                is Result.Success -> _registered.value = r.data
                is Result.Error -> _registered.value = null
            }
            _loading.value = false
        }
    }
}