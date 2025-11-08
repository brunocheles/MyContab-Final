package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TrailingIconButton(
    onClick: () -> Unit,
    text: String,
    icon: Painter,
    iconDesc: String,
    colors: ButtonColors,
    modifier: Modifier,
    shape: Shape,
    iconTint: Color
) {
    Button(
        onClick = onClick,
        colors = colors,
        modifier = modifier,
        shape = shape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth() // Ensure the row fills the button's content area
        ) {
            Icon(
                painter = icon,
                contentDescription = iconDesc,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
            // Spacer to push the text to the center and the icon to the end
            Spacer(Modifier.weight(1f))

            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 4.dp) // Add some padding between text and icon
            )

            // Spacer to balance the layout and center the text visually
            Spacer(Modifier.weight(1f))
        }
    }
}