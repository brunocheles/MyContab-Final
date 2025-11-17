package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.Principal

@Composable
fun Logo() {
    Box(
        modifier = Modifier
            .size(140.dp)
            .background(color = Principal, shape = MyContabShapes.large)
            .padding(15.dp)
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.mycontab_logo),
            tint = NewGray,
            contentDescription = null
        )
    }
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = "MyContab",
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = NewGray
    )
}