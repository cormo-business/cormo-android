package com.cormo.neulbeot.store

import com.cormo.neulbeot.data.model.UserModel

object UserStore {
    // 회원가입 끝나고 닉네임/프로필 경로를 여기에 채워 넣어 사용
    var user: UserModel = UserModel(nickname = "닉네임", level = 3, coin = 120)
}
