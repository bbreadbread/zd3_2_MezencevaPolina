package com.example.mytv2

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("alternativeName") val title: String,
    @SerializedName("year") val year: Int,
    @SerializedName("poster") val poster: Poster
)

data class Poster(
    @SerializedName("url") val image: String? // URL постера может быть null
)