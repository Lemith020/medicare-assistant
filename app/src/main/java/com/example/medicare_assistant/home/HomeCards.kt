package com.example.medicare_assistant.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.ui.components.PrimaryButton
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow

@Composable
fun CourseCompleteCard(medicineName: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuminousYellow, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CheckBadge()
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.home_complete_title),
                modifier = Modifier.semantics { heading() },
                color = Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.home_complete_body, medicineName),
            color = Ink,
            fontSize = 17.sp,
            lineHeight = 24.sp
        )
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = LuminousYellow)
        ) {
            Text(stringResource(R.string.action_got_it), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CheckBadge() {
    Canvas(Modifier.size(44.dp)) {
        drawCircle(Ink)
        val w = size.width
        val h = size.height
        val check = Path().apply {
            moveTo(w * 0.27f, h * 0.52f)
            lineTo(w * 0.43f, h * 0.67f)
            lineTo(w * 0.73f, h * 0.35f)
        }
        drawPath(
            check,
            LuminousYellow,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun EmptyHomeCard(onAddMedicine: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.home_empty_title),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.home_empty_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 17.sp
        )
        PrimaryButton(
            text = stringResource(R.string.action_add_medicine),
            onClick = onAddMedicine,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}