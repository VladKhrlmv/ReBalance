package com.rebalance.ui.component.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebalance.ui.theme.md_theme_light_onPrimary

@Composable
fun ReferenceButton(label: String, paddingTop: Dp, image: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = paddingTop)
            .width(250.dp)
            .height(45.dp),
        shape = RoundedCornerShape(40.dp),
//        border = BorderStroke(1.dp, Color.Gray),
//        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
        Box {
            Image(
                painterResource(id = image),
                contentDescription = "icon",
                modifier = Modifier
                    .size(25.dp),
                colorFilter = ColorFilter.tint(color = md_theme_light_onPrimary)

            )
            Text(
                text = label,
//                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp
            )
        }

    }
}
