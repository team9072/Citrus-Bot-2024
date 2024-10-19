package com.team1678.frc2024.shooting;

public class SpeakerRegression {
	// spotless:off
        public static double[][] kOldHoodManualAngle = {

                        // @x --> distance from target (in meters)
                        // @y --> hood angle (in degrees)

                        { 1.17, 55.0 },
                        { 1.50, 51.0 },
                        { 2.0, 45.0 },
                        { 2.25, 42.0 },
                        { 2.5, 39.5 },
                        { 2.75, 37.0 },
                        { 3.0, 34.0 },
                        { 3.25, 33.0 },
                        { 3.5, 32.0 },
                        { 3.75, 31.0 },
                        { 4.0, 29.5 },
                        { 4.25, 28.5 },
                        { 4.5, 27.0 },
                        { 4.75, 26.6 },
                        { 5.0, 25.9 },
                        { 5.5, 25.0 },
                        { 6.0, 22.0 },
                        { 6.5, 20.0 },
                        { 7.0, 19.0 },
                        { 7.5, 17.5 },
        };

        // New for 9072
        public static double[][] kHoodManualAngle = {
                        // @x --> distance from target (in meters)
                        // @y --> hood angle (in degrees)
                        { 1.17, 55.0 },
                        { 1.50, 49.0 },
                        { 2.0, 42.0 },
                        { 2.25, 40.5 },
                        { 2.5, 38.2 },
                        { 2.75, 36.0 },
                        { 3.0, 34.0 },
                        { 3.25, 32.25 },
                        { 3.5, 31.3 },
                        { 3.75, 30.0 },
                        { 4.0, 29.0 },
                        { 4.25, 28.0 },
                        { 4.5, 27.3 },
                        { 4.75, 27.2 },
                        { 5.0, 26.0 },
                        { 5.5, 25.7 },
                        // TODO: Untuned after 5.5
                        { 6.0, 23.0 },
                        { 6.5, 20.0 },
                        { 7.0, 19.0 },
                        { 7.5, 17.5 },
        };

        public static double[][] kFlywheelManualRPM = {
                        // Need to change values

                        // @x --> distance from target (in meters)
                        // @y --> shooter velocity (in rpm)
                        { 2.0, 5400 },
                        { 2.5, 5400 },
                        { 3.0, 5400 },
                        { 3.5, 5400 },
                        { 4.0, 5400 },
                        { 4.5, 5400 },
                        { 5.0, 5400 },
                        { 5.5, 5400 },
                        { 6.0, 6000 },
                        { 6.5, 7000 },
                        { 7.0, 7000 },
                        { 7.5, 8000 },
        };

        public static double[][] kOldShooterCurveOffset = {
                        // @x --> distance from target (in meters)
                        // @y --> angle to add to robot heading (degrees)
                        { 2.1, -5.0 },
                        { 3.0, -8.0 },
                        { 4.0, -6.0 },
                        { 5.0, -4.0 },
                        { 6.0, -4.0 },
        };

        // New for 9072
        public static double[][] kShooterCurveOffset = {
                // @x --> distance from target (in meters)
                // @y --> angle to add to robot heading (degrees)
                {1.3, 0},
                {2.6, 0},
                {3.5, 0},
                {5.0, 1},
                {6.0, 3}

        };

        public static double[][] kSkewOffset = {
                        // @x --> horizontal offset from target (meters)
                        // @y --> angle to add to robot heading (degrees)
                        { 0.0, 0.0 },
                        { 3.0, 0.0 },
                        { 3.8, 0.0 }

        };
        // spotless:on
}
