package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Constants;

/** One geared motor mechanically turns both turrets. */
public class TurretSubsystem {

    private final DcMotorEx motor;
    private double targetAngleRadians;
    private double angleErrorRadians;

    public TurretSubsystem(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, Constants.Turret.MOTOR_NAME);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Both turrets must be mechanically centered before INIT.
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        stop();
    }

    public void aimAt(double robotRelativeAngleRadians) {
        targetAngleRadians = normalizeRadians(
                robotRelativeAngleRadians - Constants.Turret.ZERO_OFFSET_RADIANS
        );

        int targetTicks = (int) Math.round(
                targetAngleRadians * Constants.Turret.TICKS_PER_RADIAN
        );
        targetTicks = Range.clip(
                targetTicks,
                Constants.Turret.MIN_TICKS,
                Constants.Turret.MAX_TICKS
        );

        int errorTicks = targetTicks - motor.getCurrentPosition();
        angleErrorRadians = errorTicks / Constants.Turret.TICKS_PER_RADIAN;
        double power = Range.clip(
                errorTicks * Constants.Turret.AIM_KP,
                -Constants.Turret.MAX_POWER,
                Constants.Turret.MAX_POWER
        );
        motor.setPower(power);
    }

    public void manual(double stickX) {
        double power = Math.abs(stickX) > Constants.Turret.STICK_DEADZONE
                ? Range.clip(stickX, -1.0, 1.0) * Constants.Turret.MAX_POWER
                : 0.0;

        int position = motor.getCurrentPosition();
        if ((position >= Constants.Turret.MAX_TICKS && power > 0.0)
                || (position <= Constants.Turret.MIN_TICKS && power < 0.0)) {
            power = 0.0;
        }
        motor.setPower(power);
    }

    public boolean isAimed() {
        return Math.abs(angleErrorRadians) <= Constants.Turret.AIM_TOLERANCE_RADIANS;
    }

    public double getTargetAngleRadians() {
        return targetAngleRadians;
    }

    public double getAngleErrorRadians() {
        return angleErrorRadians;
    }

    public int getPosition() {
        return motor.getCurrentPosition();
    }

    public void stop() {
        motor.setPower(0.0);
    }

    private static double normalizeRadians(double angle) {
        while (angle > Math.PI) angle -= 2.0 * Math.PI;
        while (angle < -Math.PI) angle += 2.0 * Math.PI;
        return angle;
    }
}
