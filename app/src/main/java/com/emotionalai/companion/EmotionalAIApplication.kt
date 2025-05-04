package com.emotionalai.companion

import android.app.Application
import com.emotionalai.companion.config.ConfigManager
import com.emotionalai.companion.memory.MemorySystem
import com.emotionalai.companion.repository.PersonaRepository

class EmotionalAIApplication : Application() {
    
    lateinit var configManager: ConfigManager
    lateinit var memorySystem: MemorySystem
    lateinit var personaRepository: PersonaRepository
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化配置管理器
        configManager = ConfigManager(this)
        
        // 初始化记忆系统
        memorySystem = MemorySystem(this)
        
        // 初始化角色仓库
        personaRepository = PersonaRepository(this)
    }
} 