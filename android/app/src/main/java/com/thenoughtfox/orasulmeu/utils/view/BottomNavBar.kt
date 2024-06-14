package com.thenoughtfox.orasulmeu.utils.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun BottomNavBar(
    selected: BottomNavTabs,
    modifier: Modifier = Modifier,
    onSelectTab: (BottomNavTabs) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            tab = BottomNavTabs.Map,
            currentlySelected = selected,
            onSelectTab = onSelectTab,
            modifier = Modifier.weight(1f)
        )

        NavItem(
            tab = BottomNavTabs.Create,
            currentlySelected = selected,
            onSelectTab = onSelectTab,
            modifier = Modifier.weight(1f)
        )

        NavItem(
            tab = BottomNavTabs.Profile,
            currentlySelected = selected,
            onSelectTab = onSelectTab,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun NavItem(
    tab: BottomNavTabs,
    currentlySelected: BottomNavTabs,
    onSelectTab: (BottomNavTabs) -> Unit,
    modifier: Modifier = Modifier
) = Column(
    verticalArrangement = Arrangement.Center,
    modifier = modifier
        .clickable { onSelectTab(tab) }
        .height(64.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val isSelected = tab == currentlySelected
    Icon(
        painter = painterResource(getIconResId(tab)),
        modifier = Modifier.size(24.dp),
        contentDescription = null,
        tint = if (isSelected) OrasulMeuTheme.colors.primary else OrasulMeuTheme.colors.onBackground
    )

    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = getItemText(tab = tab),
        style = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight(500),
            color = if (isSelected) OrasulMeuTheme.colors.primary else OrasulMeuTheme.colors.onBackground
        )
    )

    Canvas(
        modifier = Modifier
            .padding(top = 4.dp)
            .size(6.dp)
    ) {
        if (isSelected) {
            drawCircle(color = Color.Red)
        }
    }
}

private fun getIconResId(tab: BottomNavTabs): Int = when (tab) {
    BottomNavTabs.Map -> R.drawable.ic_menu_map
    BottomNavTabs.Create -> R.drawable.ic_menu_create
    BottomNavTabs.Profile -> R.drawable.ic_menu_profile
}

@Composable
@ReadOnlyComposable
private fun getItemText(tab: BottomNavTabs): String = stringResource(
    when (tab) {
        BottomNavTabs.Map -> R.string.menu_map
        BottomNavTabs.Create -> R.string.menu_create
        BottomNavTabs.Profile -> R.string.menu_profile
    }
)

enum class BottomNavTabs {
    Map, Create, Profile
}

@Preview
@Composable
private fun Preview() = OrasulMeuTheme {
    var selected: BottomNavTabs by remember {
        mutableStateOf(BottomNavTabs.Map)
    }

    BottomNavBar(selected, onSelectTab = { selected = it })
}