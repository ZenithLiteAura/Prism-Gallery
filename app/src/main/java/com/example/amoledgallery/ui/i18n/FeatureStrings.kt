package com.example.amoledgallery.ui.i18n

/**
 * Texts of the 1.2 features (tools, viewer extras, editor additions), kept apart from [AppStrings] so the
 * long interface does not have to grow by a hundred entries. English, German, Simplified Chinese.
 */
object F {
    private val lang: AppLanguage get() = LanguageState.language
    private fun t(english: String, german: String, chinese: String) = when (lang) {
        AppLanguage.ENGLISH -> english
        AppLanguage.GERMAN -> german
        AppLanguage.CHINESE -> chinese
    }
    private fun n(
        count: Int,
        oneEn: String,
        otherEn: String,
        oneDe: String,
        otherDe: String,
        chinese: String
    ) = "$count " + when (lang) {
        AppLanguage.ENGLISH -> if (count == 1) oneEn else otherEn
        AppLanguage.GERMAN -> if (count == 1) oneDe else otherDe
        AppLanguage.CHINESE -> chinese
    }

    // ---- Filtered counts ----------------------------------------------------------------------------
    fun imageCount(count: Int) = n(count, "image", "images", "Bild", "Bilder", "张图片")
    fun videoCount(count: Int) = n(count, "video", "videos", "Video", "Videos", "个视频")
    fun gifCount(count: Int) = n(count, "GIF", "GIFs", "GIF", "GIFs", "个 GIF")

    // ---- Albums -------------------------------------------------------------------------------------
    val invalidFolderName get() = t(
        "A name cannot contain / \\ : * ? \" < > |",
        "Ein Name darf / \\ : * ? \" < > | nicht enthalten",
        "名称中不能包含 / \\ : * ? \" < > |"
    )

    // ---- Video player -------------------------------------------------------------------------------
    val loopOn get() = t("Loop: on", "Wiederholen: an", "循环：开")
    val loopOff get() = t("Loop: off", "Wiederholen: aus", "循环：关")

    // ---- Tools hub ----------------------------------------------------------------------------------
    val tools get() = t("Tools", "Werkzeuge", "工具")
    val toolsSubtitle get() = t("Search, places, clean-up and more", "Suche, Orte, Aufräumen und mehr", "搜索、地点、清理等")
    val findSection get() = t("Find", "Finden", "查找")
    val cleanSection get() = t("Clean up", "Aufräumen", "清理")
    val createSection get() = t("Create", "Erstellen", "创建")
    val personalizeSection get() = t("Personalize", "Personalisieren", "个性化")

    // ---- Search ------------------------------------------------------------------------------------
    val search get() = t("Search", "Suche", "搜索")
    val searchHint get() = t("Name, place, date, album, video…", "Name, Ort, Datum, Album, Video…", "名称、地点、日期、相册、视频…")
    val searchEmptyTitle get() = t("Nothing found", "Nichts gefunden", "没有找到")
    val searchEmptyMessage get() = t("Try a place, a month, a year or a file name.", "Versuche einen Ort, einen Monat, ein Jahr oder einen Dateinamen.", "试试地点、月份、年份或文件名。")
    val searchStartMessage get() = t(
        "Search runs on this device only - by file name, album, place, date and type.",
        "Die Suche läuft nur auf diesem Gerät - nach Dateiname, Album, Ort, Datum und Art.",
        "搜索只在本机进行——按文件名、相册、地点、日期和类型查找。"
    )
    fun results(count: Int) = n(count, "result", "results", "Treffer", "Treffer", "条结果")

    // ---- Places --------------------------------------------------------------------------------------
    val places get() = t("Places", "Orte", "地点")
    val placesEmptyTitle get() = t("No places yet", "Noch keine Orte", "还没有地点")
    val placesEmptyMessage get() = t(
        "Photos with a location appear here, grouped by town. Place names are looked up offline.",
        "Fotos mit Standort erscheinen hier, nach Ort sortiert. Ortsnamen werden offline ermittelt.",
        "带位置的照片会按城镇分组出现在这里，地名在本地离线查出。"
    )
    val placesScanning get() = t("Reading locations…", "Standorte werden gelesen…", "正在读取位置…")
    val geoNamesCredit get() = t("Place names: GeoNames (CC BY 4.0)", "Ortsnamen: GeoNames (CC BY 4.0)", "地名来源：GeoNames（CC BY 4.0）")
    val near get() = t("near", "bei", "附近")

    // ---- On this day ---------------------------------------------------------------------------------
    val onThisDay get() = t("On this day", "An diesem Tag", "往年今日")
    val onThisDayEmptyTitle get() = t("No memories today", "Heute keine Erinnerungen", "今天没有回忆")
    val onThisDayEmptyMessage get() = t(
        "Photos taken on this date in earlier years show up here.",
        "Fotos, die an diesem Datum in früheren Jahren entstanden sind, erscheinen hier.",
        "往年今天拍的照片会出现在这里。"
    )
    fun yearsAgo(years: Int) = if (years == 1) t("1 year ago", "Vor 1 Jahr", "1 年前") else t("$years years ago", "Vor $years Jahren", "$years 年前")

    // ---- Slideshow -----------------------------------------------------------------------------------
    val slideshow get() = t("Slideshow", "Diashow", "幻灯片")
    val slideInterval get() = t("Show each photo for", "Jedes Foto zeigen für", "每张照片显示")
    val slideshowLock get() = t("Lock", "Sperren", "锁定")
    val slideshowUnlockHint get() = t("Hold 5 s to unlock", "5 s halten zum Entsperren", "长按 5 秒解锁")
    val slideshowCustom get() = t("Custom…", "Eigene…", "自定义…")
    val slideshowCustomTitle get() = t("Seconds per photo", "Sekunden pro Foto", "每张显示秒数")
    val slideshowSecondsPlaceholder get() = t("Seconds (1–600)", "Sekunden (1–600)", "秒数（1–600）")
    fun seconds(s: Int) = t("$s s", "$s s", "$s 秒")
    val loop get() = t("Loop", "Wiederholen", "循环")
    val off get() = t("off", "aus", "关")
    val start get() = t("Start", "Starten", "开始")
    val shuffle get() = t("Shuffle", "Zufällig", "随机")

    // ---- Viewer extras -------------------------------------------------------------------------------
    val editDateTime get() = t("Edit date & time", "Datum & Uhrzeit ändern", "修改日期和时间")
    val editLocation get() = t("Edit location", "Standort bearbeiten", "编辑位置")
    val removeLocation get() = t("Remove location", "Standort entfernen", "移除位置")
    val setLocation get() = t("Set location", "Standort festlegen", "设置位置")
    val searchTown get() = t("Search a town", "Ort suchen", "搜索地点")
    val locationRemoved get() = t("Location removed", "Standort entfernt", "位置已移除")
    val locationSaved get() = t("Location saved", "Standort gespeichert", "位置已保存")
    val dateSaved get() = t("Date & time saved", "Datum & Uhrzeit gespeichert", "日期和时间已保存")
    val onlyPhotos get() = t("This only works for photos (JPEG, PNG, WebP).", "Das geht nur bei Fotos (JPEG, PNG, WebP).", "只适用于照片（JPEG、PNG、WebP）。")
    val shareOptions get() = t("Share options", "Teilen-Optionen", "分享选项")
    val shareOriginal get() = t("Share original", "Original teilen", "分享原图")
    val shareWithoutLocation get() = t("Share without location", "Ohne Standort teilen", "分享时去掉位置")
    val shareAsJpeg get() = t("Share as JPEG", "Als JPEG teilen", "以 JPEG 分享")
    val shareSmaller get() = t("Share smaller (max. 2048 px)", "Kleiner teilen (max. 2048 px)", "分享缩小版（最长边 2048 px）")
    val copyToAlbum get() = t("Copy to album", "In Album kopieren", "复制到相册")
    fun copiedItems(count: Int, album: String) = t(
        "Copied ${n(count, "item", "items", "", "", "")} to '$album'",
        "${n(count, "Element", "Elemente", "Element", "Elemente", "")} nach '$album' kopiert",
        "已把 $count 项复制到“$album”"
    )
    val setAs get() = t("Use as… (contact photo, etc.)", "Verwenden als… (Kontaktbild usw.)", "用作…（联系人照片等）")
    val print get() = t("Print", "Drucken", "打印")
    val lockRotation get() = t("Lock rotation", "Drehung sperren", "锁定旋转")
    val unlockRotation get() = t("Unlock rotation", "Drehung entsperren", "解除旋转锁定")
    val saveFrame get() = t("Save current frame", "Aktuelles Bild speichern", "保存当前画面")
    val frameSaved get() = t("Frame saved to Pictures/Video captures", "Bild in Pictures/Video captures gespeichert", "画面已保存到 Pictures/Video captures")
    val playbackSpeed get() = t("Playback speed", "Wiedergabegeschwindigkeit", "播放速度")
    val mute get() = t("Mute", "Stumm", "静音")
    val unmute get() = t("Sound on", "Ton an", "打开声音")
    val motionPhoto get() = t("Motion photo", "Bewegtes Foto", "动态照片")
    val playMotion get() = t("Play motion", "Bewegung abspielen", "播放动态")
    val exportMotionVideo get() = t("Save motion as video", "Bewegung als Video speichern", "把动态存成视频")
    val videoSaved get() = t("Video saved", "Video gespeichert", "视频已保存")
    val hdrDisplay get() = t("HDR photos", "HDR-Fotos", "HDR 照片")
    val hdrDisplaySubtitle get() = t("Show HDR photos with their full brightness", "HDR-Fotos mit voller Helligkeit zeigen", "以完整亮度显示 HDR 照片")
    val failed get() = t("That didn't work", "Das hat nicht geklappt", "没有成功")

    // ---- Private album -------------------------------------------------------------------------------
    val privateAlbum get() = t("Private album", "Privates Album", "私密相册")
    val moveToPrivate get() = t("Move to private album", "Ins private Album verschieben", "移到私密相册")
    val privateSubtitle get() = t("Locked with a PIN, invisible to other apps", "Mit PIN gesperrt, für andere Apps unsichtbar", "用 PIN 锁定，其他应用看不到")
    val setPin get() = t("Set a PIN", "PIN festlegen", "设置 PIN")
    val enterPin get() = t("Enter PIN", "PIN eingeben", "输入 PIN")
    val repeatPin get() = t("Repeat PIN", "PIN wiederholen", "再输一次 PIN")
    val wrongPin get() = t("Wrong PIN", "Falsche PIN", "PIN 不正确")
    val pinsDiffer get() = t("The PINs don't match", "Die PINs stimmen nicht überein", "两次输入的 PIN 不一致")
    val pinHint get() = t("4 to 8 digits", "4 bis 8 Ziffern", "4 到 8 位数字")
    val unlockWithBiometrics get() = t("Use fingerprint or screen lock", "Fingerabdruck oder Displaysperre", "使用指纹或锁屏密码")
    val changePin get() = t("Change PIN", "PIN ändern", "修改 PIN")
    val privateEmptyTitle get() = t("Nothing private yet", "Noch nichts Privates", "还没有私密内容")
    val privateEmptyMessage get() = t(
        "Select photos and choose \"Move to private album\". They are moved into the app's own storage, where no other app can see them.",
        "Wähle Fotos aus und tippe auf \"Ins private Album verschieben\". Sie werden in den eigenen Speicher der App verschoben, wo keine andere App sie sieht.",
        "选中照片后点“移到私密相册”。它们会被搬进应用自己的存储空间，其他应用看不到。"
    )
    val restoreFromPrivate get() = t("Move back to gallery", "Zurück in die Galerie", "移回图库")
    val privateWarning get() = t(
        "Private photos are stored inside the app. Uninstalling the app or clearing its data deletes them.",
        "Private Fotos liegen in der App. Wird die App deinstalliert oder ihr Speicher gelöscht, sind sie weg.",
        "私密照片保存在应用内部。卸载应用或清除其数据，这些照片就没了。"
    )
    fun movedToPrivate(count: Int) = t(
        "${n(count, "item", "items", "", "", "")} moved to the private album",
        "${n(count, "Element", "Elemente", "Element", "Elemente", "")} ins private Album verschoben",
        "已把 $count 项移到私密相册"
    )
    fun restoredFromPrivate(count: Int) = t(
        "${n(count, "item", "items", "", "", "")} moved back to Pictures/Restored",
        "${n(count, "Element", "Elemente", "Element", "Elemente", "")} nach Pictures/Restored zurückverschoben",
        "已把 $count 项移回 Pictures/Restored"
    )
    val pullForPrivate get() = t("Pull down to open the private album", "Herunterziehen für das private Album", "下拉打开私密相册")
    val releaseForPrivate get() = t("Release to open the private album", "Loslassen für das private Album", "松开打开私密相册")

    // ---- Other albums / album extras -----------------------------------------------------------------
    val otherAlbums get() = t("Other albums", "Weitere Alben", "其他相册")
    val moveToOtherAlbums get() = t("Move to Other albums", "Zu Weitere Alben", "移到“其他相册”")
    val removeFromOtherAlbums get() = t("Back to main albums", "Zurück zu den Hauptalben", "回到主相册")
    val otherAlbumsEmpty get() = t("Albums you rarely open can be tucked away here from their menu.", "Selten genutzte Alben kannst du über ihr Menü hierher auslagern.", "不常打开的相册，可以从它的菜单里收进这里。")
    val addToHomeScreen get() = t("Add to home screen", "Zum Startbildschirm", "添加到主屏幕")
    val listView get() = t("List view", "Listenansicht", "列表视图")
    val gridView get() = t("Grid view", "Rasteransicht", "网格视图")
    val cameraOnly get() = t("Only camera photos", "Nur Kamerafotos", "只看相机照片")
    val allItems get() = t("All items", "Alle Objekte", "全部项目")
    val viewDays get() = t("Days", "Tage", "按天")
    val viewMonths get() = t("Months", "Monate", "按月")
    val viewYears get() = t("Years", "Jahre", "按年")
    val albumGroups get() = t("Album groups", "Albumgruppen", "相册分组")
    val newGroup get() = t("New group", "Neue Gruppe", "新建分组")
    val groupName get() = t("Group name", "Gruppenname", "分组名称")
    val addToGroup get() = t("Add to group", "Zu Gruppe hinzufügen", "加入分组")
    val removeFromGroup get() = t("Remove from group", "Aus Gruppe entfernen", "移出分组")
    val deleteGroup get() = t("Delete group", "Gruppe löschen", "删除分组")
    fun albumsCount(count: Int) = n(count, "album", "albums", "Album", "Alben", "个相册")

    // ---- Clean up ------------------------------------------------------------------------------------
    val similarPhotos get() = t("Similar photos", "Ähnliche Fotos", "相似照片")
    val similarSubtitle get() = t("Bursts and near-identical shots", "Serienbilder und fast gleiche Aufnahmen", "连拍和几乎一样的照片")
    val similarScanning get() = t("Comparing photos…", "Fotos werden verglichen…", "正在比对照片…")
    val similarEmptyTitle get() = t("No similar photos", "Keine ähnlichen Fotos", "没有相似照片")
    val similarEmptyMessage get() = t("Series of near-identical shots appear here so you can keep the best one.", "Reihen fast gleicher Aufnahmen erscheinen hier, damit du die beste behalten kannst.", "成串的近似照片会出现在这里，方便你留下最好的一张。")
    fun similarSet(count: Int) = t("$count similar photos", "$count ähnliche Fotos", "$count 张相似照片")
    val keepBest get() = t("Best", "Beste", "最佳")
    val cleanUp get() = t("Clean-up suggestions", "Aufräum-Vorschläge", "清理建议")
    val cleanUpSubtitle get() = t("Old screenshots, large videos, RAW files", "Alte Screenshots, große Videos, RAW-Dateien", "旧截图、大视频、RAW 文件")
    val oldScreenshots get() = t("Screenshots older than 30 days", "Screenshots älter als 30 Tage", "30 天前的截图")
    val largeVideos get() = t("Large videos (over 100 MB)", "Große Videos (über 100 MB)", "大视频（超过 100 MB）")
    val rawFiles get() = t("RAW files", "RAW-Dateien", "RAW 文件")
    val blurryOrDark get() = t("Very dark photos", "Sehr dunkle Fotos", "非常暗的照片")
    val nothingToClean get() = t("Nothing to clean up here.", "Hier gibt es nichts aufzuräumen.", "这里没有需要清理的。")
    fun selectedFrees(count: Int, size: String) = t(
        "${n(count, "item", "items", "", "", "")} selected · frees $size",
        "${n(count, "Element", "Elemente", "Element", "Elemente", "")} ausgewählt · gibt $size frei",
        "已选 $count 项 · 可释放 $size"
    )
    val moveToTrash get() = t("Move to trash", "In den Papierkorb", "移到回收站")

    // ---- Create --------------------------------------------------------------------------------------
    val collage get() = t("Collage", "Collage", "拼图")
    val collageSubtitle get() = t("2 to 6 photos in one picture", "2 bis 6 Fotos in einem Bild", "2 到 6 张照片拼成一张")
    val gif get() = t("GIF", "GIF", "GIF")
    val gifSubtitle get() = t("Animated GIF from several photos", "Animiertes GIF aus mehreren Fotos", "把多张照片做成动图")
    val pdf get() = t("PDF", "PDF", "PDF")
    val pdfSubtitle get() = t("A PDF document with one photo per page", "Ein PDF mit einem Foto pro Seite", "每页一张照片的 PDF 文档")
    fun selectPhotos(min: Int, max: Int) = t("Select $min to $max photos", "Wähle $min bis $max Fotos", "选择 $min 到 $max 张照片")
    fun selectedCount(count: Int) = t("$count selected", "$count ausgewählt", "已选 $count 张")
    val next get() = t("Next", "Weiter", "下一步")
    val layout get() = t("Layout", "Anordnung", "布局")
    val spacing get() = t("Spacing", "Abstand", "间距")
    val restoreShort get() = t("Restore", "Zurückholen", "恢复")
    fun deletePrivateTitle(n: Int) = t(
        if (n == 1) "Delete this photo for good?" else "Delete $n photos for good?",
        if (n == 1) "Dieses Foto endgültig löschen?" else "$n Fotos endgültig löschen?",
        if (n == 1) "永久删除这张照片？" else "永久删除 $n 张照片？"
    )
    val deletePrivateMessage get() = t("They are removed from the private album and cannot be brought back.", "Sie werden aus dem privaten Album gelöscht und lassen sich nicht zurückholen.", "它们会从私密相册中删除，无法找回。")
    val hdrScreenUnsupported get() = t("this screen shows no HDR photos", "Bildschirm zeigt keine HDR-Fotos", "这块屏幕显示不了 HDR 照片")
    val deleteKey get() = t("Delete", "Löschen", "删除")
    val frameColor get() = t("Frame colour", "Rahmenfarbe", "边框颜色")
    val white get() = t("White", "Weiß", "白色")
    val black get() = t("Black", "Schwarz", "黑色")
    val frameDelay get() = t("Time per frame", "Zeit pro Bild", "每帧时长")
    val creating get() = t("Creating…", "Wird erstellt…", "正在生成…")
    fun savedTo(path: String) = t("Saved to $path", "Gespeichert in $path", "已保存到 $path")
    val pageSize get() = t("Page size", "Seitenformat", "页面尺寸")
    val fitToPhoto get() = t("Photo size", "Fotogröße", "按照片尺寸")

    // ---- Personalize ---------------------------------------------------------------------------------
    val slideshowWallpaper get() = t("Slideshow wallpaper", "Diashow-Hintergrund", "幻灯片壁纸")
    val slideshowWallpaperSubtitle get() = t("Photos of an album change on your home screen", "Fotos eines Albums wechseln auf dem Startbildschirm", "让某个相册的照片在桌面轮流更换")
    val chooseAlbum get() = t("Album", "Album", "相册")
    val changeEvery get() = t("Change every", "Wechseln alle", "更换间隔")
    fun minutes(m: Int) = t("$m min", "$m Min.", "$m 分钟")
    fun hours(h: Int) = t("$h h", "$h Std.", "$h 小时")
    val apply get() = t("Set as wallpaper", "Als Hintergrund festlegen", "设为壁纸")
    val photoWidget get() = t("Photo widget", "Foto-Widget", "照片小组件")
    val photoWidgetSubtitle get() = t("Long-press your home screen > Widgets > Prism Gallery", "Startbildschirm lange drücken > Widgets > Prism Gallery", "长按桌面 > 小组件 > Prism Gallery")
    val photoWidgetHelp get() = t(
        "The widget shows a photo from your favorites (or recent photos) and changes it every hour. Tap it to open the gallery.",
        "Das Widget zeigt ein Foto aus deinen Favoriten (oder neuen Fotos) und wechselt es stündlich. Tippe darauf, um die Galerie zu öffnen.",
        "小组件会显示你收藏的照片（或最近的照片），每小时换一张。点一下可以打开图库。"
    )
    val favoritesOrRecent get() = t("Favorites (or recent photos)", "Favoriten (oder neue Fotos)", "收藏（或最近的照片）")

    // ---- Editor additions ----------------------------------------------------------------------------
    val adjust get() = t("Adjust", "Anpassen", "调整")
    val filters get() = t("Filters", "Filter", "滤镜")
    val draw get() = t("Draw", "Zeichnen", "涂画")
    val text get() = t("Text", "Text", "文字")
    val watermark get() = t("Watermark", "Wasserzeichen", "水印")
    val watermarkHint get() = t("e.g. ONLY FOR APARTMENT APPLICATION", "z. B. NUR FÜR WOHNUNGSBEWERBUNG", "例如：仅供租房申请使用")
    val brightness get() = t("Brightness", "Helligkeit", "亮度")
    val contrast get() = t("Contrast", "Kontrast", "对比度")
    val saturation get() = t("Saturation", "Sättigung", "饱和度")
    val warmth get() = t("Warmth", "Wärme", "色温")
    val vignette get() = t("Vignette", "Vignette", "暗角")
    val original get() = t("Original", "Original", "原图")
    val vivid get() = t("Vivid", "Lebhaft", "鲜艳")
    val mono get() = t("Mono", "Schwarzweiß", "黑白")
    val sepia get() = t("Sepia", "Sepia", "棕褐")
    val cool get() = t("Cool", "Kühl", "冷色")
    val warm get() = t("Warm", "Warm", "暖色")
    val fade get() = t("Fade", "Verblasst", "褪色")
    val noir get() = t("Noir", "Noir", "黑白电影")
    val copyEdits get() = t("Copy edits", "Bearbeitungen kopieren", "复制编辑")
    val pasteEdits get() = t("Paste edits", "Bearbeitungen einfügen", "粘贴编辑")
    val editsCopied get() = t("Edits copied - select photos and paste them", "Bearbeitungen kopiert - wähle Fotos und füge sie ein", "编辑已复制——选中照片后粘贴即可")
    fun editsPasted(count: Int) = t(
        "Edited copies of ${n(count, "photo", "photos", "", "", "")} saved",
        "Bearbeitete Kopien von ${n(count, "Foto", "Fotos", "Foto", "Fotos", "")} gespeichert",
        "已保存 $count 张编辑后的副本"
    )
    val undo get() = t("Undo", "Rückgängig", "撤销")
    val enterText get() = t("Enter text", "Text eingeben", "输入文字")
    val size get() = t("Size", "Größe", "大小")
    val color get() = t("Color", "Farbe", "颜色")
    val trim get() = t("Trim", "Kürzen", "裁剪")
    val compressVideo get() = t("Make smaller", "Verkleinern", "压缩变小")
    val compressVideoTitle get() = t("Video size", "Videogröße", "视频大小")
    fun compressTo(p: Int) = t("${p}p", "${p}p", "${p}p")
    val keepResolution get() = t("Keep resolution", "Auflösung behalten", "保留分辨率")
}