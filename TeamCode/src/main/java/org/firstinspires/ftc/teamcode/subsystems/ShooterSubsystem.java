package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Constants;

/** Independent NECTAR and POLLEN flywheels and release gates. */
public class ShooterSubsystem {

    public enum ShotType {
        POLLEN,
        NECTAR
    }

    public static final String BIG_MOTOR_NAME = "bigShooter";
    public static final String SMALL_MOTOR_NAME = "smallShooter";
    public static final String BIG_GATE_NAME = "bigGate";
    public static final String SMALL_GATE_NAME = "smallGate";

    public static double BIG_GATE_CLOSED = 0.80;
    public static double BIG_GATE_OPEN = 0.45;
    public static double SMALL_GATE_CLOSED = 0.80;
    public static double SMALL_GATE_OPEN = 0.45;

    private final DcMotorEx bigShooter;
    private final DcMotorEx smallShooter;
    private final Servo bigGate;
    private final Servo smallGate;

    private ShotType activeType = ShotType.POLLEN;
    private double targetVelocity;

    public ShooterSubsystem(HardwareMap hardwareMap) {
        bigShooter = hardwareMap.get(DcMotorEx.class, BIG_MOTOR_NAME);
        smallShooter = hardwareMap.get(DcMotorEx.class, SMALL_MOTOR_NAME);
        bigGate = hardwareMap.get(Servo.class, BIG_GATE_NAME);
        smallGate = hardwareMap.get(Servo.class, SMALL_GATE_NAME);

        configure(bigShooter);
        configure(smallShooter);
        stop();
        closeGates();
    }

    private static void configure(DcMotorEx motor) {
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void spin(ShotType type, double ticksPerSecond) {
        activeType = type;
        targetVelocity = ticksPerSecond;

        if (type == ShotType.NECTAR) {
            bigShooter.setVelocity(ticksPerSecond);
            smallShooter.setPower(0.0);
        } else {
            smallShooter.setVelocity(ticksPerSecond);
            bigShooter.setPower(0.0);
        }
    }

    public boolean isAtSpeed() {
        return Math.abs(getVelocity() - targetVelocity)
                <= Constants.BioBuzz.SHOOTER_READY_TOLERANCE;
    }

    public double getVelocity() {
        return activeType == ShotType.NECTAR
                ? bigShooter.getVelocity()
                : smallShooter.getVelocity();
    }

    public double getTargetVelocity() {
        return targetVelocity;
    }

    public void setGateOpen(boolean open) {
        if (activeType == ShotType.NECTAR) {
            bigGate.setPosition(open ? BIG_GATE_OPEN : BIG_GATE_CLOSED);
            smallGate.setPosition(SMALL_GATE_CLOSED);
        } else {
            smallGate.setPosition(open ? SMALL_GATE_OPEN : SMALL_GATE_CLOSED);
            bigGate.setPosition(BIG_GATE_CLOSED);
        }
    }

    public void closeGates() {
        bigGate.setPosition(BIG_GATE_CLOSED);
        smallGate.setPosition(SMALL_GATE_CLOSED);
    }

    public void stop() {
        bigShooter.setPower(0.0);
        smallShooter.setPower(0.0);
        targetVelocity = 0.0;
        closeGates();
    }
}
