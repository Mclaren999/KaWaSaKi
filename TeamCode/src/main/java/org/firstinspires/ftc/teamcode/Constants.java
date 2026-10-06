package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

/** Shared constants for the mecanum base and its Pinpoint heading controller. */
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

        // Heading PIDF. Pinpoint heading, error and derivative use radians.
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
}
