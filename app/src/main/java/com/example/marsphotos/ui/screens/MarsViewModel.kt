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
package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.data.MarsPhotosRepository
import com.example.marsphotos.model.MarsPhoto
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface MarsUiState {
    data class Success(val photos: List<MarsPhoto>) : MarsUiState
    data object Error : MarsUiState
    data object Loading : MarsUiState
}

class MarsViewModel(
    private val marsPhotosRepository: MarsPhotosRepository
) : ViewModel() {

    /** The mutable State that stores the status of the most recent request */
    var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getMarsPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    private fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = MarsUiState.Loading
            marsUiState = try {

                //注意看 這裡是由marsPhotosRepository的getMarsPhotos()方法
                //去取得 UI需要呈現的東西
                //ViewModel這裡 不負責直接使用到網路的功能
                //也就是 ViewModel這裡 沒有使用到Retrofit的service
                //ViewModel這裡 只是透過 Repository 取得這些資料
                //至於 Repository 怎麼取得這些資料 ViewModel不用知道
                //這就是所謂的 UI 和 資料 分開
                //ViewModel這裡代表UI 代表UI的狀態
                //Repository那邊代表 從網路取得資料

                MarsUiState.Success(
                    photos = marsPhotosRepository.getMarsPhotos()
                )

                //另外 要強調的是 本例中 尚未涉及 資料的儲存 也就是資料庫的概念
                //Repository那邊 只是負責取得資料 並沒有儲存資料
                //ViewModel這裡 只是代表 UI的狀態 雖然確實"暫存"了這些些資料
                //但這並非真正的儲存 當程式關閉 資料也會消失

            } catch (e: IOException) {
                MarsUiState.Error
            } catch (e: HttpException) {
                MarsUiState.Error
            }
        }
    }

    /**
     * Factory for [MarsViewModel] that takes [MarsPhotosRepository] as a dependency
     */
    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {

                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)

                val marsPhotosRepository = application.container.marsPhotosRepository

                MarsViewModel(marsPhotosRepository = marsPhotosRepository)

            }

        }

    }
    /*
     第一：ViewModelFactory
     現在這個ViewModel 使用 所謂的依賴注入
     當需要用到 資料的時候
     程式才會去形成取得資料的物件 注入這個 ViewModel
     也就是說 現在這個ViewModel 它的建構式需要輸入參數了
     安卓預設的ViewModel 是做不到這件事的
     需要用一種所謂的 ViewModelFactory 的概念
     去生成 建構式有輸入引數的ViewModel

     */

    /*
     第二：companion object
     先說它的作用，它是一個物件
     它在類別載入時被初始化
     將所需的依賴項注入到 ViewModel 中
     並且可以在沒有類別實例的情況下存取

     它與類別的具體實例無關，不受實例化的影響
     這是 所謂的 類別的一個 靜態部分

     類別的實例可以直接存取 companion object 中定義的靜態成員

     */
}
