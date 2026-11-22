package com.example.medicare_assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.ui.theme.statusColors

@Composable
fun AllergyWarningCard(medicineName: String, modifier: Modifier = Modifier) {
    val colors = statusColors()
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.badBackground, shape)
            .border(1.dp, colors.badBorder, shape)
            .padding(16.dp)
            .semantics(mergeDescendants = true) {}
    ) {
        Text(
            text = stringResource(R.string.allergy_warning_title, medicineName),
            color = colors.badText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.allergy_warning_body),
            color = colors.badText,
            fontSize = 15.sp
        )
    }
}