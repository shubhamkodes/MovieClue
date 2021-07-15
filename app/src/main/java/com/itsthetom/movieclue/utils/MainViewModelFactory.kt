package com.itsthetom.movieclue.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itsthetom.movieclue.MainViewModel

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }

}