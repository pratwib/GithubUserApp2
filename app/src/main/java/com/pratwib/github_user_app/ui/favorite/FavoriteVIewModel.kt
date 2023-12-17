package com.pratwib.github_user_app.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pratwib.github_user_app.database.FavoriteEntity
import com.pratwib.github_user_app.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavRepository: FavoriteRepository = FavoriteRepository(application)
    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = mFavRepository.getAllFavorites()
}