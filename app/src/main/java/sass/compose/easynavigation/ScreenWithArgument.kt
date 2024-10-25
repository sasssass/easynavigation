package sass.compose.easynavigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import sass.compose.annotation.NavigationNode

@NavigationNode(route = "sample_screen")
@Composable
fun SimpleScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(modifier = Modifier.testTag("btn1"), onClick = {
            navController.navigate(ScreenWithArgumentGraphNode.navigationRoute(
                id = "123", name = "Ali"
            ))
        }) {
            Text(text = "Go To Screen with Arguments")
        }
    }
}

@NavigationNode(route = "screen_with_argument", args = arrayOf("id", "name"))
@Composable
fun ScreenWithArgument(navController: NavController, id: String, name: String) {
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "You're $name with id as $id")
        Button(modifier = Modifier.testTag("btn2"), onClick = {
            navController.navigate(ScreenWithOptionalArgumentsGraphNode.navigationRoute(
                id = id, name = name, nickName = "Sadegh", age = null
            ))
        }) {
            Text(text = "Go To Screen with Optional Arguments")
        }
    }
}

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
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "OK, you're $name with id as $name, your nick name is ${nickName?:"Undefined"}, and your age is ${age?:"Undefined"}")
    }
}