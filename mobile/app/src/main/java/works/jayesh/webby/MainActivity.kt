package works.jayesh.webby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import works.jayesh.webby.navigation.WebbyApp
import works.jayesh.webby.ui.theme.WebbyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Allow dynamic theme switching
            var darkMode by remember { mutableStateOf(false) }

            WebbyTheme(darkTheme = darkMode) { WebbyApp(onToggleTheme = { darkMode = !darkMode }) }
        }
    }
}
