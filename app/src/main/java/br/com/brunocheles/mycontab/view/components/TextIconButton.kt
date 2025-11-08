package br.com.brunocheles.mycontab.view.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R

@Composable
fun TextIconButton(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = ButtonDefaults.shape,
    @DrawableRes startIcon: Int? = null,
    @DrawableRes endIcon: Int? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        contentPadding = PaddingValues(start = 12.dp),
        shape = shape
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            startIcon?.let {
                Icon(painter = painterResource(id = it), contentDescription = null, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            
            Text(
                modifier = Modifier
                    .height(28.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                text = text,
                fontSize = 18.sp
            )
            
            endIcon?.let {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(painter = painterResource(id = it), contentDescription = null)
            }
        }
    }
}

@Composable
@Preview
fun TextIconButtonPreview() {
    TextIconButton(
        text = "Teste",
        modifier = Modifier.fillMaxWidth(),
        startIcon = R.drawable.rounded_home,
        endIcon = null,
        onClick = {}
    )
}