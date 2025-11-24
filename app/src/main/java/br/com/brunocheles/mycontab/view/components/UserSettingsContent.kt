package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.items.User
import br.com.brunocheles.mycontab.ui.theme.NewGray
import coil.compose.AsyncImage
import coil.request.ImageRequest

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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.photoUrl) // A URL do Firebase
                .crossfade(true) // Animação suave ao carregar
                .build(),
            contentDescription = "Profile Picture",
            // 🔹 Se a URL for nula (fallback) ou der erro ao carregar (error), mostra a imagem padrão
            placeholder = painterResource(R.drawable.android),
            error = painterResource(R.drawable.android),
            fallback = painterResource(R.drawable.android),
            contentScale = ContentScale.Crop, // Garante que a foto preencha o círculo sem distorcer
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape) // Corta a imagem em círculo
                .border(width = 2.dp, color = NewGray, shape = CircleShape)
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

@Preview(showBackground = true)
@Composable
fun UserSettingsContentPreview() {
    UserSettingsContent(
        user = User(),
        onLogoutClick = {}
    )
}