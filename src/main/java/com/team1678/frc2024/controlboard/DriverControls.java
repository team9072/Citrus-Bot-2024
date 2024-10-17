package com.team1678.frc2024.controlboard;

import com.team1678.frc2024.FieldLayout;
import com.team1678.frc2024.Robot;
import com.team1678.frc2024.RobotState;
import com.team1678.frc2024.subsystems.Climber;
import com.team1678.frc2024.subsystems.Drive;
import com.team1678.frc2024.subsystems.Hood;
import com.team1678.frc2024.subsystems.IntakeDeploy;
import com.team1678.frc2024.subsystems.LEDs;
import com.team1678.frc2024.subsystems.Superstructure;
import com.team1678.frc2024.subsystems.vision.VisionDeviceManager;
import com.team254.lib.geometry.Pose2d;
import com.team254.lib.geometry.Rotation2d;
import com.team254.lib.geometry.Translation2d;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriverControls {

	ControlBoard mControlBoard = ControlBoard.getInstance();

	Superstructure mSuperstructure = Superstructure.getInstance();
	Climber mClimber = Climber.getInstance();
	Hood mHood = Hood.getInstance();
	Drive mDrive = Drive.getInstance();
	LEDs mLEDs = LEDs.getInstance();

	private boolean mClimbMode = false;
	private boolean top_buttons_clear = true;

	/* ONE CONTROLLER */

	public void oneControllerMode() {
		if (!mClimbMode) {
			mSuperstructure.setWantClimbMode(false);
			if (mControlBoard.getEnterClimbModeDriver()) {
				mClimbMode = true;
				top_buttons_clear = false;
				mSuperstructure.tuckState();
				if (mSuperstructure.getFeederBreak() && !mSuperstructure.getAmpBreak()) {
					mSuperstructure.holdToAmpTransition();
				} else {
					mSuperstructure.idleState();
				}
				return;
			}

			if (mControlBoard.driver.rightTrigger.isBeingPressed()) {
				mDrive.overrideHeading(true);
			} else {
				mDrive.overrideHeading(false);
			}

			// Intake
			if (mControlBoard.driver.rightBumper.wasActivated()) {
				mSuperstructure.intakeToHoldTransition();
			}

			if (mControlBoard.driver.leftBumper.wasActivated()) {
				mSuperstructure.tuckState();
			}

			// Amp sequences
			if (mControlBoard.driver.bButton.shortReleased() && !mSuperstructure.getAmpBreak()) {
				if (mControlBoard.driver.POV270.buttonActive
						|| IntakeDeploy.getInstance().getSetpoint() < IntakeDeploy.kUnjamAngle) {
					mSuperstructure.intakeToAmpTransition();
				} else {
					mSuperstructure.holdToAmpTransition();
				}
			}

			if (mControlBoard.driver.leftTrigger.wasActivated()) {
				mSuperstructure.elevatorAmpExtendTransition();
			} else if (mControlBoard.driver.leftTrigger.wasReleased()) {
				mSuperstructure.ampScoreTransition();
			}

			// Shooter
			if (mControlBoard.driver.aButton.wasActivated()) {
				mSuperstructure.setWantPrep(true);
			} else if (mControlBoard.driver.xButton.wasActivated()) {
				mSuperstructure.setWantPrep(false);
			}

			if (mControlBoard.driver.yButton.wasActivated()) {
				if (mControlBoard.driver.POV270.buttonActive
						|| IntakeDeploy.getInstance().getSetpoint() < IntakeDeploy.kUnjamAngle) {
					mSuperstructure.slowContinuousShotState();
				} else {
					mSuperstructure.fireState();
				}
			}

			if (mControlBoard.driver.POV180.wasActivated()) {
				mSuperstructure.toggleFerry();
			}

			if (mControlBoard.driver.POV0.wasActivated()) {
				mSuperstructure.exhaustState();
			} else if (mControlBoard.driver.POV0.wasReleased()) {
				mSuperstructure.idleState();
			}

			if (mControlBoard.operator.POV0.isBeingPressed()) {
				mHood.setWantJog(0.5);
			} else if (mControlBoard.operator.POV180.isBeingPressed()) {
				mHood.setWantJog(-0.5);
			}

			if (mControlBoard.driver.POV90.wasActivated()) {
				Translation2d speaker = new Translation2d(0.0, FieldLayout.kSpeakerCenter.getTranslation().y());
				// Distance from speaker face to subwoofer, robot's length / 2
				Translation2d offset = new Translation2d(Units.inchesToMeters(35.5 + (32 / 2.0)), 0.0);
				Translation2d pos = FieldLayout.handleAllianceFlip(speaker.plus(offset), Robot.is_red_alliance);

				RobotState.getInstance().reset(Timer.getFPGATimestamp(), new Pose2d(pos, new Rotation2d()));
				RobotState.getInstance().resetKalman();
				Drive.getInstance().getWheelTracker().resetPose(new Pose2d(pos, new Rotation2d()));
			}

			if (mControlBoard.operator.rightTrigger.longPressed()) {
				mSuperstructure.continuousShootState();
			}

			if (mControlBoard.operator.yButton.wasActivated()) {
				mSuperstructure.ampUnjam();
			}

			if (mControlBoard.operator.leftBumper.longPressed()) {
				VisionDeviceManager.setDisableVision(!VisionDeviceManager.visionDisabled());
			}

			if (mControlBoard.operator.aButton.longPressed()) {
				System.out.println("Homing Hood!");
				mHood.setWantHome(true);
			}
			

		} else {
			mSuperstructure.setWantClimbMode(true);
			if (!top_buttons_clear) {
				mSuperstructure.tuckState();
				if (mControlBoard.topButtonsClearDriver()) {
					top_buttons_clear = true;
				}
				return;
			}

			if (mControlBoard.getExitClimbModeDriver()) {
				mClimbMode = false;
				return;
			}

			if (mControlBoard.driver.leftBumper.wasActivated()) {
				mSuperstructure.tuckState();
			}

			if (mControlBoard.driver.aButton.wasActivated()) {
				mClimber.setSetpointMotionMagic(Climber.kRetractionHeight);
			} else if (mControlBoard.driver.yButton.wasActivated()) {
				mClimber.setSetpointMotionMagic(Climber.kExtensionHeight);
			} else if (mControlBoard.driver.bButton.wasActivated()) {
				mClimber.setSetpointMotionMagic(Climber.kPrepHeight);
			}
			if (mControlBoard.driver.xButton.wasActivated()) {
				mClimber.setSetpointMotionMagic(Climber.kPullHeight);
			}

			if (mControlBoard.driver.rightBumper.wasActivated()) {
				mSuperstructure.elevatorFullExtendTransition();
			} else if (mControlBoard.driver.leftBumper.wasActivated()) {
				mSuperstructure.idleState();
			}

			if (mControlBoard.driver.rightTrigger.wasActivated()) {
				mSuperstructure.trapScoreTransition();
			}

			if (mControlBoard.driver.POV0.wasActivated()) {
				mSuperstructure.holdToAmpTransition();
			}

			mDrive.overrideHeading(false);

			SmartDashboard.putBoolean("Climber Open Loop", mClimberJog);
		}
	}

	/* TWO CONTROLLERS */

	public void twoControllerMode() {
		if (mClimbMode) {
			climbModeControls();
		} else {
			cycleModeControls();
		}
	}

	private void cycleModeControls() {
		mSuperstructure.setWantClimbMode(false);
		if (mControlBoard.getEnterClimbModeOperator()) {
			mClimbMode = true;
			top_buttons_clear = false;
			mSuperstructure.tuckState();
			mSuperstructure.idleState();
			return;
		}

		// Intake
		if (mControlBoard.driver.rightBumper.wasActivated()) {
			mSuperstructure.intakeToHoldTransition();
		} else if (mControlBoard.driver.leftBumper.wasActivated() || mControlBoard.operator.leftBumper.wasActivated()) {
			mSuperstructure.tuckState();
		}

		// Amp sequences
		if (mControlBoard.operator.bButton.wasActivated()) {
			mSuperstructure.holdToAmpTransition();
		} else if (mControlBoard.operator.rightBumper.wasActivated()) {
			mSuperstructure.elevatorAmpExtendTransition();
		} else if (mControlBoard.operator.rightTrigger.wasActivated()) {
			mSuperstructure.ampScoreTransition();
		}

		// Shooter
		if (mControlBoard.operator.aButton.wasActivated()) {
			mSuperstructure.setWantPrep(true);
		} else if (mControlBoard.operator.xButton.wasActivated()) {
			mSuperstructure.setWantPrep(false);
		} else if (mControlBoard.operator.yButton.wasActivated()) {
			mSuperstructure.fireState();
		}

		if (mControlBoard.operator.startButton.wasActivated()) {
			mSuperstructure.exhaustState();
		} else if (mControlBoard.operator.startButton.wasReleased()) {
			mSuperstructure.idleState();
		}

		if (mControlBoard.driver.getPOV() == 0) {
			mHood.setWantJog(0.5);
		} else if (mControlBoard.driver.getPOV() == 180) {
			mHood.setWantJog(-0.5);
		}
	}

	private boolean mClimberJog = false;

	private void climbModeControls() {
		mSuperstructure.setWantClimbMode(true);
		if (!top_buttons_clear) {
			mSuperstructure.tuckState();
			if (mControlBoard.topButtonsClearOperator()) {
				top_buttons_clear = true;
			}
			return;
		}

		if (mControlBoard.getExitClimbModeOperator()) {
			mClimbMode = false;
			return;
		}

		if (mControlBoard.driver.getLeftBumper()) {
			mSuperstructure.tuckState();
		}

		if (mControlBoard.operator.aButton.wasActivated()) {
			mClimber.setSetpointMotionMagic(Climber.kRetractionHeight);
		} else if (mControlBoard.operator.yButton.wasActivated()) {
			mClimber.setSetpointMotionMagic(Climber.kExtensionHeight);
		} else if (mControlBoard.operator.bButton.wasActivated()) {
			mClimber.setSetpointMotionMagic(Climber.kPrepHeight);
		}
		if (mControlBoard.operator.xButton.wasActivated()) {
			mClimber.setSetpointMotionMagic(Climber.kPullHeight);
		}

		if (mControlBoard.operator.rightBumper.wasActivated()) {
			mSuperstructure.elevatorFullExtendTransition();
		} else if (mControlBoard.operator.leftBumper.wasActivated()) {
			mSuperstructure.idleState();
		}

		if (mControlBoard.operator.leftCenterClick.wasActivated()) {
			mClimberJog = !mClimberJog;
			mClimber.enableSoftLimits(!mClimberJog);
			mClimber.setStatorCurrentLimit(5.0, mClimberJog);
		}

		if (mClimberJog) {
			if (mControlBoard.operator.getPOV() == 0) {
				mClimber.setOpenLoop(0.5);
			} else if (mControlBoard.operator.getPOV() == 180) {
				mClimber.setOpenLoop(-0.5);
			} else if (mControlBoard.operator.getPOV() == 90) {
				mClimber.zeroSensors();
			} else {
				mClimber.setOpenLoop(0.0);
			}
		}
		SmartDashboard.putBoolean("Climber Open Loop", mClimberJog);
	}
}
