package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/** Intake and ramp controls for the single driver. */
public class IntakeSubsystem {

    public static final String MOTOR_NAME = "intake";
    public static final String RAMP_SERVO_NAME = "ramp";

    public static double INTAKE_POWER = -1.0;
    public static double REVERSE_POWER = 1.0;
    public static double RAMP_UP_POSITION = 0.15;
    public static double RAMP_DOWN_POSITION = 0.78;

    private final DcMotorEx motor;
    private final Servo ramp;
    private boolean rampDown;

    public IntakeSubsystem(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        ramp = hardwareMap.get(Servo.class, RAMP_SERVO_NAME);

        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stop();
        raiseRamp();
    }

    public void control(Gamepad gamepad) {
        if (gamepad.left_bumper) {
            reverse(1.0);
        } else if (gamepad.left_trigger > 0.05) {
            intake(gamepad.left_trigger);
        } else {
            stop();
        }

        if (gamepad.a) lowerRamp();
        if (gamepad.b) raiseRamp();
    }

    public void intake(double trigger) {
        motor.setPower(Range.clip(INTAKE_POWER * trigger, -1.0, 1.0));
    }

    public void reverse(double trigger) {
        motor.setPower(Range.clip(REVERSE_POWER * trigger, -1.0, 1.0));
    }

    public void stop() {
        motor.setPower(0.0);
    }

    public void raiseRamp() {
        rampDown = false;
        ramp.setPosition(RAMP_UP_POSITION);
    }

    public void lowerRamp() {
        rampDown = true;
        ramp.setPosition(RAMP_DOWN_POSITION);
    }

    public boolean isRampDown() {
        return rampDown;
    }
}
