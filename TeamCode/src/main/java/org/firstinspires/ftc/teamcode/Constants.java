package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

/** All robot constants that are expected to be tuned on the real robot. */
public final class Constants {

    private Constants() {
    }

    public static final class Drive {
        public static final String LEFT_FRONT = "leftFront";
        public static final String RIGHT_FRONT = "rightFront";
        public static final String LEFT_REAR = "leftRear";
        public static final String RIGHT_REAR = "rightRear";
        public static final String PINPOINT_NAME = "pinpoint";

        public static final DcMotorSimple.Direction LEFT_FRONT_DIRECTION =
                DcMotorSimple.Direction.REVERSE;
        public static final DcMotorSimple.Direction LEFT_REAR_DIRECTION =
                DcMotorSimple.Direction.REVERSE;
        public static final DcMotorSimple.Direction RIGHT_FRONT_DIRECTION =
                DcMotorSimple.Direction.FORWARD;
        public static final DcMotorSimple.Direction RIGHT_REAR_DIRECTION =
                DcMotorSimple.Direction.FORWARD;

        public static double NORMAL_POWER = 0.75;
        public static double TURBO_POWER = 1.0;
        public static double STRAFE_MULTIPLIER = 1.1;

        public static double HEADING_KP = 1.8;
        public static double HEADING_KI = 0.0;
        public static double HEADING_KD = 0.10;
        public static double HEADING_KF = 0.035;
        public static double HEADING_MAX_CORRECTION = 0.45;
        public static double HEADING_TOLERANCE_RADIANS = Math.toRadians(1.0);
        public static double HEADING_INTEGRAL_LIMIT = 0.25;
        public static double TURN_STICK_DEADZONE = 0.08;

        private Drive() {
        }
    }

    /** BIOBUZZ field targets in Pedro coordinates: inches, origin at bottom-left. */
    public static final class BioBuzz {
        public static final double FIELD_CENTER_X = 72.0;
        public static final double FIELD_CENTER_Y = 72.0;

        /*
         * Starter coordinates. Verify against your field/CAD and adjust in one
         * place. The two CELL centers are approximately 18.8 inches apart.
         */
        public static double RED_HIVE_X = 60.0;
        public static double BLUE_HIVE_X = 84.0;
        public static double AUDIENCE_CELL_Y = FIELD_CENTER_Y - 9.4;
        public static double FAR_CELL_Y = FIELD_CENTER_Y + 9.4;

        // Shooter exit position relative to Pinpoint robot pose.
        public static double SHOOTER_OFFSET_FORWARD = 0.0;
        public static double SHOOTER_OFFSET_LEFT = 0.0;

        // Flight-time model: seconds = delay + distance * secondsPerInch.
        public static double POLLEN_RELEASE_DELAY_SECONDS = 0.08;
        public static double POLLEN_SECONDS_PER_INCH = 0.0080;
        public static double NECTAR_RELEASE_DELAY_SECONDS = 0.09;
        public static double NECTAR_SECONDS_PER_INCH = 0.0085;

        // Initial DcMotorEx velocity maps; tune with measured shots.
        public static double POLLEN_BASE_TICKS_PER_SECOND = 1350.0;
        public static double POLLEN_TICKS_PER_SECOND_PER_INCH = 4.0;
        public static double NECTAR_BASE_TICKS_PER_SECOND = 1450.0;
        public static double NECTAR_TICKS_PER_SECOND_PER_INCH = 4.5;
        public static double SHOOTER_READY_TOLERANCE = 90.0;

        private BioBuzz() {
        }
    }

    public static final class Turret {
        public static final String MOTOR_NAME = "turret";
        public static double TICKS_PER_RADIAN = 420.0;
        public static double ZERO_OFFSET_RADIANS = 0.0;
        public static int MIN_TICKS = -900;
        public static int MAX_TICKS = 900;
        public static double AIM_KP = 0.004;
        public static double MAX_POWER = 0.35;
        public static double AIM_TOLERANCE_RADIANS = Math.toRadians(2.0);
        public static double STICK_DEADZONE = 0.08;

        private Turret() {
        }
    }
}
