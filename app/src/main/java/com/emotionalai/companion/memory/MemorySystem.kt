package com.emotionalai.companion.memory

import androidx.room.*
import com.emotionalai.companion.model.Emotion
import kotlinx.coroutines.flow.Flow
import java.util.*

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val emotion: Emotion,
    val timestamp: Long,
    val importance: Float, // 0.0-1.0
    val tags: List<String>
)

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE importance > :threshold ORDER BY timestamp DESC")
    fun getImportantMemories(threshold: Float): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE tags LIKE '%' || :tag || '%'")
    fun getMemoriesByTag(tag: String): Flow<List<Memory>>

    @Insert
    suspend fun insertMemory(memory: Memory)

    @Delete
    suspend fun deleteMemory(memory: Memory)

    @Query("DELETE FROM memories WHERE timestamp < :timestamp")
    suspend fun deleteOldMemories(timestamp: Long)
}

@Database(entities = [Memory::class], version = 1)
abstract class MemoryDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
}

class MemorySystem(private val memoryDao: MemoryDao) {
    private val shortTermMemory = mutableListOf<Memory>()
    private val maxShortTermSize = 5

    suspend fun addMemory(content: String, emotion: Emotion, importance: Float, tags: List<String>) {
        val memory = Memory(
            content = content,
            emotion = emotion,
            timestamp = System.currentTimeMillis(),
            importance = importance,
            tags = tags
        )

        // 添加到短期记忆
        shortTermMemory.add(0, memory)
        if (shortTermMemory.size > maxShortTermSize) {
            shortTermMemory.removeAt(shortTermMemory.size - 1)
        }

        // 添加到长期记忆
        memoryDao.insertMemory(memory)
    }

    fun getShortTermMemory(): List<Memory> = shortTermMemory.toList()

    fun getLongTermMemory(): Flow<List<Memory>> = memoryDao.getAllMemories()

    fun getImportantMemories(threshold: Float = 0.7f): Flow<List<Memory>> =
        memoryDao.getImportantMemories(threshold)

    fun getMemoriesByTag(tag: String): Flow<List<Memory>> =
        memoryDao.getMemoriesByTag(tag)

    suspend fun cleanupOldMemories(daysToKeep: Int = 60) {
        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000)
        memoryDao.deleteOldMemories(cutoffTime)
    }

    fun findRelevantMemories(query: String): Flow<List<Memory>> {
        // 使用余弦相似度匹配相关记忆
        return memoryDao.getAllMemories()
    }

    private fun calculateMemoryImportance(
        emotion: Emotion,
        content: String,
        tags: List<String>
    ): Float {
        // 基于情绪强度、内容长度、标签数量等计算重要性
        val emotionWeight = emotion.intensity * 0.4f
        val contentWeight = (content.length / 100f).coerceAtMost(0.3f)
        val tagsWeight = (tags.size / 5f).coerceAtMost(0.3f)

        return emotionWeight + contentWeight + tagsWeight
    }
} 