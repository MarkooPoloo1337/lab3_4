package ru.altmanea.edu.server.repo

import ru.altmanea.edu.server.model.Lesson

val lessonsRepo = ListRepo<Lesson>()

val lessonsRepoTestData = listOf(
    Lesson("Math", students=setOf(
        "Sheldon",
        "Leonard"
    )),
    Lesson("Phys", students=setOf(
        "Leonard",
        "Howard"
    )),
    Lesson("Story", students=setOf(
        "Howard",
        "Penny"
    )),
)