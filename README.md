[![](https://jitpack.io/v/sasssass/easynavigation.svg)](https://jitpack.io/#sasssass/easynavigation)


Simplify navigation in your Compose project with auto-generated helper functions for passing both required and optional arguments to your composables.

Installation : 
1. add maven("https://jitpack.io") to your setting.gradle
2. add ksp plugin in your project level gradle and apply it in your app level gradle
3. add dependencies in your app level gradle :

implementation("com.github.sasssass.easynavigation:compose-navigation-annotation:0.3")

ksp("com.github.sasssass.easynavigation:compose-navigation-annotation-helper:0.3")

Using the @NavigationNode annotation above your composable function, you can easily specify parameters like "route", "args", and "optionalArgs". This annotation automatically generates navigation code for you, reducing boilerplate and making your navigation logic cleaner and easier to maintain.

Generated Helper Code Pattern:
The generated navigation helper will be named as: YOUR_COMPOSABLE_FUNCTION_NAME + GraphNode.

Example Usage:

```
@NavigationNode(route = "screen_with_optional_arguments", args = arrayOf("id", "name"),
    optionalArgs = arrayOf("age","nickName")
)
@Composable
fun ScreenWithOptionalArguments(
    id: String,
    name: String,
    nickName: String?,
    age: String?
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "OK, you're $name with id as $name, your nick name is ${nickName?:"Undefined"}, and your age is ${age?:"Undefined"}")
    }
}
```

this gives you a generated code like this : 

```
public object ScreenWithOptionalArgumentsGraphNode {
  public val rawRoute: String = "screen_with_optional_arguments"

  public val route: String =
      "screen_with_optional_arguments/{id}/{name}?age={age}&nickName={nickName}"

  private val args: Array<String> = arrayOf("id", "name")

  private val optionalArgs: Array<String> = arrayOf("age", "nickName")

  public const val ARG_OPTIONAL_age: String = "age"

  public const val ARG_OPTIONAL_nickName: String = "nickName"

  public const val ARG_id: String = "id"

  public const val ARG_name: String = "name"

  public fun navigationRoute(
    arg_id: String,
    arg_name: String,
    optionalArg_age: String?,
    optionalArg_nickName: String?,
  ): String {
    val ret = buildString {
    append(rawRoute)
    append("/$arg_id")
    append("/$arg_name")
    append("?")
    if (optionalArg_age != null) {
        append("age=$optionalArg_age")
    	append("&")
    }
    if (optionalArg_nickName != null) {
        append("nickName=$optionalArg_nickName")
    }}
    return ret
  }
}
```

if you don't declare args or optionalArgs for your annotation the code would be simpler and you won't see any related code for those parameters

ARG_ + name = normal argument key

ARG_OPTIONAL + name = optional argument key

rawRoute = raw version of the route without declraing needed arguments

route = processed version of route which you can pass to you NavHost

navigationRoute = the fucntion which you can pass your argument and optionalArguments (if there're any) and in return it will pass you the route you need to pass to navController.navigate() function

You can then navigate effortlessly with the generated helper functions:

```
navController.navigate(ScreenWithOptionalArgumentsGraphNode.navigationRoute(
                arg_id = id, arg_name = name, optionalArg_nickName = "Sadegh", optionalArg_age = null
            )
```
Or use it within a composable block for handling back stack entries:

```
composable(ScreenWithOptionalArgumentsGraphNode.route) {backStackEntry ->
                    val id = checkNotNull(backStackEntry.arguments?.getString(ScreenWithOptionalArgumentsGraphNode.ARG_id))
                    val name = checkNotNull(backStackEntry.arguments?.getString(ScreenWithOptionalArgumentsGraphNode.ARG_name))
                    val nickName = backStackEntry.arguments?.getString(ScreenWithOptionalArgumentsGraphNode.ARG_OPTIONAL_nickName)
                    val age = backStackEntry.arguments?.getString(ScreenWithOptionalArgumentsGraphNode.ARG_OPTIONAL_age)
                    ScreenWithOptionalArguments(
                        name = name,
                        id = id,
                        nickName = nickName,
                        age = age
                    )
                }
```
