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
import react.useRef
import ru.altmanea.edu.server.model.Config.Companion.groupsPath
import ru.altmanea.edu.server.model.Config.Companion.groupsURL
import ru.altmanea.edu.server.model.Config.Companion.studentsURL
import ru.altmanea.edu.server.model.Item
import ru.altmanea.edu.server.model.Student
import wrappers.AxiosResponse
import wrappers.QueryError
import wrappers.axios
import kotlin.js.json

external interface GroupListProps : Props {
    var groups: List<Item<String>>
}
fun fcGroupList() = fc("StudentList") { props: GroupListProps ->

    h3 { +"Groups" }
    ol {
        props.groups.map { groupItem ->
            li {
                val group = groupItem.elem
                Link {
                    attrs.to = "/groups/${groupItem.elem}"
                    +"$group \t"
                }
            }
        }
    }
}

fun fcContainerGroupList() = fc("QueryStudentList") { _: Props ->
    val queryClient = useQueryClient()

    val query = useQuery<Any, QueryError, AxiosResponse<Array<Item<String>>>, Any>(
        "studentList",
        {
            axios<Array<String>>(jso {
                url = groupsURL
            })
        }
    )
    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val items = query.data?.data?.toList() ?: emptyList()
        child(fcGroupList()) {
            attrs.groups = items
        }
    }
}
