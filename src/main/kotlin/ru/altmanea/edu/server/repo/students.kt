package ru.altmanea.edu.server.repo

import ru.altmanea.edu.server.model.Config
import ru.altmanea.edu.server.model.Student

val studentsRepo = ListRepo<Student>()

fun ListRepo<Student>.urlByUUID(uuid: String) =
    this[uuid]?.let {
        Config.studentsURL + it.uuid
    }

fun ListRepo<Student>.urlByFirstname(firstname: String) =
    this.find { it.firstname == firstname }.let {
        if (it.size == 1)
            Config.studentsURL + it.first().uuid
        else
            null
    }


val studentsRepoTestData = listOf(
    Student("Sheldon", "Cooper", "29z"),
    Student("Leonard", "Hofstadter", "29z"),
    Student("Howard", "Wolowitz", "29m"),
    Student("Penny", "Hofstadter", "29a"),
)

val groupsRepo = ListRepo<String>()

fun getListGroup(): MutableList<String> {
    val fin = mutableListOf<String>()
    studentsRepoTestData.map{
        fin.add(it.groop)
    }
    return fin
}

val gropusRepoTestData = getListGroup().toSet()