package sass.compose.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NavigationNode(
    val route: String,
    val args: Array<String> = [],
    val optionalArgs: Array<String> = []
)


