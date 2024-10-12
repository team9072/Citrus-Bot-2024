package com.team1678.frc2024;

import com.team254.lib.drivers.CanDeviceId;

public class Ports {
	/*
	 * LIST OF CHANNEL AND CAN IDS
	 *
	 * Swerve Modules go:
	 * 0 1
	 * 2 3
	 *
	 * spotless:off
	 */

	/* DRIVETRAIN CAN DEVICE IDS */
	public static final CanDeviceId FL_DRIVE = new CanDeviceId(2, "canivore1");
	public static final CanDeviceId FL_ROTATION = new CanDeviceId(3, "canivore1");
	public static final CanDeviceId FL_CANCODER = new CanDeviceId(4, "canivore1");

	public static final CanDeviceId FR_DRIVE = new CanDeviceId(5, "canivore1");
	public static final CanDeviceId FR_ROTATION = new CanDeviceId(6, "canivore1");
	public static final CanDeviceId FR_CANCODER = new CanDeviceId(7, "canivore1");

	public static final CanDeviceId BL_DRIVE = new CanDeviceId(8, "canivore1");
	public static final CanDeviceId BL_ROTATION = new CanDeviceId(9, "canivore1");
	public static final CanDeviceId BL_CANCODER = new CanDeviceId(10, "canivore1");

	public static final CanDeviceId BR_DRIVE = new CanDeviceId(11, "canivore1");
	public static final CanDeviceId BR_ROTATION = new CanDeviceId(12, "canivore1");
	public static final CanDeviceId BR_CANCODER = new CanDeviceId(13, "canivore1");

	/* SUBSYSTEM CAN DEVICE IDS */
	public static final CanDeviceId INTAKE_PIVOT = new CanDeviceId(14, "canivore1");
	public static final CanDeviceId INTAKE_ROLLER = new CanDeviceId(15, "canivore1");

	public static final CanDeviceId SERIALIZER = new CanDeviceId(16, "canivore1");
	public static final CanDeviceId FEEDER = new CanDeviceId(17, "canivore1");

	public static final CanDeviceId AMP_ROLLER = new CanDeviceId(18, "canivore1");

	public static final CanDeviceId ELEVATOR_MAIN = new CanDeviceId(19, "canivore1");
	public static final CanDeviceId ELEVATOR_FOLLOWER = new CanDeviceId(20, "canivore1");

	public static final CanDeviceId SHOOTER_TOP = new CanDeviceId(21, "canivore1");
	public static final CanDeviceId SHOOTER_BOTTOM = new CanDeviceId(22, "canivore1");

	public static final CanDeviceId HOOD = new CanDeviceId(23, "canivore1");
	public static final CanDeviceId HOOD_CANCODER = new CanDeviceId(24, "canivore1");

	public static final CanDeviceId CLIMBER_MAIN = new CanDeviceId(25, "canivore1");
	public static final CanDeviceId CLIMBER_FOLLOWER = new CanDeviceId(26, "canivore1");

	// public static final CanDeviceId PIGEON = new CanDeviceId(1, "canivore1");
	public static final int PIGEON = 1;
	
	public static final CanDeviceId LEDS = new CanDeviceId(2, "rio");

	/* BEAM BREAK DIO CHANNELS*/
	public static final int SERIALIZER_BREAK = 1;
	public static final int FEEDER_BREAK = 2;
	public static final int AMP_BREAK = 0; 

	/* LINEAR SERVO PWM CHANNELS */
	public static final int CLIMBER_LINEAR_ACTUATOR = 9;
	public static final int ELEVATOR_LINEAR_ACTUATOR = 0;

	// spotless:on
}