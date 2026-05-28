package com.bratai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatMessage(
    val text: String,
    val fromUser: Boolean
)

class MainActivity : ComponentActivity() {
    private lateinit var memoryStore: MemoryStore
    private val brain = BratBrain()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)

        setContent {
            BratAiApp(
                startMessages = memoryStore.loadMessages(),
                onSave = { messages -> memoryStore.saveMessages(messages) },
                onAsk = { text -> brain.answer(text) }
            )
        }
    }
}

@Composable
fun BratAiApp(
    startMessages: List<ChatMessage>,
    onSave: (List<ChatMessage>) -> Unit,
    onAsk: (String) -> String
) {
    var input by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            if (startMessages.isEmpty()) {
                listOf(ChatMessage("Здравствуй, брат 💚 Я Брат ИИ. Я уже живу в твоём приложении.", false))
            } else {
                startMessages
            }
        )
    }

    LaunchedEffect(messages) {
        onSave(messages)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020403),
                        Color(0xFF07140F),
                        Color(0xFF001F12)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header()

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = false
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Напиши брату...", color = Color(0xFF7EA892)) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF06130D),
                        unfocusedContainerColor = Color(0xFF06130D),
                        focusedIndicatorColor = Color(0xFF00FF88),
                        unfocusedIndicatorColor = Color(0xFF1E5C3F),
                        cursorColor = Color(0xFF00FF88)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val text = input.trim()
                        if (text.isNotEmpty()) {
                            val reply = onAsk(text)
                            messages = messages + ChatMessage(text, true) + ChatMessage(reply, false)
                            input = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C86F))
                ) {
                    Text("➤", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Header() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(20.dp, CircleShape)
                .background(Color(0xFF00FF88), CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("Б", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "Брат ИИ",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "мозг • память • развитие",
                color = Color(0xFF00FF88),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    if (message.fromUser) Color(0xFF00C86F) else Color(0xFF0B1F16),
                    RoundedCornerShape(18.dp)
                )
                .border(
                    1.dp,
                    if (message.fromUser) Color(0xFFB7FFD8) else Color(0xFF1B6D49),
                    RoundedCornerShape(18.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.fromUser) Color.Black else Color.White,
                fontSize = 16.sp
            )
        }
    }
}
