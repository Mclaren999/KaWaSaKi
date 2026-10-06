package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp for:
 * - four-motor mecanum drive;
 * - one intake motor;
 * - separate big-ball and small-ball shooter motors;
 * - one geared turret motor that turns both turrets;
 * - big-ball gate, small-ball gate and ramp servos.
 *
 * Before INIT, place both geared turrets in their mechanical center position.
 */
@TeleOp(name = "KaWaSaKi Dual Ball", group = "Main")
public class KaWaSaKiTeleOp extends OpMode {

    // Hardware Configuration names.
    private static final String LEFT_FRONT_NAME = "leftFront";
    private static final String RIGHT_FRONT_NAME = "rightFront";
    private static final String LEFT_REAR_NAME = "leftRear";
    private static final String RIGHT_REAR_NAME = "rightRear";
    private static final String INTAKE_NAME = "intake";
    private static final String BIG_SHOOTER_NAME = "bigShooter";
    private static final String SMALL_SHOOTER_NAME = "smallShooter";
    private static final String TURRET_NAME = "turret";
    private static final String BIG_GATE_NAME = "bigGate";
    private static final String SMALL_GATE_NAME = "smallGate";
    private static final String RAMP_NAME = "ramp";

    // Tune these values carefully with the robot raised off the floor.
    public static double DRIVE_POWER = 0.75;
    public static double INTAKE_POWER = 1.0;
    public static double BIG_SHOOTER_POWER = 1.0;
    public static double SMALL_SHOOTER_POWER = 1.0;
    public static double TURRET_POWER = 0.35;
    public static int TURRET_MIN_TICKS = -900;
    public static int TURRET_MAX_TICKS = 900;

    // Servo positions must be tuned for the actual linkage.
    public static double BIG_GATE_CLOSED = 0.80;
    public static double BIG_GATE_OPEN = 0.45;
    public static double SMALL_GATE_CLOSED = 0.80;
    public static double SMALL_GATE_OPEN = 0.45;
    public static double RAMP_UP = 0.15;
    public static double RAMP_DOWN = 0.78;

    private DcMotorEx leftFront;
    private DcMotorEx rightFront;
    private DcMotorEx leftRear;
    private DcMotorEx rightRear;
    private DcMotorEx intake;
    private DcMotorEx bigShooter;
    private DcMotorEx smallShooter;
    private DcMotorEx turret;

    private Servo bigGate;
    private Servo smallGate;
    private Servo ramp;

    private boolean rampIsDown;

    @Override
    public void init() {
        leftFront = hardwareMap.get(DcMotorEx.class, LEFT_FRONT_NAME);
        rightFront = hardwareMap.get(DcMotorEx.class, RIGHT_FRONT_NAME);
        leftRear = hardwareMap.get(DcMotorEx.class, LEFT_REAR_NAME);
        rightRear = hardwareMap.get(DcMotorEx.class, RIGHT_REAR_NAME);

        intake = hardwareMap.get(DcMotorEx.class, INTAKE_NAME);
        bigShooter = hardwareMap.get(DcMotorEx.class, BIG_SHOOTER_NAME);
        smallShooter = hardwareMap.get(DcMotorEx.class, SMALL_SHOOTER_NAME);
        turret = hardwareMap.get(DcMotorEx.class, TURRET_NAME);

        bigGate = hardwareMap.get(Servo.class, BIG_GATE_NAME);
        smallGate = hardwareMap.get(Servo.class, SMALL_GATE_NAME);
        ramp = hardwareMap.get(Servo.class, RAMP_NAME);

        // Matches the usual FTC mecanum arrangement.
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);

        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        bigShooter.setDirection(DcMotorSimple.Direction.FORWARD);
        smallShooter.setDirection(DcMotorSimple.Direction.FORWARD);
        turret.setDirection(DcMotorSimple.Direction.FORWARD);

        setBrake(leftFront);
        setBrake(rightFront);
        setBrake(leftRear);
        setBrake(rightRear);
        setBrake(intake);
        setBrake(turret);

        // Shooter wheels coast down instead of stopping abruptly.
        bigShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        smallShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // INIT defines the centered turret position as encoder zero.
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        bigGate.setPosition(BIG_GATE_CLOSED);
        smallGate.setPosition(SMALL_GATE_CLOSED);
        ramp.setPosition(RAMP_UP);
        rampIsDown = false;

        stopAllMotors();

        telemetry.addLine("KaWaSaKi ready");
        telemetry.addLine("Center both turrets before pressing INIT");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (gamepad1.back || gamepad2.back) {
            stopAllMotors();
            bigGate.setPosition(BIG_GATE_CLOSED);
            smallGate.setPosition(SMALL_GATE_CLOSED);
            telemetry.addLine("EMERGENCY STOP (hold BACK)");
            telemetry.update();
            return;
        }

        driveMecanum();
        controlIntake();
        controlShootersAndGates();
        controlTurret();
        controlRamp();

        telemetry.addData("Turret", "%d ticks", turret.getCurrentPosition());
        telemetry.addData("Ramp", rampIsDown ? "DOWN" : "UP");
        telemetry.addData("Big shooter", gamepad2.right_bumper ? "ON" : "OFF");
        telemetry.addData("Small shooter", gamepad2.left_bumper ? "ON" : "OFF");
        telemetry.update();
    }

    private void driveMecanum() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1;
        double turn = gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(turn), 1.0);
        double speed = gamepad1.right_trigger > 0.25 ? 1.0 : DRIVE_POWER;

        leftFront.setPower(((y + x + turn) / denominator) * speed);
        leftRear.setPower(((y - x + turn) / denominator) * speed);
        rightFront.setPower(((y - x - turn) / denominator) * speed);
        rightRear.setPower(((y + x - turn) / denominator) * speed);
    }

    private void controlIntake() {
        double power = 0.0;
        if (gamepad2.right_trigger > 0.15) {
            power = -INTAKE_POWER * gamepad2.right_trigger;
        } else if (gamepad2.left_trigger > 0.15) {
            power = INTAKE_POWER * gamepad2.left_trigger;
        }
        intake.setPower(Range.clip(power, -1.0, 1.0));
    }

    private void controlShootersAndGates() {
        bigShooter.setPower(gamepad2.right_bumper ? BIG_SHOOTER_POWER : 0.0);
        smallShooter.setPower(gamepad2.left_bumper ? SMALL_SHOOTER_POWER : 0.0);

        // Gates open only while the button is held.
        bigGate.setPosition(gamepad2.a ? BIG_GATE_OPEN : BIG_GATE_CLOSED);
        smallGate.setPosition(gamepad2.b ? SMALL_GATE_OPEN : SMALL_GATE_CLOSED);
    }

    private void controlTurret() {
        double command = Math.abs(gamepad2.right_stick_x) > 0.08
                ? gamepad2.right_stick_x * TURRET_POWER
                : 0.0;

        int position = turret.getCurrentPosition();
        if ((position >= TURRET_MAX_TICKS && command > 0)
                || (position <= TURRET_MIN_TICKS && command < 0)) {
            command = 0.0;
        }

        turret.setPower(Range.clip(command, -TURRET_POWER, TURRET_POWER));
    }

    private void controlRamp() {
        if (gamepad2.dpad_down) {
            rampIsDown = true;
        } else if (gamepad2.dpad_up) {
            rampIsDown = false;
        }
        ramp.setPosition(rampIsDown ? RAMP_DOWN : RAMP_UP);
    }

    private void setBrake(DcMotorEx motor) {
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void stopAllMotors() {
        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);
        intake.setPower(0);
        bigShooter.setPower(0);
        smallShooter.setPower(0);
        turret.setPower(0);
    }

    @Override
    public void stop() {
        stopAllMotors();
        bigGate.setPosition(BIG_GATE_CLOSED);
        smallGate.setPosition(SMALL_GATE_CLOSED);
        ramp.setPosition(RAMP_UP);
    }
}
