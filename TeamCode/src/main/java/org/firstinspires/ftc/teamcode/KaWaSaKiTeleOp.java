package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;

/** Main driver-controlled program. Mechanism logic lives in separate subsystems. */
@TeleOp(name = "KaWaSaKi Dual Ball", group = "Main")
public class KaWaSaKiTeleOp extends OpMode {

    private DcMotorEx leftFront;
    private DcMotorEx rightFront;
    private DcMotorEx leftRear;
    private DcMotorEx rightRear;

    private IntakeSubsystem intake;
    private ShooterSubsystem shooter;
    private TurretSubsystem turret;

    @Override
    public void init() {
        initDrive();

        intake = new IntakeSubsystem(hardwareMap);
        shooter = new ShooterSubsystem(hardwareMap);
        turret = new TurretSubsystem(hardwareMap);

        telemetry.addLine("KaWaSaKi ready");
        telemetry.addLine("Center both turrets before INIT");
        telemetry.update();
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
        if (gamepad1.back || gamepad2.back) {
            stopEverything();
            telemetry.addLine("EMERGENCY STOP (hold BACK)");
            telemetry.update();
            return;
        }

        drive();
        controlIntake();
        controlShooter();
        controlTurret();

        telemetry.addData("Turret", "%d ticks", turret.getPosition());
        telemetry.addData("Ramp", intake.isRampDown() ? "DOWN" : "UP");
        telemetry.update();
    }

    private void drive() {
        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x * Constants.Drive.STRAFE_MULTIPLIER;
        double turn = gamepad1.right_stick_x;

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

    private void controlIntake() {
        if (gamepad2.right_trigger > 0.15) {
            intake.intake(gamepad2.right_trigger);
        } else if (gamepad2.left_trigger > 0.15) {
            intake.reverse(gamepad2.left_trigger);
        } else {
            intake.stop();
        }

        if (gamepad2.dpad_down) {
            intake.lowerRamp();
        } else if (gamepad2.dpad_up) {
            intake.raiseRamp();
        }
    }

    private void controlShooter() {
        shooter.setBigShooter(gamepad2.right_bumper);
        shooter.setSmallShooter(gamepad2.left_bumper);
        shooter.setBigGateOpen(gamepad2.a);
        shooter.setSmallGateOpen(gamepad2.b);
    }

    private void controlTurret() {
        turret.manual(gamepad2.right_stick_x);
    }

    private void stopDrive() {
        leftFront.setPower(0.0);
        rightFront.setPower(0.0);
        leftRear.setPower(0.0);
        rightRear.setPower(0.0);
    }

    private void stopEverything() {
        stopDrive();
        intake.stop();
        shooter.stop();
        shooter.closeGates();
        turret.stop();
    }

    @Override
    public void stop() {
        stopEverything();
        intake.raiseRamp();
    }
}
