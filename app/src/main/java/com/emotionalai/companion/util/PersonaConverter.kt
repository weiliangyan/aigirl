package com.emotionalai.companion.util

import com.emotionalai.companion.model.*
import org.json.JSONObject
import org.json.JSONArray

class PersonaConverter {
    companion object {
        fun markdownToJson(markdown: String): JSONObject {
            val json = JSONObject()
            val base = JSONObject()
            val personality = JSONObject()
            val behavior = JSONObject()
            val interaction = JSONObject()
            val preferences = JSONObject()

            // 解析基本信息
            val nameRegex = "姓名：(.*?)\\n".toRegex()
            val ageRegex = "年龄：(\\d+)\\n".toRegex()
            val nameMatch = nameRegex.find(markdown)
            val ageMatch = ageRegex.find(markdown)
            
            if (nameMatch != null) base.put("name", nameMatch.groupValues[1])
            if (ageMatch != null) base.put("age", ageMatch.groupValues[1].toInt())

            // 解析性格特点
            val externalTraits = mutableListOf<String>()
            val internalTraits = mutableListOf<String>()
            
            var isExternal = false
            var isInternal = false
            
            markdown.lines().forEach { line ->
                when {
                    line.contains("外在表现：") -> isExternal = true
                    line.contains("内在特质：") -> {
                        isExternal = false
                        isInternal = true
                    }
                    line.startsWith("- ") && isExternal -> externalTraits.add(line.substring(2))
                    line.startsWith("- ") && isInternal -> internalTraits.add(line.substring(2))
                }
            }

            personality.put("external_traits", JSONArray(externalTraits))
            personality.put("internal_traits", JSONArray(internalTraits))

            // 解析互动风格
            val replyLengthRegex = "回复长度：(.*?)\\n".toRegex()
            val toneRegex = "语气特点：(.*?)\\n".toRegex()
            val replyLengthMatch = replyLengthRegex.find(markdown)
            val toneMatch = toneRegex.find(markdown)

            if (replyLengthMatch != null) {
                val length = replyLengthMatch.groupValues[1]
                behavior.put("reply_length", length)
            }
            if (toneMatch != null) {
                behavior.put("tone", toneMatch.groupValues[1])
            }

            // 解析喜好
            val likes = mutableListOf<String>()
            val dislikes = mutableListOf<String>()
            
            var isLikes = false
            var isDislikes = false
            
            markdown.lines().forEach { line ->
                when {
                    line.contains("热爱：") -> {
                        isLikes = true
                        isDislikes = false
                    }
                    line.contains("讨厌：") -> {
                        isLikes = false
                        isDislikes = true
                    }
                    line.startsWith("- ") && isLikes -> likes.add(line.substring(2))
                    line.startsWith("- ") && isDislikes -> dislikes.add(line.substring(2))
                }
            }

            preferences.put("likes", JSONArray(likes))
            preferences.put("dislikes", JSONArray(dislikes))

            // 组装JSON
            json.put("base", base)
            json.put("personality", personality)
            json.put("behavior", behavior)
            json.put("preferences", preferences)

            return json
        }

        fun jsonToMarkdown(json: JSONObject): String {
            val base = json.getJSONObject("base")
            val personality = json.getJSONObject("personality")
            val behavior = json.getJSONObject("behavior")
            val preferences = json.getJSONObject("preferences")

            return buildString {
                appendLine("# 角色设定")
                appendLine()
                appendLine("## 基础信息")
                appendLine("姓名：${base.getString("name")}")
                appendLine("年龄：${base.getInt("age")}")
                appendLine()
                appendLine("## 性格特点")
                appendLine("外在表现：")
                personality.getJSONArray("external_traits").forEach { trait ->
                    appendLine("- $trait")
                }
                appendLine()
                appendLine("内在特质：")
                personality.getJSONArray("internal_traits").forEach { trait ->
                    appendLine("- $trait")
                }
                appendLine()
                appendLine("## 互动风格")
                appendLine("回复特点：")
                appendLine("- 回复长度：${behavior.getString("reply_length")}")
                appendLine("- 语气特点：${behavior.getString("tone")}")
                appendLine()
                appendLine("## 喜好清单")
                appendLine("热爱：")
                preferences.getJSONArray("likes").forEach { like ->
                    appendLine("- $like")
                }
                appendLine()
                appendLine("讨厌：")
                preferences.getJSONArray("dislikes").forEach { dislike ->
                    appendLine("- $dislike")
                }
            }
        }
    }
} 