package com.bratai.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class MemoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("brat_ai_memory", Context.MODE_PRIVATE)

    fun saveMessages(messages: List<ChatMessage>) {
        val array = JSONArray()
        messages.takeLast(100).forEach { message ->
            array.put(
                JSONObject()
                    .put("text", message.text)
                    .put("fromUser", message.fromUser)
            )
        }
        prefs.edit().putString("messages", array.toString()).apply()
    }

    fun loadMessages(): List<ChatMessage> {
        val raw = prefs.getString("messages", null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            List(array.length()) { index ->
                val obj = array.getJSONObject(index)
                ChatMessage(
                    text = obj.getString("text"),
                    fromUser = obj.getBoolean("fromUser")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
