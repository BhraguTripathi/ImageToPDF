package com.example.imagetopdf.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.imagetopdf.ui.theme.IconTint

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search"
) {


    OutlinedTextField(
        value = query,
        onValueChange = {
                newText -> onQueryChange(newText)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 10.dp),
        placeholder = {
            Text(text = "Search", color = Color.Gray)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search_Icon",
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(30.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = IconTint,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = IconTint,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}