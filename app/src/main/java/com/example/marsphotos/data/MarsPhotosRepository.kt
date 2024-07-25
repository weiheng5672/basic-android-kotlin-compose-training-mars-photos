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
package com.example.marsphotos.data

import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.network.MarsApiService

/**
 * Repository that fetch mars photos list from marsApi.
 */
//這是一個介面 他的方法是一個函數
//這個函數 輸出 MarsPhoto的List
//但是 他要怎麼實現 這個介面本身沒有定義
interface MarsPhotosRepository {
    /** Fetches list of MarsPhoto from marsApi */
    suspend fun getMarsPhotos(): List<MarsPhoto>
}

/**
 * Network Implementation of Repository that fetch mars photos list from marsApi.
 */
//這是實現上面那個介面的物件
//這個物件雖然身為所謂的 Repository 但他不儲存資料 只是抓取資料

//他負責 使用Retrofit的service去取得資料
//他負責 透過Retrofit的service去實作介面的功能

//這些資料將提供給 UI層 也就是ViewModel去使用

class NetworkMarsPhotosRepository(
    private val marsApiService: MarsApiService
) : MarsPhotosRepository {
    /** Fetches list of MarsPhoto from marsApi*/
    override suspend fun getMarsPhotos(): List<MarsPhoto> = marsApiService.getPhotos()
}

//在這邊解釋下 簡單的功能這麼分開 乍看是複雜化
//但是 我只能透過 Retrofit 從網路抓取資料嗎?
//如果 我只是想測試 可不可以先用假資料進行?
//這種分離 將使得 這種想法變的可行