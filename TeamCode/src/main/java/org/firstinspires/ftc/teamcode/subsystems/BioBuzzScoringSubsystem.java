package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Coordinates BIOBUZZ target selection, moving-shot lead, turret and flywheel.
 *
 * Controls on gamepad2:
 * A/B = POLLEN/NECTAR, X/Y = audience/far CELL,
 * dpad left/right = red/blue HIVE,
 * hold left trigger = auto aim + spin, right trigger = release when ready.
 */
public class BioBuzzScoringSubsystem {

    private final ShooterSubsystem shooter;
    private final TurretSubsystem turret;

    private BioBuzzTargeting.Alliance alliance = BioBuzzTargeting.Alliance.RED;
    private BioBuzzTargeting.Cell cell = BioBuzzTargeting.Cell.AUDIENCE;
    private ShooterSubsystem.ShotType shotType = ShooterSubsystem.ShotType.POLLEN;
    private BioBuzzTargeting.AimSolution solution;
    private boolean ready;

    public BioBuzzScoringSubsystem(HardwareMap hardwareMap) {
        shooter = new ShooterSubsystem(hardwareMap);
        turret = new TurretSubsystem(hardwareMap);
    }

    public void update(
            Gamepad gamepad,
            Pose pose,
            double velocityX,
            double velocityY,
            double angularVelocity
    ) {
        readSelection(gamepad);

        boolean autoAim = gamepad.left_trigger > 0.15;
        boolean fire = gamepad.right_trigger > 0.5;

        if (autoAim) {
            solution = BioBuzzTargeting.solve(
                    pose,
                    velocityX,
                    velocityY,
                    angularVelocity,
                    alliance,
                    cell,
                    shotType
            );
            turret.aimAt(solution.turretAngle);
            shooter.spin(shotType, solution.shooterVelocity);
            ready = turret.isAimed() && shooter.isAtSpeed();
            shooter.setGateOpen(fire && ready);
        } else {
            ready = false;
            shooter.stop();
            turret.manual(gamepad.right_stick_x);
        }
    }

    private void readSelection(Gamepad gamepad) {
        if (gamepad.a) shotType = ShooterSubsystem.ShotType.POLLEN;
        if (gamepad.b) shotType = ShooterSubsystem.ShotType.NECTAR;
        if (gamepad.x) cell = BioBuzzTargeting.Cell.AUDIENCE;
        if (gamepad.y) cell = BioBuzzTargeting.Cell.FAR;
        if (gamepad.dpad_left) alliance = BioBuzzTargeting.Alliance.RED;
        if (gamepad.dpad_right) alliance = BioBuzzTargeting.Alliance.BLUE;
    }

    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Shot", "%s / %s / %s", alliance, cell, shotType);
        telemetry.addData("Auto aim ready", ready);

        if (solution != null) {
            telemetry.addData("Target", "(%.1f, %.1f)",
                    solution.targetX, solution.targetY);
            telemetry.addData("Distance", "%.1f in", solution.distance);
            telemetry.addData("Lead time", "%.3f s", solution.flightTime);
            telemetry.addData("Turret target", "%.1f deg",
                    Math.toDegrees(solution.turretAngle));
            telemetry.addData("Turret error", "%.1f deg",
                    Math.toDegrees(turret.getAngleErrorRadians()));
            telemetry.addData("Flywheel", "%.0f / %.0f ticks/s",
                    shooter.getVelocity(), shooter.getTargetVelocity());
        }
    }

    public void stop() {
        shooter.stop();
        turret.stop();
    }
}
