package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.BioBuzzScoringSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;

/** One driver: drive, intake and fire. Tracking starts automatically. */
@TeleOp(name = "KaWaSaKi BIOBUZZ Auto", group = "Main")
public class KaWaSaKiTeleOp extends OpMode {

    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private BioBuzzScoringSubsystem scoring;
    private boolean r3WasPressed;

    @Override
    public void init() {
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        scoring = new BioBuzzScoringSubsystem(hardwareMap);
        telemetry.addLine("Automatic tracking ready");
        telemetry.update();
    }

    @Override
    public void start() {
        drive.start();
    }

    @Override
    public void loop() {
        if (gamepad1.right_stick_button && !r3WasPressed) {
            drive.resetPoseAtReferencePoint();
        }
        r3WasPressed = gamepad1.right_stick_button;

        drive.drive(gamepad1);
        intake.control(gamepad1);
        scoring.update(
                drive.getPose(),
                drive.getFieldVelocityX(),
                drive.getFieldVelocityY(),
                drive.getAngularVelocity(),
                gamepad1.right_trigger > 0.5
        );

        drive.addTelemetry(telemetry);
        scoring.addTelemetry(telemetry);
        telemetry.addData("Ramp", intake.isRampDown() ? "DOWN" : "UP");
        telemetry.update();
    }

    @Override
    public void stop() {
        scoring.stop();
        intake.stop();
        drive.stop();
    }
}
