package com.pinkmandarin.mathmove.presentation.game

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.domain.model.PoseAction
import kotlinx.coroutines.delay

@Composable
fun PoseGuide(
    targetPose: PoseAction,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    // Animate between base pose and target pose
    var showTargetPose by remember { mutableStateOf(false) }

    LaunchedEffect(targetPose) {
        showTargetPose = false
        while (true) {
            delay(800)
            showTargetPose = true
            delay(1200)
            showTargetPose = false
        }
    }

    val currentPose = if (showTargetPose && targetPose != PoseAction.NONE) {
        targetPose
    } else {
        PoseAction.NONE
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = currentPose,
            animationSpec = tween(400),
            label = "poseGuide"
        ) { pose ->
            val imageRes = when (pose) {
                PoseAction.LEFT_HAND_UP,
                PoseAction.RIGHT_HAND_UP -> R.drawable.neon_human_hand_up
                PoseAction.LEFT_FOOT_UP,
                PoseAction.RIGHT_FOOT_UP -> R.drawable.neon_human_foot_up
                PoseAction.NONE -> R.drawable.neon_human_base
            }

            // Mirror for left-side actions:
            // hand_up image shows RIGHT hand raised → mirror for LEFT hand
            // foot_up image shows LEFT foot raised → mirror for RIGHT foot
            val mirrorX = when (pose) {
                PoseAction.LEFT_HAND_UP -> true
                PoseAction.LEFT_FOOT_UP -> true
                else -> false
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = getPoseDescription(pose),
                modifier = Modifier
                    .size(size)
                    .graphicsLayer(scaleX = if (mirrorX) -1f else 1f)
            )
        }
    }
}

private fun getPoseDescription(pose: PoseAction): String {
    return when (pose) {
        PoseAction.LEFT_HAND_UP -> "Raise left hand"
        PoseAction.RIGHT_HAND_UP -> "Raise right hand"
        PoseAction.LEFT_FOOT_UP -> "Raise left foot"
        PoseAction.RIGHT_FOOT_UP -> "Raise right foot"
        PoseAction.NONE -> "Stand still"
    }
}
