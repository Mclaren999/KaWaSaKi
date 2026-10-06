package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;

/** Driver controls only. All drive logic lives in DriveSubsystem. */
@TeleOp(name = "KaWaSaKi Base", group = "Main")
public class KaWaSaKiTeleOp extends OpMode {

    private DriveSubsystem drive;

    @Override
    public void init() {
        drive = new DriveSubsystem(hardwareMap);
        telemetry.addLine("KaWaSaKi ready");
        telemetry.update();
    }

    @Override
    public void start() {
        drive.start();
    }

    @Override
    public void loop() {
        drive.drive(gamepad1);
        drive.addTelemetry(telemetry);
        telemetry.update();
    }

    @Override
    public void stop() {
        drive.stop();
    }
}
