package com.thenoughtfox.orasulmeu.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.noughtyfox.authentication.facebook.FacebookSignIn
import com.noughtyfox.authentication.google.GoogleSignIn
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.MainActivity
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import com.thenoughtfox.orasulmeu.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private val googleSignIn by lazy {
        GoogleSignIn(requireContext(), requireActivity().activityResultRegistry,
            onSignIn = { account ->
                lifecycleScope.launch {
                    viewModel.event.send(
                        Event.SendToken(
                            type = SingInType.Google, account.idToken ?: ""
                        )
                    )
                }
            },
            onFails = { exception ->
                lifecycleScope.launch {
                    viewModel.event.send(
                        Event.FailedAuth(
                            type = SingInType.Google, msg = exception.message ?: ""
                        )
                    )
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            OrasulMeuTheme {
                val uiState by viewModel.state.collectAsState()
                LoginPage(
                    uiState = uiState,
                    onSendEvent = { lifecycleScope.launch { viewModel.event.send(it) } }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(googleSignIn)
        subscribeObservables()
    }

    private fun subscribeObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.action.collect { action ->
                    when (action) {
                        is Action.ShowToast -> context?.showToast(action.msg)
                        is Action.Auth -> {
                            when (action.type) {
                                SingInType.Google -> googleSignIn.signInWithGoogle(
                                    context?.getString(R.string.default_web_client_id)
                                )

                                SingInType.Facebook -> loginWithFacebook()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loginWithFacebook() {
        (activity as? MainActivity)?.let { activity ->
            FacebookSignIn.signInWithFacebook(activity,
                permissions = listOf("public_profile"),
                onSignIn = { account ->
                    lifecycleScope.launch {
                        viewModel.event.send(
                            Event.SendToken(
                                type = SingInType.Facebook, token = account.accessToken.token
                            )
                        )
                    }
                },
                onFails = { exception ->
                    lifecycleScope.launch {
                        viewModel.event.send(
                            Event.FailedAuth(
                                type = SingInType.Facebook, msg = exception.message ?: ""
                            )
                        )
                    }
                }
            )
        }
    }

}