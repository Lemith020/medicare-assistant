package com.example.medicare_assistant.profile.view.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow

@Composable
fun AllergyInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes error: Int?,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val label = stringResource(R.string.allergy_name_label)

    Column(modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = scheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .semantics { contentDescription = label },
                placeholder = {
                    Text(
                        text = stringResource(R.string.allergy_name_hint),
                        fontSize = 18.sp,
                        color = scheme.onSurfaceVariant
                    )
                },
                textStyle = TextStyle(fontSize = 18.sp, color = scheme.onSurface),
                singleLine = true,
                isError = error != null,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = scheme.surface,
                    unfocusedContainerColor = scheme.surface,
                    errorContainerColor = scheme.surface,
                    focusedTextColor = scheme.onSurface,
                    unfocusedTextColor = scheme.onSurface,
                    errorTextColor = scheme.onSurface,
                    focusedBorderColor = scheme.onSurface,
                    unfocusedBorderColor = scheme.onSurface,
                    errorBorderColor = scheme.error,
                    cursorColor = scheme.onSurface,
                    errorCursorColor = scheme.error
                )
            )
            Button(
                onClick = onAdd,
                modifier = Modifier.heightIn(min = 56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LuminousYellow, contentColor = Ink)
            ) {
                Text(
                    text = stringResource(R.string.profile_add),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (error != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(error),
                color = scheme.error,
                fontSize = 14.sp
            )
        }
    }
}