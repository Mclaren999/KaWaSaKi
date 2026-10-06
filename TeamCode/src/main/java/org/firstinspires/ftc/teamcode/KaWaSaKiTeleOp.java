package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.BioBuzzScoringSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;

/** Clean TeleOp: controls are delegated to drive and scoring subsystems. */
@TeleOp(name = "KaWaSaKi BIOBUZZ", group = "Main")
public class KaWaSaKiTeleOp extends OpMode {

    private DriveSubsystem drive;
    private BioBuzzScoringSubsystem scoring;

    @Override
    public void init() {
        drive = new DriveSubsystem(hardwareMap);
        scoring = new BioBuzzScoringSubsystem(hardwareMap);
        telemetry.addLine("KaWaSaKi BIOBUZZ ready");
        telemetry.update();
    }

    @Override
    public void start() {
        drive.start();
    }

    @Override
    public void loop() {
        drive.drive(gamepad1);
        scoring.update(
                gamepad2,
                drive.getPose(),
                drive.getFieldVelocityX(),
                drive.getFieldVelocityY(),
                drive.getAngularVelocity()
        );

        drive.addTelemetry(telemetry);
        scoring.addTelemetry(telemetry);
        telemetry.update();
    }

    @Override
    public void stop() {
        scoring.stop();
        drive.stop();
    }
}
