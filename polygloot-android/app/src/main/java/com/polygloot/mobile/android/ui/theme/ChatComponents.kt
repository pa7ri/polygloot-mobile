package com.polygloot.mobile.android.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polygloot.mobile.android.ui.translator.TranslatorResult
import com.polygloot.mobile.android.ui.translator.TranslatorUser
import java.util.UUID

@Composable
fun ChatScreen(translations: List<TranslatorResult>) {
    val messages = remember { mutableStateOf(translations) }
    Column(modifier = Modifier.fillMaxWidth().height(450.dp)) {
        ChatList(messages.value)
    }
}

@Composable
fun ChatList(messages: List<TranslatorResult>) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        state = listState,
        verticalArrangement = Arrangement.Bottom
    ) {
        items(messages, key = { "${it.result}+${UUID.randomUUID()}" }) { message ->
            message.user?.let { ChatCard(user = it, text = message.result) }
        }

    }
    LaunchedEffect(messages.size) {
        if(messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
}

@Composable
fun ChatCard(user: TranslatorUser, text: String) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = if (user == TranslatorUser.SOURCE) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (user == TranslatorUser.SOURCE) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                color = if (user == TranslatorUser.SOURCE) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        listOf(
            TranslatorResult(TranslatorUser.TARGET, "Who are you?"),
            TranslatorResult(TranslatorUser.TARGET, "Hi"),
            TranslatorResult(TranslatorUser.SOURCE, "Ahoj"),
        )
    )
}
