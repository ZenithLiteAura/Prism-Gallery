package com.example.amoledgallery.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.amoledgallery.data.LibraryFolders
import com.example.amoledgallery.data.MediaRepository
import com.example.amoledgallery.model.BrowserDir
import com.example.amoledgallery.model.LibraryFolder
import com.example.amoledgallery.ui.design.ListRow
import com.example.amoledgallery.ui.design.ThemedChipAction
import com.example.amoledgallery.ui.design.ThemedListSection
import com.example.amoledgallery.ui.design.ThemedSegmentedControl
import com.example.amoledgallery.ui.i18n.strings
import com.example.amoledgallery.ui.theme.LocalAppTheme
import com.example.amoledgallery.util.StorageAccess
import java.io.File

/**
 * 「设置 → 通用」里的入口行：显示当前选了几个文件夹，点开 [SettingsSection.FOLDERS] 那一页。
 * 四种设计的设置页共用，样式由 [ThemedListSection] 按设计自适应。
 */
@Composable
fun LibraryFoldersEntryRow(modifier: Modifier = Modifier) {
    val summary = if (LibraryFolders.onlySelected) {
        strings.foldersSelectedSummary(LibraryFolders.count())
    } else {
        strings.foldersReadAll
    }
    ThemedListSection(
        modifier = modifier,
        rows = listOf(
            ListRow(
                title = strings.libraryFolders,
                icon = Icons.Rounded.Folder,
                value = summary,
                onClick = { SettingsRequest.open(SettingsSection.FOLDERS) }
            )
        )
    )
}

/**
 * 「设置 → 读取的文件夹」的内容，两种模式：
 * - 已识别：系统媒体库认识、含照片/视频的文件夹，勾选后按媒体库过滤（zh3 的行为）。
 * - 浏览目录：从存储根按字母序浏览真实目录（含 "." 开头的隐藏目录），自选目录直接从磁盘读，
 *   需要「所有文件访问」权限。
 *
 * 四套设计共用这一份内容，外层由各自的设置页给标题和返回键。
 */
@Composable
fun LibraryFoldersContent(
    loadFolders: suspend () -> List<LibraryFolder>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val palette = LocalAppTheme.current.palette
    val repository = remember(context) { MediaRepository(context) }

    var mode by rememberSaveable { mutableIntStateOf(0) }

    // ---- 模式一：已识别的文件夹 ----
    var folders by remember { mutableStateOf<List<LibraryFolder>?>(null) }
    var failed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val result = runCatching { loadFolders() }
        folders = result.getOrElse {
            failed = true
            emptyList()
        }
    }

    // ---- 模式二：浏览目录 ----
    var currentPath by rememberSaveable { mutableStateOf<String?>(null) }
    var accessGranted by remember { mutableStateOf(StorageAccess.isGranted(context)) }
    var dirs by remember { mutableStateOf<List<BrowserDir>?>(null) }
    // 从系统「所有文件访问」页返回后重新判断权限。
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) accessGranted = StorageAccess.isGranted(context)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        accessGranted = StorageAccess.isGranted(context)
    }
    val legacyPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        accessGranted = StorageAccess.isGranted(context)
    }
    val requestAccess: () -> Unit = {
        val settings = StorageAccess.settingsIntent(context)
        if (settings != null) {
            settingsLauncher.launch(settings)
        } else {
            StorageAccess.legacyRuntimePermission()?.let { legacyPermissionLauncher.launch(it) }
        }
        Unit
    }

    LaunchedEffect(mode, accessGranted, currentPath) {
        if (mode == 1 && accessGranted) {
            dirs = null
            dirs = runCatching { repository.listDirectories(currentPath) }.getOrDefault(emptyList())
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ThemedSegmentedControl(
            options = listOf(0, 1),
            selected = mode,
            label = { if (it == 0) strings.foldersModeKnown else strings.foldersModeBrowse },
            onSelect = { mode = it }
        )

        if (mode == 0) {
            KnownFolders(
                folders = folders,
                failed = failed,
                onToggle = { LibraryFolders.toggle(it, context) }
            )
        } else {
            BrowseFolders(
                currentPath = currentPath,
                dirs = dirs,
                accessGranted = accessGranted,
                requestAccess = requestAccess,
                onEnter = { currentPath = it },
                onUp = {
                    // 走到存储卷根之后再上一级就是"存储列表"（内部存储 / SD 卡），而不是 /storage。
                    val parent = currentPath?.let { File(it).parentFile?.absolutePath }
                    currentPath = when (parent) {
                        null, "/storage", "/storage/emulated" -> null
                        else -> parent
                    }
                }
            )
        }
    }
}

@Composable
private fun KnownFolders(
    folders: List<LibraryFolder>?,
    failed: Boolean,
    onToggle: (String) -> Unit
) {
    val context = LocalContext.current
    val palette = LocalAppTheme.current.palette
    val onlySelected = LibraryFolders.onlySelected
    val selectedPaths = LibraryFolders.paths
    val allPaths = folders.orEmpty().map { it.relativePath }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ThemedSegmentedControl(
            options = listOf(true, false),
            selected = onlySelected,
            label = { if (it) strings.foldersOnlySelected else strings.foldersReadAll },
            onSelect = { LibraryFolders.setOnlySelected(it, context) }
        )
        Text(
            text = if (onlySelected) strings.foldersSelectedSummary(LibraryFolders.count()) else strings.foldersReadAll,
            color = palette.accent,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        when {
            folders == null -> LoadingBox(palette.accent)
            failed -> Hint(strings.foldersLoadFailed)
            folders.isEmpty() -> Hint(strings.foldersEmpty)
            else -> {
                ThemedListSection(
                    rows = folders.map { folder ->
                        val selected = folder.relativePath in selectedPaths
                        ListRow(
                            title = folder.name,
                            subtitle = (if (folder.isSdCard) "SD · " else "") + folder.relativePath,
                            icon = if (selected) Icons.Rounded.FolderOpen else Icons.Rounded.Folder,
                            iconColor = if (selected) palette.accent else palette.textMuted,
                            value = (if (selected) "✓ " else "") + strings.items(folder.count),
                            showChevron = false,
                            onClick = { onToggle(folder.relativePath) }
                        )
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemedChipAction(strings.foldersSelectAll, palette.accent, onClick = {
                        LibraryFolders.selectAll(allPaths, context)
                    })
                    ThemedChipAction(strings.foldersClearAll, palette.textMuted, onClick = {
                        LibraryFolders.clear(context)
                    })
                }
            }
        }

        Text(
            text = strings.libraryFoldersFooter,
            color = palette.textMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun BrowseFolders(
    currentPath: String?,
    dirs: List<BrowserDir>?,
    accessGranted: Boolean,
    requestAccess: () -> Unit,
    onEnter: (String) -> Unit,
    onUp: () -> Unit
) {
    val context = LocalContext.current
    val palette = LocalAppTheme.current.palette
    val localPaths = LibraryFolders.localPaths

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (!accessGranted) {
            // 没有「所有文件访问」时，目录树是读不全的：先引导去系统设置授权。
            Text(
                text = strings.foldersAllFilesTitle,
                color = palette.onBackground,
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = strings.foldersAllFilesMessage,
                color = palette.textMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                ThemedChipAction(strings.foldersAllFilesGrant, palette.accent, onClick = requestAccess)
            }
        }

        if (currentPath != null) {
            Text(
                text = currentPath,
                color = palette.accent,
                fontSize = 13.sp,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ThemedChipAction(strings.foldersBrowseUp, palette.textMuted, onClick = onUp)
                val added = LibraryFolders.isLocalSelected(currentPath)
                ThemedChipAction(
                    if (added) strings.foldersRemoveCurrent else strings.foldersAddCurrent,
                    palette.accent,
                    onClick = { LibraryFolders.toggleLocalDir(currentPath, context) }
                )
            }
        }

        // 已添加的目录：显示出来随时可以移除。
        if (localPaths.isNotEmpty()) {
            Text(
                text = strings.foldersAddedDirs,
                color = palette.textMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            ThemedListSection(
                rows = localPaths.sorted().map { path ->
                    ListRow(
                        title = File(path).name.ifBlank { path },
                        subtitle = path,
                        icon = Icons.Rounded.FolderOpen,
                        iconColor = palette.accent,
                        value = strings.foldersRemoveCurrent,
                        showChevron = false,
                        onClick = { LibraryFolders.removeLocalDir(path, context) }
                    )
                }
            )
        } else {
            Hint(strings.foldersNoAddedDirs)
        }

        when {
            !accessGranted -> Hint(strings.foldersAllFilesMissing)
            dirs == null -> LoadingBox(palette.accent)
            dirs.isEmpty() -> Hint(strings.foldersEmpty)
            else -> ThemedListSection(
                rows = dirs.map { dir ->
                    ListRow(
                        // 名字原样显示，点开头的目录自然排在前面。
                        title = dir.name,
                        // 存储卷根显示路径（"0 项" 没意义），普通目录显示其中的媒体数。
                        subtitle = if (dir.isRoot) dir.path else strings.items(dir.mediaCount),
                        icon = Icons.Rounded.Folder,
                        iconColor = if (dir.removable) palette.accent else palette.textMuted,
                        showChevron = true,
                        onClick = { onEnter(dir.path) }
                    )
                }
            )
        }

        Text(
            text = strings.foldersLocalHint,
            color = palette.textMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun LoadingBox(accent: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = accent)
    }
}

@Composable
private fun Hint(text: String) {
    val palette = LocalAppTheme.current.palette
    Text(
        text = text,
        color = palette.textMuted,
        fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}