# Prism Gallery（简体中文定制版）

一个完全离线、无广告、无账号的 Android 相册应用：Kotlin + Compose 编写，内置四套完整设计风格
（iOS 液态玻璃、三星 One UI、小米 HyperOS、Google Gemini），可自由切换。

本仓库维护的是带简体中文与一系列自用改动的版本。

## 功能

- 简体中文界面（英 / 德 / 简中三语，系统语言为 zh-CN 时默认中文）
- 四套设计风格，各自独立的布局、动画与顶栏
- 照片、视频、GIF；按日/月/年时间线；仅相机照片筛选
- 相册按真实文件夹层级展示，支持多级嵌套；子文件夹封面从子树递归取图
- 文件夹内长按拖拽自定义排序（只存应用内顺序，不改磁盘），可拖动右侧滑条快速跳转
- 读取文件夹：默认只读取手动勾选的文件夹，也可按目录树浏览并直接从磁盘读取
- 动态照片：打开即自动播放，无进度条，左右滑动照常翻页
- 全屏查看器：退出后网格回到刚才看的那张照片；幻灯片支持 1–600 秒自定义间隔与锁定
- 编辑（另存副本）、拼图 / GIF / PDF、打印、清理重复与相似照片
- 收藏、回收站、搜索、地点、往年今日、存储统计
- 幻灯片壁纸、视频/GIF 动态壁纸、照片小组件
- 无网络权限，所有数据留在设备上

## 下载

最新 APK 见 [Releases](https://github.com/ZenithLiteAura/Prism-Gallery/releases)。

- APK 使用本仓库的专用证书签名；若手机上装过同包名但不同签名的版本，需要先卸载再安装。
- 同证书的后续版本可以直接覆盖升级，数据保留。

APK 签名证书 SHA-256：

```
AE:40:6B:56:6F:30:95:8C:94:0D:D9:8F:C0:66:F5:8E:09:1E:AC:8C:81:91:22:B5:7E:52:8C:0B:5C:46:A7:2E
```

可用 `apksigner verify --print-certs app-release.apk` 核对。

## 构建

需要 JDK 17+ 与 Android SDK（compileSdk 34）：

```sh
./gradlew assembleRelease
```

产物在 `app/build/outputs/apk/release/`。要签名的 release 版，在项目根目录创建
`keystore.properties`（已被 git 忽略，切勿提交）：

```properties
storeFile=/path/to/your-release-key.jks
storePassword=...
keyAlias=...
keyPassword=...
```

## 许可证

GPL-3.0，详见 [LICENSE](LICENSE)。