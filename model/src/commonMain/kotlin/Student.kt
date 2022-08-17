package ru.altmanea.edu.server.model

import kotlinx.serialization.*

@Serializable
class Student(
    val firstname: String,
    val surname: String,
    val groop: String
) {
    val fullname: String
        get() = "$firstname $surname $groop"
}