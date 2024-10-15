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
