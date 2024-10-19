package com.team1678.frc2024.subsystems.vision;

import com.team1678.frc2024.Constants;
import com.team1678.frc2024.subsystems.Subsystem;
import com.team1678.lib.TunableNumber;
import com.team254.lib.util.MovingAverage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.List;

public class VisionDeviceManager extends Subsystem {
	private static VisionDeviceManager mInstance;

	public static VisionDeviceManager getInstance() {
		if (mInstance == null) {
			mInstance = new VisionDeviceManager();
		}
		return mInstance;
	}

	private VisionDevice mPortCamera;
	private VisionDevice mStarboardCamera;

	private List<VisionDevice> mAllCameras;

	private static TunableNumber timestampOffset = new TunableNumber("VisionTimestampOffset", (0.1), false);

	private MovingAverage mHeadingAvg = new MovingAverage(100);
	private double mMovingAvgRead = Double.NaN;

	private static boolean disable_vision = false;

	private VisionDeviceManager() {
		mPortCamera = new PhotonVisionDevice(Constants.PhotonVisionConstants.kPortCameraSettings);
		mStarboardCamera = new PhotonVisionDevice(Constants.PhotonVisionConstants.kStarboardCameraSettings);
		mAllCameras = List.of(mPortCamera, mStarboardCamera);
	}

	@Override
	public void readPeriodicInputs() {
		mAllCameras.forEach(VisionDevice::readPeriodicInputs);
		mMovingAvgRead = mHeadingAvg.getAverage();
	}

	@Override
	public void writePeriodicOutputs() {
		mAllCameras.forEach(VisionDevice::writePeriodicOutputs);
	}

	@Override
	public void outputTelemetry() {
		mAllCameras.forEach(VisionDevice::outputTelemetry);
		SmartDashboard.putNumber("Vision heading moving avg", getMovingAverageRead());
		SmartDashboard.putBoolean("vision disabled", visionDisabled());
	}

	public double getMovingAverageRead() {
		return mMovingAvgRead;
	}

	public synchronized void addHeadingMeasuremant(double headingDegrees) {
		mHeadingAvg.addNumber(headingDegrees);
	}

	public synchronized boolean fullyConnected() {
		for (VisionDevice camera : mAllCameras) {
			if (!camera.isConnected()) return false;
		}

		return true;
	}

	public static double getTimestampOffset() {
		return timestampOffset.get();
	}

	public static boolean visionDisabled() {
		return disable_vision;
	}

	public static void setDisableVision(boolean disable) {
		disable_vision = disable;
	}
}
