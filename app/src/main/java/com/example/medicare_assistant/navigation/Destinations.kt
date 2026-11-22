package com.example.medicare_assistant.navigation

// Overlay pages (bottom nav එක නැති full pages). parent = Back ඔබපුවාම යන තැන
enum class Overlay(val parent: Overlay?) {
    NONE(null),
    ADD_MEDICINE(NONE),
    PROFILE(NONE),
    EDIT_PROFILE(PROFILE),
    ALLERGIES(PROFILE)
}