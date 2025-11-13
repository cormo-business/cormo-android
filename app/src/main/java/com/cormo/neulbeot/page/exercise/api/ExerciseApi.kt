package com.cormo.neulbeot.page.exercise.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ExerciseApi {
    @POST("/api/test")
    suspend fun saveRecord(@Body body: SaveRecordRequest): Response<SaveRecordResponse>

}

data class SaveRecordRequest (
    val memberId: Long,
//    val weight: Integer,
//    val repeatNumber: Integer,
//    val successNumber: Integer,
//    val failNumber: Integer,
//    val exerciseTypeId: Long,
    )

data class SaveRecordResponse (
    val memberId: Long,

)