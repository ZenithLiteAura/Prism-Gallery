package com.example.amoledgallery.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.example.amoledgallery.data.MediaThumbnail

/**
 * Data model representing an Album (folder) containing media items.
 * Supports hierarchical sub-album relationships and filesystem paths.
 * Annotated with @Immutable for compose recomposition skip optimization.
 *
 * @property bucketId MediaStore BUCKET_ID uniquely identifying the folder; `dir_<hash>` for a
 *   folder that only exists because of its sub-folders
 * @property name Folder display name (BUCKET_DISPLAY_NAME)
 * @property count Number of media items directly in this folder
 * @property totalCount Number of media items in this folder and all its sub-folders
 * @property coverUri Thumbnail Uri for the album cover (for a folder without its own photos: from
 *   the newest photo somewhere below it)
 * @property coverIsVideo The cover is a video frame (only when nothing below has a photo)
 * @property isFolderOnly No media of its own, only sub-folders - renaming/deleting/moving it is
 *   not offered, since the folder is just the way to its children
 * @property isSdCard True if this album is located on an external SD card
 * @property relativePath Relative directory path (e.g. "Pictures/Vacation/")
 * @property directoryPath Absolute folder path (e.g. "/storage/emulated/0/Pictures/Vacation")
 * @property parentDirectoryPath Absolute parent folder path
 * @property subAlbumCount Number of direct sub-albums inside this album
 */
@Immutable
data class AlbumItem(
    val bucketId: String,
    val name: String,
    val count: Int,
    val coverUri: Uri? = null,
    val isSdCard: Boolean = false,
    val relativePath: String = "",
    val directoryPath: String = "",
    val parentDirectoryPath: String = "",
    val subAlbumCount: Int = 0,
    val totalCount: Int = 0,
    val coverIsVideo: Boolean = false,
    val isFolderOnly: Boolean = false,
    /** Which storage it is on; [isSdCard] is true for both SD card and USB storage. */
    val storage: com.example.amoledgallery.util.StorageKind =
        if (isSdCard) com.example.amoledgallery.util.StorageKind.SD_CARD else com.example.amoledgallery.util.StorageKind.INTERNAL
) {
    /** What the album tile shows: its own count, or the whole tree for a folder-only node. */
    val displayCount: Int
        get() = if (count > 0) count else totalCount

    val coverThumbnail: MediaThumbnail?
        get() = coverUri?.let { MediaThumbnail(it, isVideo = coverIsVideo) }
}