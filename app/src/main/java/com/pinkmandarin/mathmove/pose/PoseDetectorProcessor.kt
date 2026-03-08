package com.pinkmandarin.mathmove.pose

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.pinkmandarin.mathmove.domain.model.PoseAction
import kotlinx.coroutines.tasks.await

/**
 * Wrapper around ML Kit PoseDetector.
 * Processes images and returns pose landmarks for classification.
 */
class PoseDetectorProcessor {

    companion object {
        private const val TAG = "PoseDetectorProcessor"
    }

    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()

    private val poseDetector: PoseDetector = PoseDetection.getClient(options)
    private val poseClassifier = PoseClassifier()

    /**
     * Process an InputImage and return the detected PoseAction.
     */
    suspend fun processImage(inputImage: InputImage): PoseAction {
        return try {
            val pose = poseDetector.process(inputImage).await()
            val landmarks = pose.allPoseLandmarks
            if (landmarks.isNotEmpty()) {
                poseClassifier.classify(landmarks)
            } else {
                PoseAction.NONE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Pose detection failed", e)
            PoseAction.NONE
        }
    }

    /**
     * Process an InputImage and return raw pose landmarks.
     */
    suspend fun detectPose(inputImage: InputImage): Pose? {
        return try {
            poseDetector.process(inputImage).await()
        } catch (e: Exception) {
            Log.e(TAG, "Pose detection failed", e)
            null
        }
    }

    /**
     * Get all landmark positions from a Pose result.
     * Returns a map of landmark type to (x, y) coordinate pairs.
     */
    fun getLandmarkPositions(pose: Pose): Map<Int, Pair<Float, Float>> {
        val positions = mutableMapOf<Int, Pair<Float, Float>>()
        for (landmark in pose.allPoseLandmarks) {
            positions[landmark.landmarkType] = Pair(landmark.position.x, landmark.position.y)
        }
        return positions
    }

    /**
     * Release resources held by the pose detector.
     */
    fun close() {
        poseDetector.close()
    }
}
