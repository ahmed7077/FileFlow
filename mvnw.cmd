@echo off
setlocal

set "MAVEN_VERSION=3.9.9"
set "PROJECT_DIR=%~dp0"
set "MAVEN_HOME=%PROJECT_DIR%.mvn\apache-maven-%MAVEN_VERSION%"
set "MAVEN_ZIP=%PROJECT_DIR%.mvn\apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Downloading Apache Maven %MAVEN_VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "New-Item -ItemType Directory -Force -Path '%PROJECT_DIR%.mvn' | Out-Null; Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%'; Expand-Archive -Force '%MAVEN_ZIP%' '%PROJECT_DIR%.mvn'"
    if errorlevel 1 (
        echo Failed to download Maven. Check your internet connection and try again.
        exit /b 1
    )
)

call "%MAVEN_HOME%\bin\mvn.cmd" %*
