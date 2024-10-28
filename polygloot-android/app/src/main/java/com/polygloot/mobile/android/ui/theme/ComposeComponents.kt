package com.polygloot.mobile.android.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polygloot.mobile.android.R
import com.polygloot.mobile.android.ui.utils.Consts.Companion.SUPPORTED_LANGUAGES

@Composable
fun LanguagePicker(
    modifier: Modifier = Modifier,
    languages: List<Pair<String, String>>,
    preselected: Pair<String, String>,
    onSelectionChanged: (selection: Pair<String, String>) -> Unit
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) }

    Box {
        Column(modifier = modifier) {
            OutlinedTextField(
                modifier = Modifier
                    .width(150.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(50.dp)
                    ),
                value = (selected.second),
                shape = RoundedCornerShape(50.dp),
                onValueChange = { },
                trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languages.forEach {
                    DropdownMenuItem(
                        onClick = {
                            onSelectionChanged(it)
                            selected = it
                            expanded = false
                        },
                        text = {
                            Text(
                                text = (it.second),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .wrapContentWidth()
                                    .align(Alignment.Start)
                            )
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )
    }
}

@Composable
fun CardTranslationText(
    modifier: Modifier = Modifier,
    contentText: String,
    languageSelected: Pair<String, String> = "Spanish" to "Spanish",
    languages: List<Pair<String, String>> = SUPPORTED_LANGUAGES,
    onSelectionChanged: (selection: Pair<String, String>) -> Unit = { },
    onRecordingStarts: () -> Unit = { },
    onRecordingStops: () -> Unit = { }
) {
    var isRecording by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 50.dp, horizontal = 10.dp)
                    .fillMaxWidth()
                    .height(80.dp),
                text = contentText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Row {
                LanguagePicker(
                    Modifier.padding(10.dp),
                    preselected = languageSelected,
                    languages = languages,
                    onSelectionChanged = { onSelectionChanged(it) }
                )
                Spacer(modifier = Modifier.weight(1f))
                FloatingActionButton(
                    onClick = {
                        if (isRecording) {
                            onRecordingStops()
                        } else {
                            onRecordingStarts()
                        }
                        isRecording = !isRecording
                    },
                    shape = RoundedCornerShape(50.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        painter = painterResource(if (isRecording) R.drawable.ic_stop else R.drawable.ic_microphone),
                        tint = MaterialTheme.colorScheme.surface,
                        contentDescription = stringResource(R.string.action_microphone)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PolyglootTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { paddingValues ->
                Column {
                    CardTranslationText(
                        modifier = Modifier.padding(paddingValues),
                        contentText = "Hola que tal?"
                    )
                }
            })
    }
}