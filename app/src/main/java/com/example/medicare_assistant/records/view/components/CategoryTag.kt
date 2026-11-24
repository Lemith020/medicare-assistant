package com.example.medicare_assistant.records.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.records.data.RecordCategory
import com.example.medicare_assistant.ui.theme.statusColors

@Composable
fun CategoryTag(category: RecordCategory, modifier: Modifier = Modifier) {
    val colors = statusColors()
    val background = when (category) {
        RecordCategory.RECEIPT -> colors.upBackground
        RecordCategory.REPORT -> colors.okBackground
    }
    val content = when (category) {
        RecordCategory.RECEIPT -> colors.upText
        RecordCategory.REPORT -> colors.okText
    }

    Text(
        text = stringResource(category.labelRes),
        modifier = modifier
            .background(background, CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = content,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
}