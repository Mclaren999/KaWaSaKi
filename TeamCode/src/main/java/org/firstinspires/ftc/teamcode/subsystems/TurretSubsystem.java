package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

/**
 * Controls the single geared motor that turns both turrets.
 * The two turrets are mechanically synchronized, so only one motor is mapped.
 */
public class TurretSubsystem {

    public static final String MOTOR_NAME = "turret";

    public static double MAX_POWER = 0.35;
    public static int MIN_TICKS = -900;
    public static int MAX_TICKS = 900;
    public static double STICK_DEADZONE = 0.08;

    private final DcMotorEx motor;

    public TurretSubsystem(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Both turrets must be mechanically centered before INIT.
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        stop();
    }

    public void manual(double stickX) {
        double power = Math.abs(stickX) > STICK_DEADZONE
                ? Range.clip(stickX, -1.0, 1.0) * MAX_POWER
                : 0.0;

        int position = motor.getCurrentPosition();
        if ((position >= MAX_TICKS && power > 0.0)
                || (position <= MIN_TICKS && power < 0.0)) {
            power = 0.0;
        }

        motor.setPower(power);
    }

    public int getPosition() {
        return motor.getCurrentPosition();
    }

    public void stop() {
        motor.setPower(0.0);
    }
}
