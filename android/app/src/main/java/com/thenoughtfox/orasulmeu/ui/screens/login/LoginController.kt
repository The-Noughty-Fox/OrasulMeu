package com.thenoughtfox.orasulmeu.ui.screens.login

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.noughtyfox.authentication.facebook.FacebookSignIn
import com.noughtyfox.authentication.facebook.FacebookSignInAccountData
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.navigation.GlobalNavDestinations
import com.thenoughtfox.orasulmeu.navigation.LocalNavigator
import com.thenoughtfox.orasulmeu.ui.MainActivity
import com.thenoughtfox.orasulmeu.utils.getActivity
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.launch

/**
 * @author Knurenko Bogdan 31.05.2024
 */

@Composable
fun LoginController() {
    val navigator = LocalNavigator.current
    val viewModel: LoginViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)

                    task.result.idToken?.let {
                        scope.launch {
                            viewModel.event.send(Event.SendToken(type = SingInType.Google, it))
                        }
                    }
                }
            }
        }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    is Action.ShowToast -> context.showToast(action.msg)
                    is Action.Auth -> {
                        when (action.type) {
                            SingInType.Google -> {
                                startForResult.launch(getGoogleLoginAuth(context).signInIntent)
                            }

                            SingInType.Facebook -> {
                                signInWithFacebook(context,
                                    onSignIn = { account ->
                                        scope.launch {
                                            viewModel.event.send(
                                                Event.SendToken(
                                                    type = SingInType.Facebook,
                                                    token = account.accessToken.token
                                                )
                                            )
                                        }
                                    }, onFails = { e ->
                                        scope.launch {
                                            viewModel.event.send(
                                                Event.FailedAuth(
                                                    type = SingInType.Facebook,
                                                    msg = e.message ?: ""
                                                )
                                            )
                                        }
                                    })
                            }
                        }
                    }

                    Action.Proceed -> navigator.navigate(GlobalNavDestinations.MapScreen)
                }
            }
        }
    }

    LoginPage(uiState = viewModel.state.collectAsState().value) {
        scope.launch {
            viewModel.event.send(it)
        }
    }
}

private fun getGoogleLoginAuth(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestId()
        .requestProfile()
        .build()

    return GoogleSignIn.getClient(context, gso)
}

private fun signInWithFacebook(
    context: Context,
    onSignIn: (FacebookSignInAccountData) -> Unit,
    onFails: (Exception) -> Unit
) {
    val activity = context.getActivity() as? MainActivity ?: return
    FacebookSignIn.signInWithFacebook(
        activity = activity,
        permissions = listOf("public_profile"),
        onSignIn = { account ->
            onSignIn(account)
        },
        onFails = { exception ->
            onFails(exception)
        }
    )
}