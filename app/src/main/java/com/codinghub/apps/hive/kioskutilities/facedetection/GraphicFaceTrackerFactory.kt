package com.codinghub.apps.hive.kioskutilities.facedetection

import com.codinghub.apps.hive.kioskutilities.camera.GraphicOverlay
import com.codinghub.apps.hive.ui.facerecognition.FaceRecognitionActivity
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face

class GraphicFaceTrackerFactory(internal var overlay: GraphicOverlay, var faceRecognitionActivity: FaceRecognitionActivity) : MultiProcessor.Factory<Face> {

    override fun create(face: Face): Tracker<Face> {
        return GraphicFaceTracker(overlay, faceRecognitionActivity, faceRecognitionActivity)
    }
}