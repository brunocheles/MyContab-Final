package br.com.brunocheles.mycontab

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import br.com.brunocheles.mycontab.ui.theme.MyContabTheme
import br.com.brunocheles.mycontab.view.nav.SetupNavDisplay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyContabTheme {
                MyApp(
                    activity = this
                )
            }
        }
    }
}

@Composable
fun MyApp(
    activity: Activity
) {
    SetupNavDisplay(activity)
}