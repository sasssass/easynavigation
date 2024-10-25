package sass.compose.easynavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = SimpleScreenGraphNode.route) {

                composable(SimpleScreenGraphNode.route) {
                    SimpleScreen(navController = navController)
                }

                composable(ScreenWithArgumentGraphNode.route) {backStackEntry ->
                    val id = checkNotNull(backStackEntry.arguments?.getString(ScreenWithArgumentGraphNode.id))
                    val name = checkNotNull(backStackEntry.arguments?.getString(ScreenWithArgumentGraphNode.name))

                    ScreenWithArgument(navController = navController, id, name)
                }

                composable(ScreenWithOptionalArgumentsGraphNode.route) {backStackEntry ->
                    val id = checkNotNull(backStackEntry.arguments?.getString(ScreenWithOptionalArgumentsGraphNode.id))
                    val name = checkNotNull(backStackEntry.arguments?.getString(ScreenWithOptionalArgumentsGraphNode.name))
                    val nickName = backStackEntry.arguments?.getString(ScreenWithOptionalArgumentsGraphNode.nickName)
                    val age = backStackEntry.arguments?.getString(ScreenWithOptionalArgumentsGraphNode.age)
                    ScreenWithOptionalArguments(
                        name = name,
                        id = id,
                        nickName = nickName,
                        age = age
                    )
                }
            }
        }
    }
}