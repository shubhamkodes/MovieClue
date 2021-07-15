package com.itsthetom.movieclue.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieModel(
    val adult: Boolean,
    val backdrop_path: String,
    val budget: Int,
    val genres: List<Int>,
    val homepage: String?,
    val id: Int,
    val imdb_id: String,
    val original_language: String,
    val original_title: String?,
    val overview: String,
    val popularity: Float,
    val poster_path: String,
    val release_date: String,
    val revenue: Int,
    val runtime: Int,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    val vote_average:Float,
    val vote_count: Float
) :Parcelable