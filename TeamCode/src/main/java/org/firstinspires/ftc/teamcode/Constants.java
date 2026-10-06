package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Shared robot constants.
 *
 * The mecanum base is configured here so OpModes do not duplicate motor names,
 * directions or drive scaling.
 */
public final class Constants {

    private Constants() {
        // Utility class.
    }

    public static final class Drive {
        public static final String LEFT_FRONT = "leftFront";
        public static final String RIGHT_FRONT = "rightFront";
        public static final String LEFT_REAR = "leftRear";
        public static final String RIGHT_REAR = "rightRear";

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

        private Drive() {
        }
    }
}
