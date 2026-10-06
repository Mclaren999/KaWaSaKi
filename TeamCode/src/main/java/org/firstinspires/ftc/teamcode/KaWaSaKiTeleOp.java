package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Clean TeleOp for the mecanum base only.
 *
 * Pinpoint supplies the heading. When the driver releases the right stick,
 * Heading PIDF holds the angle at which the stick was released.
 */
@TeleOp(name = "KaWaSaKi Base", group = "Main")
public class KaWaSaKiTeleOp extends OpMode {

    private DcMotorEx leftFront;
    private DcMotorEx rightFront;
    private DcMotorEx leftRear;
    private DcMotorEx rightRear;
    private GoBildaPinpointDriver pinpoint;

    private double targetHeading;
    private double currentHeading;
    private double headingError;
    private double headingCorrection;
    private double headingIntegral;
    private double previousHeadingError;
    private long previousPidTimeNanos;
    private boolean headingHoldActive;

    @Override
    public void init() {
        initDrive();

        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                Constants.Drive.PINPOINT_NAME
        );

        // Keep the robot stationary during INIT while Pinpoint resets its pose and IMU.
        pinpoint.resetPosAndIMU();
        pinpoint.update();

        currentHeading = readHeadingRadians();
        targetHeading = currentHeading;
        resetHeadingPid();

        telemetry.addLine("KaWaSaKi base ready");
        telemetry.addLine("Heading source: Pinpoint");
        telemetry.update();
    }

    @Override
    public void start() {
        pinpoint.update();
        currentHeading = readHeadingRadians();
        targetHeading = currentHeading;
        resetHeadingPid();
    }

    private void initDrive() {
        leftFront = hardwareMap.get(DcMotorEx.class, Constants.Drive.LEFT_FRONT);
        rightFront = hardwareMap.get(DcMotorEx.class, Constants.Drive.RIGHT_FRONT);
        leftRear = hardwareMap.get(DcMotorEx.class, Constants.Drive.LEFT_REAR);
        rightRear = hardwareMap.get(DcMotorEx.class, Constants.Drive.RIGHT_REAR);

        leftFront.setDirection(Constants.Drive.LEFT_FRONT_DIRECTION);
        leftRear.setDirection(Constants.Drive.LEFT_REAR_DIRECTION);
        rightFront.setDirection(Constants.Drive.RIGHT_FRONT_DIRECTION);
        rightRear.setDirection(Constants.Drive.RIGHT_REAR_DIRECTION);

        configureDriveMotor(leftFront);
        configureDriveMotor(rightFront);
        configureDriveMotor(leftRear);
        configureDriveMotor(rightRear);
    }

    private void configureDriveMotor(DcMotorEx motor) {
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0.0);
    }

    @Override
    public void loop() {
        drive();

        telemetry.addData("Heading", "%.1f deg", Math.toDegrees(currentHeading));
        telemetry.addData("Target", "%.1f deg", Math.toDegrees(targetHeading));
        telemetry.addData("Error", "%.2f deg", Math.toDegrees(headingError));
        telemetry.addData("PIDF correction", "%.3f", headingCorrection);
        telemetry.update();
    }

    private void drive() {
        pinpoint.update();
        currentHeading = readHeadingRadians();

        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x * Constants.Drive.STRAFE_MULTIPLIER;
        double manualTurn = gamepad1.right_stick_x;

        double turn;
        if (Math.abs(manualTurn) > Constants.Drive.TURN_STICK_DEADZONE) {
            // Driver is turning: do not fight the stick. Continuously move the target.
            targetHeading = currentHeading;
            resetHeadingPid();
            turn = manualTurn;
        } else {
            turn = calculateHeadingCorrection();
        }

        double denominator = Math.max(
                Math.abs(forward) + Math.abs(strafe) + Math.abs(turn),
                1.0
        );
        double speed = gamepad1.right_trigger > 0.25
                ? Constants.Drive.TURBO_POWER
                : Constants.Drive.NORMAL_POWER;

        leftFront.setPower((forward + strafe + turn) / denominator * speed);
        leftRear.setPower((forward - strafe + turn) / denominator * speed);
        rightFront.setPower((forward - strafe - turn) / denominator * speed);
        rightRear.setPower((forward + strafe - turn) / denominator * speed);
    }

    private double calculateHeadingCorrection() {
        long now = System.nanoTime();

        // The first loop after manual turning only captures the new target cleanly.
        if (!headingHoldActive) {
            targetHeading = currentHeading;
            previousPidTimeNanos = now;
            previousHeadingError = 0.0;
            headingIntegral = 0.0;
            headingError = 0.0;
            headingCorrection = 0.0;
            headingHoldActive = true;
            return 0.0;
        }

        headingError = normalizeRadians(targetHeading - currentHeading);
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

    private double readHeadingRadians() {
        return pinpoint.getPosition().getHeading(AngleUnit.RADIANS);
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

    private void stopDrive() {
        leftFront.setPower(0.0);
        rightFront.setPower(0.0);
        leftRear.setPower(0.0);
        rightRear.setPower(0.0);
    }

    @Override
    public void stop() {
        stopDrive();
    }
}
