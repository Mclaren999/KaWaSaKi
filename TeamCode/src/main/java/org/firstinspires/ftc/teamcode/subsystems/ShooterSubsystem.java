package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Controls two independent shooters and their ball-release servos.
 * One motor/servo pair is for big balls and the other is for small balls.
 */
public class ShooterSubsystem {

    public static final String BIG_MOTOR_NAME = "bigShooter";
    public static final String SMALL_MOTOR_NAME = "smallShooter";
    public static final String BIG_GATE_NAME = "bigGate";
    public static final String SMALL_GATE_NAME = "smallGate";

    public static double BIG_SHOOTER_POWER = 1.0;
    public static double SMALL_SHOOTER_POWER = 1.0;
    public static double BIG_GATE_CLOSED = 0.80;
    public static double BIG_GATE_OPEN = 0.45;
    public static double SMALL_GATE_CLOSED = 0.80;
    public static double SMALL_GATE_OPEN = 0.45;

    private final DcMotorEx bigShooter;
    private final DcMotorEx smallShooter;
    private final Servo bigGate;
    private final Servo smallGate;

    public ShooterSubsystem(HardwareMap hardwareMap) {
        bigShooter = hardwareMap.get(DcMotorEx.class, BIG_MOTOR_NAME);
        smallShooter = hardwareMap.get(DcMotorEx.class, SMALL_MOTOR_NAME);
        bigGate = hardwareMap.get(Servo.class, BIG_GATE_NAME);
        smallGate = hardwareMap.get(Servo.class, SMALL_GATE_NAME);

        bigShooter.setDirection(DcMotorSimple.Direction.FORWARD);
        smallShooter.setDirection(DcMotorSimple.Direction.FORWARD);
        bigShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        smallShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        bigShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        smallShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stop();
        closeGates();
    }

    public void setBigShooter(boolean enabled) {
        bigShooter.setPower(enabled
                ? Range.clip(BIG_SHOOTER_POWER, -1.0, 1.0)
                : 0.0);
    }

    public void setSmallShooter(boolean enabled) {
        smallShooter.setPower(enabled
                ? Range.clip(SMALL_SHOOTER_POWER, -1.0, 1.0)
                : 0.0);
    }

    public void setBigGateOpen(boolean open) {
        bigGate.setPosition(open ? BIG_GATE_OPEN : BIG_GATE_CLOSED);
    }

    public void setSmallGateOpen(boolean open) {
        smallGate.setPosition(open ? SMALL_GATE_OPEN : SMALL_GATE_CLOSED);
    }

    public void closeGates() {
        setBigGateOpen(false);
        setSmallGateOpen(false);
    }

    public void stop() {
        bigShooter.setPower(0.0);
        smallShooter.setPower(0.0);
    }
}
