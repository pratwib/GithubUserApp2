package com.pratwib.github_user_app.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pratwib.github_user_app.datasource.SearchResponse
import com.pratwib.github_user_app.datasource.UserResponse
import com.pratwib.github_user_app.networking.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserRepository {
    val user = MutableLiveData<ArrayList<UserResponse>?>()
    val userSearch = MutableLiveData<ArrayList<UserResponse>?>()
    val isLoading = MutableLiveData<Boolean>()
    val isDataFailed = MutableLiveData<Boolean>()
    var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private const val TAG = "UserRepo"
    suspend fun getListUser() {
        coroutineScope.launch {
            isLoading.value = true
            val getUserDeferred = ApiConfig.getApiService().getListUsersAsync()
            try {
                isLoading.value = false
                isDataFailed.value = false
                user.postValue(getUserDeferred)
            } catch (e: Exception) {
                isLoading.value = false
                isDataFailed.value = true
                Log.e(TAG, "onFailure: ${e.message.toString()}")
            }
        }
    }

    fun getUserBySearch(user: String) {
        isLoading.value = true
        val client = ApiConfig.getApiService().getUserBySearch(user)
        client.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    isLoading.value = false
                    isDataFailed.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.items != null) {
                            userSearch.postValue(responseBody.items)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                isLoading.value = false
                isDataFailed.value = true
                Log.e("UserRepo", "onFailure: ${t.message.toString()}")
            }

        })

    }
}