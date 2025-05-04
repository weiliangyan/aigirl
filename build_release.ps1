# Set environment variables
$env:KEYSTORE_PASSWORD = "123456"
$env:KEY_ALIAS = "emotional_ai"
$env:KEY_PASSWORD = "123456"

# Clean project
.\gradlew.bat clean

# Build release version
.\gradlew.bat assembleRelease

# Check build result
if ($LASTEXITCODE -eq 0) {
    Write-Host "Build successful! APK file is located at: app/build/outputs/apk/release/app-release.apk"
} else {
    Write-Host "Build failed, please check error messages"
} 