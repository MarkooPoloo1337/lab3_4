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
import ru.altmanea.edu.server.model.Config.Companion.groupsPath
import ru.altmanea.edu.server.model.Config.Companion.studentsURL
import ru.altmanea.edu.server.model.Item
import ru.altmanea.edu.server.model.Student
import wrappers.AxiosResponse
import wrappers.QueryError
import wrappers.axios
import kotlin.js.json

external interface StudentListProps : Props {
    var students: List<Item<Student>>
    var addStudent: (String, String, String) -> Unit
    var deleteStudent: (Int) -> Unit
}

fun fcStudentList() = fc("StudentList") { props: StudentListProps ->

    val firstnameRef = useRef<INPUT>()
    val surnameRef = useRef<INPUT>()
    val groupRef = useRef<INPUT>()

    span {
        p {
            +"Firstname: "
            input {
                ref = firstnameRef
            }
        }
        p {
            +"Surname: "
            input {
                ref = surnameRef
            }
        }
        p {
            +"Group: "
            input {
                ref = groupRef
            }
        }
        button {
            +"Add student"
            attrs.onClickFunction = {
                firstnameRef.current?.value?.let { firstname ->
                    surnameRef.current?.value?.let { surname ->
                        groupRef.current?.value?.let { group ->
                            props.addStudent(firstname, surname, group)
                        }
                    }
                }
            }
        }
    }

    ol {
        props.students.mapIndexed { index, studentItem ->
            li {
                val student = Student(studentItem.elem.firstname, studentItem.elem.surname, studentItem.elem.groop)
                Link {
                    attrs.to = "/student/${studentItem.uuid}"
                    +"${student.fullname} \t"
                }
                button {
                    +"X"
                    attrs.onClickFunction = {
                        props.deleteStudent(index)
                    }
                }
            }
        }
    }
}

fun fcContainerStudentList() = fc("QueryStudentList") { _: Props ->
    val queryClient = useQueryClient()
    val asl = useParams()
    val id = asl["id"] ?: ""

    val query = useQuery<Any, QueryError, AxiosResponse<Array<Item<Student>>>, Any>(
        "studentList",
        {
            axios<Array<Student>>(jso {
                url = groupsPath + id
            })
        }
    )
    val addStudentMutation = useMutation<Any, Any, Any, Any>(
        { student: Student ->
            axios<String>(jso {
                url = studentsURL
                method = "Post"
                headers = json(
                    "Content-Type" to "application/json"
                )
                data = JSON.stringify(student)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("studentList")
            }
        }
    )

    val deleteStudentMutation = useMutation<Any, Any, Any, Any>(
        { studentItem: Item<Student> ->
            axios<String>(jso {
                url = "$studentsURL/${studentItem.uuid}"
                method = "Delete"
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("studentList")
            }
        }
    )

    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val items = query.data?.data?.toList() ?: emptyList()
        child(fcStudentList()) {
            attrs.students = items
            attrs.addStudent = { f, s, g->
                addStudentMutation.mutate(Student(f, s, g), null)
            }
            attrs.deleteStudent = {
                deleteStudentMutation.mutate(items[it], null)
            }
        }
    }
}
