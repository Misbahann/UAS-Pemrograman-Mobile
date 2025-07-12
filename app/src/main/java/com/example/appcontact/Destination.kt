package com.example.appcontact

enum class Destination(val route: String, val label: String) {
    SONGS("songs", "Songs"),
    PHOTOS("album", "albums"),
    MOVIES("movies", "Movies")
}