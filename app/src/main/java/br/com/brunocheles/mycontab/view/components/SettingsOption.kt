package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import br.com.brunocheles.mycontab.ui.theme.NewGray

@Composable
fun SettingsOption(
    text: String,
    icon: Int,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    TextIconButton(
        text = text,
        modifier = Modifier.padding(top = 6.dp),
        shape = RectangleShape,
        startIcon = icon,
        colors = ButtonDefaults.buttonColors(
            contentColor = NewGray,
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        onClick = { onClick?.invoke() },
        enabled = enabled
    )
}

@Composable
fun DividerLine() {
    Spacer(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .background(
                color = NewGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
    )
}
