package com.team1678.frc2024.subsystems.vision;

import com.team1678.frc2024.Robot;
import com.team1678.frc2024.RobotState;
import com.team1678.frc2024.RobotState.VisionUpdate;
import com.team1678.lib.Util;
import com.team1678.lib.logger.LogUtil;
import com.team254.lib.geometry.Pose2d;
import com.team254.lib.geometry.Translation2d;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.List;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

public class PhotonVisionDevice extends VisionDevice {
	private static final double std_dev_multiplier = 1.0;

	private final CameraConstants m_constants;
	private final PhotonCamera m_camera;
	private final PhotonPoseEstimator m_poseEstimator;

	private boolean isConnected = false;
	private double latestTimestamp = 0;

	public PhotonVisionDevice(CameraConstants constants) {
		m_constants = constants;
		m_camera = new PhotonCamera(m_constants.cameraName);
		m_poseEstimator = new PhotonPoseEstimator(
				AprilTagFields.k2024Crescendo.loadAprilTagLayoutField(),
				PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
				m_camera,
				m_constants.robotToCamera);
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
			double timestamp = estimation.timestampSeconds - VisionDeviceManager.getTimestampOffset();
			Pose3d fieldToRobot = estimation.estimatedPose;
			Pose2d robotPose2d = Util.to254Pose(fieldToRobot.toPose2d());

			List<PhotonTrackedTarget> targets = estimation.targetsUsed;
			Pose3d[] tagPoses = (Pose3d[]) targets.stream()
					.map((target) -> {
						Transform3d cameraToTag = target.getBestCameraToTarget();
						Pose3d fieldToTag = fieldToRobot
								.transformBy(m_constants.robotToCamera)
								.transformBy(cameraToTag);
						return fieldToTag;
					})
					.toArray();

			double total_tag_dist = 0.0;
			double lowest_dist = Double.POSITIVE_INFINITY;
			for (Pose3d tagPose : tagPoses) {
				double dist = fieldToRobot.getTranslation().getDistance(tagPose.getTranslation());
				total_tag_dist += dist;
				lowest_dist = Math.min(dist, lowest_dist);
			}
			double avg_dist = total_tag_dist / tagPoses.length;

			// Estimate standard deviation of vision measurement
			double xyStdDev = std_dev_multiplier
					* (0.1)
					* ((0.01 * Math.pow(lowest_dist, 2.0)) + (0.005 * Math.pow(avg_dist, 2.0)))
					/ tagPoses.length;
			xyStdDev = Math.max(0.02, xyStdDev);

			LogUtil.recordPose3d("Vision " + m_constants.cameraName + "/Tag Poses", tagPoses);
			SmartDashboard.putNumber("Vision " + m_constants.cameraName + "/N Tags Seen", tagPoses.length);
			SmartDashboard.putNumber("Vision " + m_constants.cameraName + "/Calculated STDev", xyStdDev);
			LogUtil.recordPose2d("Vision " + m_constants.cameraName + "/Estimated Pose", robotPose2d);
			LogUtil.recordPose2d(
					"Vision " + m_constants.cameraName + "/Relevant Odometry Pose",
					RobotState.getInstance().getFieldToVehicle(timestamp));

			// Photonvision automatically handles camera to robot transformation, so return zero translation
			RobotState.getInstance()
					.addVisionUpdate(
							new VisionUpdate(timestamp, robotPose2d.getTranslation(), new Translation2d(), xyStdDev));

			double rotationDegrees = robotPose2d.getRotation().getDegrees();

			// Avoid angle wrapping issues
			if (!Robot.is_red_alliance) {
				rotationDegrees = Util.boundAngleNeg180to180Degrees(rotationDegrees);
			} else {
				rotationDegrees = Util.boundAngle0to360Degrees(rotationDegrees);
			}

			SmartDashboard.putNumber("Vision Heading/" + m_constants.cameraName, rotationDegrees);
			VisionDeviceManager.getInstance().addHeadingMeasuremant(rotationDegrees);
		});
	}

	@Override
	public void outputTelemetry() {
		SmartDashboard.putNumber("Vision " + m_constants.cameraName + "/Last Update Timestamp", latestTimestamp);
		SmartDashboard.putBoolean("Vision " + m_constants.cameraName + "/Connnected:", isConnected);
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}

	public static class CameraConstants {
		public final String cameraName;
		public final Transform3d robotToCamera;

		public CameraConstants(String cameraName, Transform3d robotToCamera) {
			this.cameraName = cameraName;
			this.robotToCamera = robotToCamera;
		}
	}
}
