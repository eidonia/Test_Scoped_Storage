package com.example.testscopedstorage.network

import com.example.testscopedstorage.model.PicResponse
import retrofit2.http.GET

interface PictureService {

    @GET("v2/list?page=1&limit=10")
    suspend fun getImageList(): PicResponse

}
