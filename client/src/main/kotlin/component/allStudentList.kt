package component

import kotlinext.js.jso
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
import ru.altmanea.edu.server.model.Config
import ru.altmanea.edu.server.model.Config.Companion.groupsPath
import ru.altmanea.edu.server.model.Config.Companion.studentsURL
import ru.altmanea.edu.server.model.Item
import ru.altmanea.edu.server.model.Student
import wrappers.AxiosResponse
import wrappers.QueryError
import wrappers.axios
import kotlin.js.json

fun fcAllStudentList() = fc("StudentList") { props: StudentListProps ->

    h3{
        + "Lessons"
    }
    ol {
        props.students.mapIndexed { index, studentItem ->
            li {
                val student = Student(studentItem.elem.firstname, studentItem.elem.surname, studentItem.elem.groop)
                Link {
                    attrs.to = "/student/${studentItem.uuid}/lessons"
                    +"${student.fullname} \t"
                }
            }
        }
    }
}

fun fcAllContainerStudentList() = fc("QueryStudentList") { _: Props ->
    val queryClient = useQueryClient()

    val query = useQuery<Any, QueryError, AxiosResponse<Array<Item<Student>>>, Any>(
        "studentList",
        {
            axios<Array<Student>>(jso {
                url = studentsURL
            })
        }
    )

    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val items = query.data?.data?.toList() ?: emptyList()
        child(fcAllStudentList()) {
            attrs.students = items
        }
    }
}
