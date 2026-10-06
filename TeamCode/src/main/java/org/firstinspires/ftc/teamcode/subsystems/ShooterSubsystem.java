package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Constants;

/** Both BIOBUZZ flywheels run together and both gates release together. */
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

    private double pollenTargetVelocity;
    private double nectarTargetVelocity;

    public ShooterSubsystem(HardwareMap hardwareMap) {
        bigShooter = hardwareMap.get(DcMotorEx.class, BIG_MOTOR_NAME);
        smallShooter = hardwareMap.get(DcMotorEx.class, SMALL_MOTOR_NAME);
        bigGate = hardwareMap.get(Servo.class, BIG_GATE_NAME);
        smallGate = hardwareMap.get(Servo.class, SMALL_GATE_NAME);

        configure(bigShooter);
        configure(smallShooter);
        stop();
    }

    private static void configure(DcMotorEx motor) {
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void spinBoth(double pollenTicksPerSecond, double nectarTicksPerSecond) {
        pollenTargetVelocity = pollenTicksPerSecond;
        nectarTargetVelocity = nectarTicksPerSecond;
        smallShooter.setVelocity(pollenTicksPerSecond);
        bigShooter.setVelocity(nectarTicksPerSecond);
    }

    public boolean areBothAtSpeed() {
        return Math.abs(getPollenVelocity() - pollenTargetVelocity)
                        <= Constants.BioBuzz.SHOOTER_READY_TOLERANCE
                && Math.abs(getNectarVelocity() - nectarTargetVelocity)
                        <= Constants.BioBuzz.SHOOTER_READY_TOLERANCE;
    }

    public void setBothGatesOpen(boolean open) {
        bigGate.setPosition(open ? BIG_GATE_OPEN : BIG_GATE_CLOSED);
        smallGate.setPosition(open ? SMALL_GATE_OPEN : SMALL_GATE_CLOSED);
    }

    public double getPollenVelocity() {
        return smallShooter.getVelocity();
    }

    public double getNectarVelocity() {
        return bigShooter.getVelocity();
    }

    public double getPollenTargetVelocity() {
        return pollenTargetVelocity;
    }

    public double getNectarTargetVelocity() {
        return nectarTargetVelocity;
    }

    public void stop() {
        bigShooter.setPower(0.0);
        smallShooter.setPower(0.0);
        pollenTargetVelocity = 0.0;
        nectarTargetVelocity = 0.0;
        setBothGatesOpen(false);
    }
}
