package com.thenoughtfox.orasulmeu.ui.profile_settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.profile_settings.ProfileSettingsContract.Action
import com.thenoughtfox.orasulmeu.ui.profile_settings.ProfileSettingsContract.Event
import com.thenoughtfox.orasulmeu.ui.profile_settings.ProfileSettingsContract.State
import com.thenoughtfox.orasulmeu.ui.theme.pageModifier
import com.thenoughtfox.orasulmeu.utils.showToast
import com.thenoughtfox.orasulmeu.utils.view.Alert
import com.thenoughtfox.orasulmeu.utils.view.Toolbar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileSettingsPage(uiState: State, action: SharedFlow<Action>, onSendEvent: (Event) -> Unit) {

    var showDeleteAlert by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (showDeleteAlert) {
        Alert(
            onDismissRequest = {
                showDeleteAlert = false
            },
            onConfirmation = {
                showDeleteAlert = false
                onSendEvent(Event.DeleteAccount)
            },
            dialogTitle = stringResource(id = R.string.profile_settings_delete_dialog_title),
            dialogText = stringResource(id = R.string.profile_settings_delete_dialog_desc),
            confirmText = stringResource(id = R.string.profile_settings_delete_dialog_ok),
            dismissText = stringResource(id = R.string.profile_settings_delete_dialog_cancel)
        )
    }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            action.collectLatest {
                when (it) {
                    is Action.ShowToast -> context.showToast(it.msg)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .pageModifier()
            .padding(horizontal = 16.dp)
    ) {
        Toolbar(
            title = stringResource(id = R.string.profile_settings_toolbar_title),
            onBackClickListener = {
                onSendEvent(Event.Back)
            }
        )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            ProfileSettingsPages.entries.forEach { page ->
                ProfileSettingsItem(
                    title = page.title,
                    icon = page.icon,
                    color = page.color,
                    onClick = {
                        when (page) {
                            ProfileSettingsPages.CONTACT_US -> contactUs(context)
                            ProfileSettingsPages.LOGOUT -> onSendEvent(Event.Logout)
                            ProfileSettingsPages.DELETE_ACCOUNT -> showDeleteAlert = true
                        }
                    }
                )
            }
        }
    }
}

private fun contactUs(context: Context) {
    //TODO replace with real email
    val emailUri = Uri.parse("mailto:email_to")
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = emailUri
        putExtra(Intent.EXTRA_SUBJECT, "email_subject")
        putExtra(Intent.EXTRA_TEXT, "email_body")
    }

    context.startActivity(intent)
}

@Composable
private fun ProfileSettingsItem(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    @ColorRes color: Int,
    onClick: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        .clickable { onClick() }
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Icon",
            tint = colorResource(id = color)
        )

        Text(
            text = stringResource(id = title),
            modifier = Modifier.padding(start = 10.dp),
            color = colorResource(id = color)
        )
    }
}

enum class ProfileSettingsPages(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @ColorRes val color: Int = R.color.black
) {
    CONTACT_US(
        title = R.string.profile_settings_contact_us_title,
        icon = R.drawable.ic_contact_support,
    ),

    LOGOUT(
        title = R.string.profile_settings_logout_title,
        icon = R.drawable.ic_logout,
    ),

    DELETE_ACCOUNT(
        title = R.string.profile_settings_delete_account_title,
        icon = R.drawable.ic_person_remove,
        color = R.color.red_500
    )
}

@Preview
@Composable
private fun PreviewProfileSettingsPage() {
    ProfileSettingsPage(
        uiState = State(),
        action = MutableSharedFlow(),
        onSendEvent = {}
    )
}