package com.pinkmandarin.mathmove.pose

import com.google.mlkit.vision.pose.PoseLandmark
import com.pinkmandarin.mathmove.domain.model.PoseAction
import com.pinkmandarin.mathmove.util.Constants

/**
 * Classifies pose landmarks into PoseAction.
 *
 * Detection rules:
 * - LEFT_HAND_UP: left wrist Y < left shoulder Y - threshold
 * - RIGHT_HAND_UP: right wrist Y < right shoulder Y - threshold
 * - LEFT_FOOT_UP: left ankle Y significantly higher than left hip Y - threshold
 * - RIGHT_FOOT_UP: right ankle Y significantly higher than right hip Y - threshold
 * - NONE: no clear action detected
 *
 * Priority: hands > feet if multiple detected
 */
class PoseClassifier {

    /**
     * Classify a list of pose landmarks into a PoseAction.
     * In image coordinates, Y increases downward, so "up" means lower Y values.
     */
    fun classify(landmarks: List<PoseLandmark>): PoseAction {
        val landmarkMap = landmarks.associateBy { it.landmarkType }

        val leftShoulder = landmarkMap[PoseLandmark.LEFT_SHOULDER]
        val rightShoulder = landmarkMap[PoseLandmark.RIGHT_SHOULDER]
        val leftWrist = landmarkMap[PoseLandmark.LEFT_WRIST]
        val rightWrist = landmarkMap[PoseLandmark.RIGHT_WRIST]
        val leftHip = landmarkMap[PoseLandmark.LEFT_HIP]
        val rightHip = landmarkMap[PoseLandmark.RIGHT_HIP]
        val leftAnkle = landmarkMap[PoseLandmark.LEFT_ANKLE]
        val rightAnkle = landmarkMap[PoseLandmark.RIGHT_ANKLE]

        // Check confidence thresholds
        val minConfidence = Constants.POSE_CONFIDENCE_THRESHOLD

        // Detect hand raises (higher priority)
        val isLeftHandUp = isLandmarkAbove(
            moving = leftWrist,
            reference = leftShoulder,
            threshold = Constants.HAND_RAISE_Y_THRESHOLD,
            minConfidence = minConfidence
        )

        val isRightHandUp = isLandmarkAbove(
            moving = rightWrist,
            reference = rightShoulder,
            threshold = Constants.HAND_RAISE_Y_THRESHOLD,
            minConfidence = minConfidence
        )

        // Detect foot raises (lower priority)
        val isLeftFootUp = isFootRaised(
            ankle = leftAnkle,
            hip = leftHip,
            otherAnkle = rightAnkle,
            threshold = Constants.FOOT_RAISE_Y_THRESHOLD,
            minConfidence = minConfidence
        )

        val isRightFootUp = isFootRaised(
            ankle = rightAnkle,
            hip = rightHip,
            otherAnkle = leftAnkle,
            threshold = Constants.FOOT_RAISE_Y_THRESHOLD,
            minConfidence = minConfidence
        )

        // Priority: hands > feet, left > right if both detected
        return when {
            isLeftHandUp -> PoseAction.LEFT_HAND_UP
            isRightHandUp -> PoseAction.RIGHT_HAND_UP
            isLeftFootUp -> PoseAction.LEFT_FOOT_UP
            isRightFootUp -> PoseAction.RIGHT_FOOT_UP
            else -> PoseAction.NONE
        }
    }

    /**
     * Check if a landmark (e.g., wrist) is above another (e.g., shoulder) by a threshold.
     * In image coordinates, "above" means smaller Y value.
     */
    private fun isLandmarkAbove(
        moving: PoseLandmark?,
        reference: PoseLandmark?,
        threshold: Float,
        minConfidence: Float
    ): Boolean {
        if (moving == null || reference == null) return false
        if (moving.inFrameLikelihood < minConfidence || reference.inFrameLikelihood < minConfidence) return false

        // Y decreases going up in image coordinates
        return moving.position.y < (reference.position.y - threshold)
    }

    /**
     * Check if a foot (ankle) is raised.
     * Compares the ankle position to the hip and optionally to the other ankle
     * to determine if the foot is lifted.
     */
    private fun isFootRaised(
        ankle: PoseLandmark?,
        hip: PoseLandmark?,
        otherAnkle: PoseLandmark?,
        threshold: Float,
        minConfidence: Float
    ): Boolean {
        if (ankle == null || hip == null) return false
        if (ankle.inFrameLikelihood < minConfidence || hip.inFrameLikelihood < minConfidence) return false

        // Method 1: Ankle is significantly higher than expected standing position
        // The ankle should be well above the hip level minus threshold
        val hipToAnkleExpected = hip.position.y + 200f // Expected standing ankle position below hip
        val isRaisedAbsolute = ankle.position.y < (hipToAnkleExpected - threshold)

        // Method 2: Compare with the other ankle - one should be significantly higher
        val isRaisedRelative = if (otherAnkle != null && otherAnkle.inFrameLikelihood >= minConfidence) {
            (otherAnkle.position.y - ankle.position.y) > threshold
        } else {
            false
        }

        return isRaisedAbsolute || isRaisedRelative
    }
}
