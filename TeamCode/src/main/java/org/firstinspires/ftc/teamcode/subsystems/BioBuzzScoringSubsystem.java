package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants;

/**
 * Always-on automatic BIOBUZZ aiming for both mechanically linked turrets.
 * Both flywheels stay at their calculated velocity; the driver only requests
 * release. The gates remain closed until both shooters and the turret are ready.
 */
public class BioBuzzScoringSubsystem {

    private final ShooterSubsystem shooter;
    private final TurretSubsystem turret;

    private final BioBuzzTargeting.Alliance alliance;
    private final BioBuzzTargeting.Cell cell;

    private BioBuzzTargeting.AimSolution pollenSolution;
    private BioBuzzTargeting.AimSolution nectarSolution;
    private double commonTurretAngle;
    private boolean ready;

    public BioBuzzScoringSubsystem(HardwareMap hardwareMap) {
        shooter = new ShooterSubsystem(hardwareMap);
        turret = new TurretSubsystem(hardwareMap);

        alliance = Constants.BioBuzz.TARGET_RED_HIVE
                ? BioBuzzTargeting.Alliance.RED
                : BioBuzzTargeting.Alliance.BLUE;
        cell = Constants.BioBuzz.TARGET_AUDIENCE_CELL
                ? BioBuzzTargeting.Cell.AUDIENCE
                : BioBuzzTargeting.Cell.FAR;
    }

    public void update(
            Pose pose,
            double velocityX,
            double velocityY,
            double angularVelocity,
            boolean fireRequested
    ) {
        pollenSolution = BioBuzzTargeting.solve(
                pose, velocityX, velocityY, angularVelocity,
                alliance, cell, ShooterSubsystem.ShotType.POLLEN
        );
        nectarSolution = BioBuzzTargeting.solve(
                pose, velocityX, velocityY, angularVelocity,
                alliance, cell, ShooterSubsystem.ShotType.NECTAR
        );

        // One motor controls both turrets, so use the circular mean of both leads.
        commonTurretAngle = Math.atan2(
                Math.sin(pollenSolution.turretAngle)
                        + Math.sin(nectarSolution.turretAngle),
                Math.cos(pollenSolution.turretAngle)
                        + Math.cos(nectarSolution.turretAngle)
        );

        turret.aimAt(commonTurretAngle);
        shooter.spinBoth(
                pollenSolution.shooterVelocity,
                nectarSolution.shooterVelocity
        );

        ready = turret.isAimed() && shooter.areBothAtSpeed();
        shooter.setBothGatesOpen(fireRequested && ready);
    }

    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Automatic target", "%s / %s", alliance, cell);
        telemetry.addData("Both shooters ready", ready);
        telemetry.addData("Target", "(%.1f, %.1f)",
                pollenSolution.targetX, pollenSolution.targetY);
        telemetry.addData("Distance", "%.1f in", pollenSolution.distance);
        telemetry.addData("Turret", "%.1f deg / error %.1f deg",
                Math.toDegrees(commonTurretAngle),
                Math.toDegrees(turret.getAngleErrorRadians()));
        telemetry.addData("POLLEN", "%.0f / %.0f ticks/s",
                shooter.getPollenVelocity(), shooter.getPollenTargetVelocity());
        telemetry.addData("NECTAR", "%.0f / %.0f ticks/s",
                shooter.getNectarVelocity(), shooter.getNectarTargetVelocity());
    }

    public void stop() {
        shooter.stop();
        turret.stop();
    }
}
