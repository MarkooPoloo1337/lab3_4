import component.*
import kotlinx.browser.document
import react.createElement
import react.dom.br
import react.dom.render
import react.query.QueryClient
import react.query.QueryClientProvider
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.router.dom.Link
import wrappers.cReactQueryDevtools

val queryClient = QueryClient()

fun main() {
    render(document.getElementById("root")!!) {
        HashRouter {
            QueryClientProvider {
                attrs.client = queryClient
                Link {
                    attrs.to = "/"
                    +"Home"
                }
                br {}
                Link {
                    attrs.to = "/groups"
                    +"Groups"
                }
                Routes {
                    Route {
                        attrs.index = true
                        attrs.element =
                            createElement(fcAllContainerStudentList())
                    }
                    Route {
                        attrs.path = "/student/:studentId/lessons"
                        attrs.element =
                            createElement(fcContainerLessonList())
                    }
                    Route {
                        attrs.path = "/student/:id"
                        attrs.element =
                            createElement(fcContainerStudent())
                    }
//                    Route {
//                        attrs.path = "/students"
//                        attrs.element =
//                            createElement(fcAllContainerStudentList())
//                    }
                    Route {
                        attrs.path = "/groups"
                        attrs.element =
                            createElement(fcContainerGroupList())
                    }
                    Route {
                        attrs.path = "/groups/:id"
                        attrs.element =
                            createElement(fcContainerStudentList())
                    }
                }
                child(cReactQueryDevtools()) {}
            }
        }
    }
}