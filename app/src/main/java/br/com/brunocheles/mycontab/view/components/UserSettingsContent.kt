package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.items.User
import br.com.brunocheles.mycontab.ui.theme.NewGray

@Composable
fun UserSettingsContent(
    user: User,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(width = 2.dp, color = NewGray, shape = CircleShape)
                .background(Color.Transparent, CircleShape)
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = user.username!!,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        user.email?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            SettingsOption("Account", R.drawable.rounded_person_edit, enabled = false)
            DividerLine()

            SettingsOption("Settings", R.drawable.rounded_settings, enabled = false)
            DividerLine()

            SettingsOption("Crowdfunding", R.drawable.rounded_crowdsource, enabled = false)
            DividerLine()

            SettingsOption(
                text = "Logout",
                icon = R.drawable.rounded_logout,
                onClick = onLogoutClick
            )
        }
    }
}

@Preview(showBackground = true, apiLevel = 35)
@Composable
fun UserSettingsContentPreview() {
    UserSettingsContent(
        user = User(),
        onLogoutClick = {}
    )
}