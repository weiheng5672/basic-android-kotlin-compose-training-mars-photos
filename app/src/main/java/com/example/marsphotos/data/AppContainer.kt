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

import com.example.marsphotos.network.MarsApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

/**
 * Dependency Injection container at the application level.
 */
// 身為 所謂的容器 他必然是要裝一些東西
// 先定義 他需要裝什麼樣的東西
interface AppContainer {

    val marsPhotosRepository: MarsPhotosRepository
    /*
     從這個屬性的定義，可以解釋，也才能具體解釋
     為什麼 要分兩階段去定義存放區

     所謂的分兩階段定義 就是先定義一個介面
     再去定義 實現那個介面的類
     而不是 直接定義一個類 在裡面實現內建那個介面的方法
     他分離了 功能本身 和 實現功能的東西

     這裡不那麼做 是因為 我在這邊 容器這裡 定義了一個變數
     去指向一個存放區的時候 我定義那個變數的類型 是MarsPhotosRepository
     也就是 只要是實現那個介面的類別 都可以被我參照

     如果我需要 對存放區做一些測試
     我可以只去更動 實現MarsPhotosRepository的類別
     這裡不需要修改

     同時 容器這裡 為什麼也要分兩階段去定義 也是相同的道理
     使用這個容器的地方 是指向 一個實現了AppContainer這個介面的類

     如果我想對這個容器做一些修改和測試 修改了下面的DefaultAppContainer
     使用這個容器的地方 依然是指向 一個實現了AppContainer這個介面的類
     不需要配合這裡做出變動

     實際上難免還是會動到一些地方 但精神就是這個精神
     分離了 功能本身 和 實現功能的東西
     除了善用介面、繼承，還使用依賴注入的概念，詳見下面的小結

     */
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
// 再定義
// 他實際上會裝的東西
// 一個 NetworkMarsPhotosRepository
// 他被延遲初始化 也就是 需要用到它的時候 才會被初始化
// 它會負責從網路 抓資料
// UI 再透過 容器 去取得 它
// 透握 它 去取得 資料
class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://android-kotlin-fun-mars-server.appspot.com/"

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: MarsApiService by lazy {
        retrofit.create(MarsApiService::class.java)
    }

    /**
     * DI implementation for Mars photos repository
     */
    override val marsPhotosRepository: MarsPhotosRepository by lazy {
        NetworkMarsPhotosRepository(retrofitService)
    }
}

/*
小結:

原本該程式 是 MarsViewModel 直接透過網路API去抓取資料
而這就是所謂的 資料層 和 UI層 沒有分開

現在這個情況已經改變了

透過網路API去抓取資料的部分
被隔離出來 形成 資料層
也就是data資料夾

並且在 資料層中 進一步將 "取得資料" "從網路" 切割
Repository 去取得資料
Container 指明了 是從網路 實現取得資料的功能

現在 MarsViewModel 那邊
是透過 Container 從 Repository 去取得資料
至於那些資料 是怎麼來的 從哪來的
MarsViewModel 不再需要知道
而這就是所謂的 資料層 和 UI層 分開

還有 所謂的依賴注入 就是在這邊實現的
只有當程式需要用到 這個部分的時候
它才會被初始化 送到 MarsViewModel 那邊

依賴注入又被稱為控制反轉
是因為 這種邏輯就相當於 我是先造一輛車 這輛車沒有引擎
我可以 在需要用車的時候 根據情況 去決定它要裝 燃氣機 還是 電動機
這就是所謂的依賴注入
雖然 車依賴於動力
但是 是 先有車 再視清況 決定動力
這和實際的邏輯是反過來的 所以叫做反轉

*/
