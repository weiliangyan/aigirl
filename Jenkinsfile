pipeline {
    agent any
    
    environment {
        ANDROID_HOME = '/usr/local/android-sdk'
        KEYSTORE_FILE = credentials('keystore-file')
        KEYSTORE_PASSWORD = credentials('keystore-password')
        KEY_ALIAS = credentials('key-alias')
        KEY_PASSWORD = credentials('key-password')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh './gradlew clean assembleRelease'
            }
        }
        
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'app/build/outputs/apk/release/*.apk', fingerprint: true
            }
        }
        
        stage('Deploy') {
            steps {
                // 这里可以添加部署步骤，比如上传到 Firebase 或其他平台
                echo '部署完成'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
} 