package com.itsthetom.movieclue.interfaces

import com.itsthetom.movieclue.models.MovieModel

interface SearchAdapterListener {
    fun startDetailsFragment(data:MovieModel)
}