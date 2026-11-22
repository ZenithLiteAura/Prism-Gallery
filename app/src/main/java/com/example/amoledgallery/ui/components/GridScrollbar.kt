package com.example.amoledgallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.amoledgallery.ui.theme.LocalAppTheme
import kotlinx.coroutines.launch

/**
 * A draggable scrollbar drawn over a LazyVerticalGrid.
 *
 * It appears on the right edge whenever the content is taller than the viewport: dragging (or tapping)
 * anywhere along it jumps the grid to that part of the list, so a folder with hundreds of photos can be
 * crossed in one move instead of endless flicking.
 *
 * Call it inside the same Box as the grid, e.g.
 * `GridScrollbar(state, Modifier.align(Alignment.CenterEnd).padding(...))`.
 */
@Composable
fun BoxScope.GridScrollbar(
    state: LazyGridState,
    modifier: Modifier = Modifier
) {
    val info = state.layoutInfo
    val total = info.totalItemsCount
    val visible = info.visibleItemsInfo.size
    // Nothing to scroll (empty grid, or everything already on screen): no bar.
    if (total <= 0 || total <= visible) return

    val palette = LocalAppTheme.current.palette
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    var trackHeightPx by remember { mutableIntStateOf(0) }
    var dragging by remember { mutableStateOf(false) }
    var lastIndex by remember { mutableIntStateOf(-1) }
    val maxIndex = (total - 1).coerceAtLeast(1)

    val fraction = (state.firstVisibleItemIndex.toFloat() / maxIndex).coerceIn(0f, 1f)
    val thumbFraction = (visible.toFloat() / total).coerceIn(0.07f, 0.85f)

    fun scrollToFraction(yFraction: Float) {
        if (trackHeightPx <= 0) return
        val target = (yFraction.coerceIn(0f, 1f) * maxIndex).toInt().coerceIn(0, maxIndex)
        if (target == lastIndex) return
        lastIndex = target
        scope.launch { state.scrollToItem(target) }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(22.dp)
            .onSizeChanged { trackHeightPx = it.height }
            .pointerInput(total, trackHeightPx) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        dragging = true
                        scrollToFraction(offset.y / trackHeightPx.toFloat())
                    },
                    onDragEnd = { dragging = false; lastIndex = -1 },
                    onDragCancel = { dragging = false; lastIndex = -1 }
                ) { change, _ ->
                    change.consume()
                    scrollToFraction(change.position.y / trackHeightPx.toFloat())
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        // Faint track, so the bar is findable without shouting.
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(palette.onBackground.copy(alpha = 0.08f))
        )
        val trackHeightDp = with(density) { trackHeightPx.toDp() }
        val thumbHeightDp = trackHeightDp * thumbFraction
        val maxOffsetDp = (trackHeightDp - thumbHeightDp).coerceAtLeast(0.dp)
        Box(
            modifier = Modifier
                .offset(y = maxOffsetDp * fraction)
                .width(if (dragging) 7.dp else 5.dp)
                .height(thumbHeightDp.coerceAtLeast(36.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(palette.onBackground.copy(alpha = if (dragging) 0.55f else 0.32f))
        )
    }
}