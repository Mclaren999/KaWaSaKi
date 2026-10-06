package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants;

/**
 * Owns the complete mecanum base: Pedro manual drive, Pinpoint heading and
 * Heading PIDF. TeleOp only passes the gamepad to this subsystem.
 */
public class DriveSubsystem {

    private final Follower follower;

    private double targetHeading;
    private double currentHeading;
    private double headingError;
    private double headingCorrection;
    private double headingIntegral;
    private double previousHeadingError;
    private long previousPidTimeNanos;
    private boolean headingHoldActive;

    public DriveSubsystem(HardwareMap hardwareMap) {
        follower = org.firstinspires.ftc.teamcode.pedro.Constants.create(hardwareMap);
        follower.setPose(Pose.zero());
        resetHeadingPid();
    }

    public void start() {
        follower.update();
        currentHeading = follower.pose().heading();
        targetHeading = currentHeading;
        resetHeadingPid();
    }

    public void drive(Gamepad gamepad) {
        currentHeading = follower.pose().heading();

        double forward = -gamepad.left_stick_y;
        double lateral = gamepad.left_stick_x * Constants.Drive.STRAFE_MULTIPLIER;
        double manualTurn = gamepad.right_stick_x;
        double turn = getTurnPower(manualTurn);
        double speed = gamepad.right_trigger > 0.25
                ? Constants.Drive.TURBO_POWER
                : Constants.Drive.NORMAL_POWER;

        follower.manual(
                forward * speed,
                lateral * speed,
                turn * speed
        );
        follower.update();

        currentHeading = follower.pose().heading();
    }

    private double getTurnPower(double manualTurn) {
        if (Math.abs(manualTurn) > Constants.Drive.TURN_STICK_DEADZONE) {
            targetHeading = currentHeading;
            resetHeadingPid();
            return manualTurn;
        }

        return calculateHeadingCorrection();
    }

    private double calculateHeadingCorrection() {
        long now = System.nanoTime();

        if (!headingHoldActive) {
            targetHeading = currentHeading;
            previousPidTimeNanos = now;
            headingHoldActive = true;
            return 0.0;
        }

        headingError = normalizeRadians(currentHeading - targetHeading);
        double dt = (now - previousPidTimeNanos) / 1_000_000_000.0;
        double derivative = 0.0;

        if (dt > 0.0 && dt < 0.1) {
            headingIntegral += headingError * dt;
            headingIntegral = Range.clip(
                    headingIntegral,
                    -Constants.Drive.HEADING_INTEGRAL_LIMIT,
                    Constants.Drive.HEADING_INTEGRAL_LIMIT
            );
            derivative = (headingError - previousHeadingError) / dt;
        }

        if (Math.abs(headingError) <= Constants.Drive.HEADING_TOLERANCE_RADIANS) {
            headingIntegral = 0.0;
            headingCorrection = 0.0;
        } else {
            headingCorrection =
                    Constants.Drive.HEADING_KP * headingError
                    + Constants.Drive.HEADING_KI * headingIntegral
                    + Constants.Drive.HEADING_KD * derivative
                    + Constants.Drive.HEADING_KF * Math.signum(headingError);

            headingCorrection = Range.clip(
                    headingCorrection,
                    -Constants.Drive.HEADING_MAX_CORRECTION,
                    Constants.Drive.HEADING_MAX_CORRECTION
            );
        }

        previousHeadingError = headingError;
        previousPidTimeNanos = now;
        return headingCorrection;
    }

    private double normalizeRadians(double angle) {
        while (angle > Math.PI) {
            angle -= 2.0 * Math.PI;
        }
        while (angle < -Math.PI) {
            angle += 2.0 * Math.PI;
        }
        return angle;
    }

    private void resetHeadingPid() {
        headingIntegral = 0.0;
        previousHeadingError = 0.0;
        headingError = 0.0;
        headingCorrection = 0.0;
        previousPidTimeNanos = System.nanoTime();
        headingHoldActive = false;
    }

    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Heading", "%.1f deg", Math.toDegrees(currentHeading));
        telemetry.addData("Target", "%.1f deg", Math.toDegrees(targetHeading));
        telemetry.addData("Error", "%.2f deg", Math.toDegrees(headingError));
        telemetry.addData("PIDF", "%.3f", headingCorrection);
    }

    public void stop() {
        follower.manual(0.0, 0.0, 0.0);
        follower.update();
    }
}
