package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Constants;

/** One geared motor automatically turns both linked turrets. */
public class TurretSubsystem {

    private final DcMotorEx motor;
    private double targetAngleRadians;
    private double angleErrorRadians;

    public TurretSubsystem(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, Constants.Turret.MOTOR_NAME);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Mechanically point the turrets at their zero direction before INIT.
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        stop();
    }

    public void aimAt(double robotRelativeAngleRadians) {
        targetAngleRadians = Range.clip(
                normalizeRadians(robotRelativeAngleRadians
                        - Constants.Turret.ZERO_OFFSET_RADIANS),
                Constants.Turret.MIN_ANGLE_RADIANS,
                Constants.Turret.MAX_ANGLE_RADIANS
        );

        int targetTicks = (int) Math.round(
                targetAngleRadians * Constants.Turret.TICKS_PER_RADIAN
        );
        int errorTicks = targetTicks - motor.getCurrentPosition();
        angleErrorRadians = errorTicks / Constants.Turret.TICKS_PER_RADIAN;

        motor.setPower(Range.clip(
                errorTicks * Constants.Turret.AIM_KP,
                -Constants.Turret.MAX_POWER,
                Constants.Turret.MAX_POWER
        ));
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
