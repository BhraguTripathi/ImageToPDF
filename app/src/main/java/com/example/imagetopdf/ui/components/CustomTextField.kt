package com.example.imagetopdf.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    icon: ImageVector,
    isPasswordField: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityIconClick: () -> Unit = {}
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = hint,
                color = Color.LightGray
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = hint,
                tint = Color.LightGray
            )
        },
        trailingIcon = {
            if (isPasswordField){
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = onVisibilityIconClick) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = Color.LightGray)
                }
            }
        },
        visualTransformation = if (isPasswordField && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF5D5FEF),
            unfocusedBorderColor = Color.LightGray
        )
    )


}

@Preview(showSystemUi = true)
@Composable
fun CustomTextFieldPreview(){
    MaterialTheme{
        CustomTextField(
            value = "",
            onValueChange = {},
            hint = "Hint",
            icon = Icons.Filled.RemoveRedEye,
            isPasswordField = true,
            passwordVisible = false,
            onVisibilityIconClick = {}
        )
    }
}