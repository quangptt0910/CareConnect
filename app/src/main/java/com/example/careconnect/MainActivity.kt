package com.example.careconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.careconnect.dataclass.SnackBarMessage // Assuming R is imported if SnackBarMessage.IdMessage uses it.
// import com.example.careconnect.R // Make sure R is imported if SnackBarMessage.IdMessage directly references R.string resources
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point and primary user interface screen for the CareConnect application.
 *
 * This activity is responsible for setting up the initial UI using Jetpack Compose
 * via the [CareConnectApp] composable. It handles the Android activity lifecycle,
 * processes incoming intents (both initial and new), and enables edge-to-edge display
 * for a modern look and feel.
 *
 * It leverages Hilt for dependency injection, indicated by the [AndroidEntryPoint] annotation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides you
     * with a Bundle containing the activity's previously frozen state, if there was one.
     *
     * In this implementation, it initializes the activity, enables edge-to-edge display
     * for the window, and calls the [render] function to set up the Compose UI
     * based on the initial [Intent] that started the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in [onSaveInstanceState].  <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Prepares the window to draw edge-to-edge.
        render(intent)     // Sets up and displays the Jetpack Compose UI.
    }

    /**
     * This is called for activities that set launchMode to "singleTop" in their
     * manifest, or if a client used the [Intent.FLAG_ACTIVITY_SINGLE_TOP] flag
     * when calling [startActivity]. In either case, when the activity is re-launched
     * while at the top of the activity stack instead of a new instance of the activity
     * being started, [onNewIntent] will be called on the existing instance with the
     * Intent that was used to re-launch it.
     *
     * This method updates the activity's current intent with the new one and then
     * re-renders the UI by calling [render] to reflect any changes that might
     * be necessary due to the new intent. It also re-applies edge-to-edge display settings.
     *
     * @param intent The new intent that was started for the activity.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        enableEdgeToEdge() // Ensures edge-to-edge is still applied. Consider if needed on every new intent
        // or if the initial call in onCreate is sufficient and maintained.
        setIntent(intent)  // Updates the intent returned by getIntent().
        render(intent)     // Re-renders the UI with the new intent.
    }

    /**
     * Sets the main content of the activity using Jetpack Compose.
     *
     * This private function is responsible for calling [setContent] and providing
     * the root composable [CareConnectApp]. It configures [CareConnectApp] with:
     * 1. A `getMessage` lambda function to resolve [SnackBarMessage] instances
     *    into displayable strings (either directly or by looking up a string resource ID).
     * 2. The current [Intent] which might be used by [CareConnectApp] for its initial setup
     *    or navigation.
     *
     * @param intent The [Intent] that the [CareConnectApp] should be rendered with.
     *               This could be the initial intent from [onCreate] or a new intent
     *               from [onNewIntent].
     */
    private fun render(intent: Intent) {
        setContent {
            CareConnectApp(
                getMessage = { snackBarMessage -> // Renamed 'message' to 'snackBarMessage' for clarity
                    when (snackBarMessage) {
                        is SnackBarMessage.StringMessage -> snackBarMessage.message
                        is SnackBarMessage.IdMessage     -> getString(snackBarMessage.message)
                    }
                },
                intent = intent
            )
        }
    }
}