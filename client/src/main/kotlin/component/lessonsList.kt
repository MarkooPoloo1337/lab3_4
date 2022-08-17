package component

import kotlinext.js.jso
import kotlinx.browser.window
import kotlinx.html.INPUT
import kotlinx.html.js.onClickFunction
import react.Props
import react.dom.*
import react.dom.aria.AriaRole
import react.fc
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.dom.Link
import react.router.useParams
import react.useRef
import ru.altmanea.edu.server.model.Config.Companion.groupsPath
import ru.altmanea.edu.server.model.Config.Companion.groupsURL
import ru.altmanea.edu.server.model.Config.Companion.lessonsURL
import ru.altmanea.edu.server.model.Config.Companion.studentsURL
import ru.altmanea.edu.server.model.Item
import ru.altmanea.edu.server.model.Lesson
import ru.altmanea.edu.server.model.Student
import wrappers.AxiosResponse
import wrappers.QueryError
import wrappers.axios
import kotlin.js.json

external interface LessonListProps : Props {
    var lessons: List<Item<Lesson>>
    var deleteLesson: (Int) -> Unit
}

fun fcLessonList() = fc("LessonList") { props: LessonListProps ->

    h3 { +"Lessons" }
    ol {
        props.lessons.mapIndexed { index, lessonItem ->
            li {
                val lesson = lessonItem.elem.name
                +lesson
                button {
                    +"X"
                    attrs.onClickFunction = {
                        props.deleteLesson(index)
                        window.location.reload()
                    }
                }
            }
        }
    }
}

fun fcContainerLessonList() = fc("QueryLessonList") { _: Props ->
    val queryClient = useQueryClient()
    val asl = useParams()
    val id = asl["studentId"] ?: ""

    val query = useQuery<Any, QueryError, AxiosResponse<Array<Item<Lesson>>>, Any>(
        "studentList",
        {
            axios<Array<String>>(jso {
                url = "$studentsURL$id/lessons"
            })
        }
    )
    val deleteLessonMutation = useMutation<Any, Any, Any, Any>(
        { lessonItem: Item<Lesson> ->
            axios<String>(jso {
                url = studentsURL + id + "/lessons/" + lessonItem.uuid
                method = "Delete"
                headers = json(
                    "Content-Type" to "application/json"
                )
                data = JSON.stringify(lessonItem)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("lessonList")
            }
        }
    )
    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val items = query.data?.data?.toList() ?: emptyList()
        child(fcLessonList()) {
            attrs.lessons = items
            attrs.deleteLesson = {
                deleteLessonMutation.mutate(items[it], null)
            }
        }
    }
}
