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
 * 先定義 一個功能 也就是 取得MarsPhoto的List
 */
interface MarsPhotosRepository {
    /** Fetches list of MarsPhoto from marsApi */
    suspend fun getMarsPhotos(): List<MarsPhoto>
}

/**
 * Network Implementation of Repository that fetch mars photos list from marsApi.
 * 再定義 一個類 實現那個功能
 */
class NetworkMarsPhotosRepository(
    private val marsApiService: MarsApiService
) : MarsPhotosRepository {
    /** Fetches list of MarsPhoto from marsApi*/
    override suspend fun getMarsPhotos(): List<MarsPhoto> = marsApiService.getPhotos()
}

/*
 小結：

     這邊實作一個存放區 將與 網路服務的直接互動
     從 ViewModel 中抽離出來

     先定義一個介面
     再去定義 實現那個介面的類
     而不是 直接定義一個類 在裡面實現內建那個介面的方法
     這分離了 功能本身 和 實現功能的東西

     使用這個存放區的地方 是指向
     一個實現了MarsPhotosRepository介面的類

     如果我想對存放區做一些修改和測試
     修改了NetworkMarsPhotosRepository
     使用這個存放區的地方 依然是指向
     一個實現了MarsPhotosRepository介面的類

     不需要配合這裡做出變動
 */