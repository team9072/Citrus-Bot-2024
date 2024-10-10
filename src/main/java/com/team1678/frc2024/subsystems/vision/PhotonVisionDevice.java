package com.team1678.frc2024.subsystems.vision;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

import com.team1678.frc2024.Robot;
import com.team1678.frc2024.RobotState;
import com.team1678.frc2024.RobotState.VisionUpdate;
import com.team1678.lib.Util;
import com.team254.lib.geometry.Translation2d;

import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PhotonVisionDevice extends VisionDevice {
    private final CameraConstants m_constants;
    private final PhotonCamera m_camera;
    private final PhotonPoseEstimator m_poseEstimator;

    private boolean isConnected = false;
    private double latestTimestamp = 0;

    public PhotonVisionDevice(CameraConstants constants) {
        m_constants = constants;
        m_camera = new PhotonCamera(m_constants.cameraName);
        m_poseEstimator = new PhotonPoseEstimator(AprilTagFields.k2024Crescendo.loadAprilTagLayoutField(), PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, m_camera, m_constants.robotToCamera);
    }

    @Override
    public void readPeriodicInputs() {
        PhotonPipelineResult result = m_camera.getLatestResult();
        latestTimestamp = result.getTimestampSeconds();
        isConnected = m_camera.isConnected();

        if (VisionDeviceManager.visionDisabled()) {
            return;
        }

        // Only update if new pose was found
        m_poseEstimator.update(result).ifPresent((estimation) -> {
            Pose3d pose = estimation.estimatedPose;
            Translation2d fieldToCamera = new Translation2d(pose.getX(), pose.getY());
            double timestamp = estimation.timestampSeconds - VisionDeviceManager.getTimestampOffset();

            // Photonvision automatically handles camera to robot transformation, so return zero translation
            RobotState.getInstance().addVisionUpdate(new VisionUpdate(
                timestamp, fieldToCamera, new Translation2d(), 0.0));
            
            double rotation_degrees = Units.radiansToDegrees(pose.getRotation().getZ()) + 180.0;

			// Avoid angle wrapping issues
			if (!Robot.is_red_alliance) {
				rotation_degrees = Util.boundAngleNeg180to180Degrees(rotation_degrees);
			} else {
				rotation_degrees = Util.boundAngle0to360Degrees(rotation_degrees);
			}

			SmartDashboard.putNumber("Vision Heading/" + m_constants.cameraName, rotation_degrees);
			VisionDeviceManager.getInstance().addHeadingMeasuremant(rotation_degrees);
        });
    }

    @Override
	public void outputTelemetry() {
		SmartDashboard.putNumber("Vision " + m_constants.cameraName + "/Last Update Timestamp", latestTimestamp);
		SmartDashboard.putBoolean("Vision " + m_constants.cameraName + "/Connnected:", isConnected);
	}

    @Override
    boolean isConnected() {
        return isConnected;
    }
    
    public static class CameraConstants {
        public final String cameraName;
        public final Transform3d robotToCamera;

        CameraConstants(String cameraName, Transform3d robotToCamera) {
            this.cameraName = cameraName;
            this.robotToCamera = robotToCamera;
        }
    }
}
