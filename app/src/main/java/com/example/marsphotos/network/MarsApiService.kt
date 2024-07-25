/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.marsphotos.network

import com.example.marsphotos.model.MarsPhoto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

    private const val BASE_URL =
        "https://android-kotlin-fun-mars-server.appspot.com"

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     * 這部分就是建構一個 Retrofit物件 去處理和網路介接 的功能
     * 這個物件還配備了 序列化/反序列化 轉換器 可以將網路格式資料和Kotlin物件互相轉換
     */
    val json = Json { ignoreUnknownKeys = true }

    val contentType = "application/json".toMediaType()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(contentType))
        .baseUrl(BASE_URL)
        .build()

    /**
     * Retrofit service object for creating api calls
     * 這部分定義了需要利用 retrofit 的自訂義函數
     * 並使用註解 標註了 哪些函數 需要使用 哪些與網路相關的請求
     */
    interface MarsApiService {
        @GET("photos")
        suspend fun getPhotos(): List<MarsPhoto>
    }

    /**
     * A public Api object that exposes the lazy-initialized Retrofit service
     * 定義一個  以Retrofit物件為核心 實現自訂義介面功能的 物件
     */
    object MarsApi {
        val retrofitService: MarsApiService by lazy {
            retrofit.create(MarsApiService::class.java)
        }
}
