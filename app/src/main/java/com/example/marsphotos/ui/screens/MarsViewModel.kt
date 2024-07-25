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
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.network.MarsApi
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

class MarsViewModel : ViewModel() {
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
    // 這個部分就是所謂的 UI層 和 資料層 沒有分開的地方
    // 這個檔案是MarsViewModel 代表UI的狀態
    // 確實如此 他唯一的一個屬性 就是marsUiState 顧名思義 望文生義
    // UI共三種狀態 分別是 Success Error Loading
    // 其中只有 Success 有資料 至於他有什麼樣的資料 封裝在他的裡面
    // 本例中 是由 MarsPhoto 組成的List
    // 而這些資料 也是由 這個類 用他的方法取抓取的
    // 而這就是所謂的資料層和UI層沒有分開的意思
    private fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = MarsUiState.Loading
            marsUiState = try {

                MarsUiState.Success(
                    //就是在這裡從網路抓取資料 並作為 實參
                    //指派給 MarsUiState.Success 的photos
                    photos = MarsApi.retrofitService.getPhotos()
                )

            } catch (e: IOException) {
                MarsUiState.Error
            } catch (e: HttpException) {
                MarsUiState.Error
            }
        }
    }
}
