# Flash File Downloader ⚡
![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk) ![Status](https://img.shields.io/badge/Status-Development-yellowgreen) ![License](https://img.shields.io/badge/License-MIT-blue)

A high-performance Java download manager with parallel chunk downloading, resume support, and real-time progress tracking. CLI version currently available with JavaFX GUI planned.

## 📦 Features
**Download Capabilities**  
- 🚀 Parallel chunk downloads (like IDM)  
- ♻ Resume interrupted downloads  
- ✋ Pause/Resume functionality  
- 🔄 Automatic retries (3 attempts default)  

**Progress Tracking**  
- 📊 Real-time percentage progress  
- ⏱ Accurate ETA calculation  
- 📏 File size detection (supports >2GB files)  
- 🚦 Download speed measurement (MB/s)  
- 🔍 Automatic filename detection  

**Technical**  
- 🧵 Multi-threaded architecture  
- 🔐 HTTPS/TLS support  
- 📁 Temp file management  
- 📝 Download logging  

### 🏗️ Building
```
# Requires Java 17+
git clone https://github.com/yourusername/flash-file-downloader.git
cd flash-file-downloader
./mvnw package
java -jar target/flash-downloader.jar [URL] [SAVE_PATH]
```
