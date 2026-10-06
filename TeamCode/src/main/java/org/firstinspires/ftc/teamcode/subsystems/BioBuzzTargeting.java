package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.Constants;

/** Pure field geometry and moving-shot lead calculation for BIOBUZZ. */
public final class BioBuzzTargeting {

    public enum Alliance {
        RED,
        BLUE
    }

    public enum Cell {
        AUDIENCE,
        FAR
    }

    public static final class AimSolution {
        public final double targetX;
        public final double targetY;
        public final double distance;
        public final double flightTime;
        public final double fieldAngle;
        public final double turretAngle;
        public final double shooterVelocity;

        private AimSolution(
                double targetX,
                double targetY,
                double distance,
                double flightTime,
                double fieldAngle,
                double turretAngle,
                double shooterVelocity
        ) {
            this.targetX = targetX;
            this.targetY = targetY;
            this.distance = distance;
            this.flightTime = flightTime;
            this.fieldAngle = fieldAngle;
            this.turretAngle = turretAngle;
            this.shooterVelocity = shooterVelocity;
        }
    }

    private BioBuzzTargeting() {
    }

    public static AimSolution solve(
            Pose robotPose,
            double robotVelocityX,
            double robotVelocityY,
            double robotAngularVelocity,
            Alliance alliance,
            Cell cell,
            ShooterSubsystem.ShotType shotType
    ) {
        double targetX = alliance == Alliance.RED
                ? Constants.BioBuzz.RED_HIVE_X
                : Constants.BioBuzz.BLUE_HIVE_X;
        double targetY = cell == Cell.AUDIENCE
                ? Constants.BioBuzz.AUDIENCE_CELL_Y
                : Constants.BioBuzz.FAR_CELL_Y;

        double cos = Math.cos(robotPose.heading());
        double sin = Math.sin(robotPose.heading());
        double offsetX =
                Constants.BioBuzz.SHOOTER_OFFSET_FORWARD * cos
                - Constants.BioBuzz.SHOOTER_OFFSET_LEFT * sin;
        double offsetY =
                Constants.BioBuzz.SHOOTER_OFFSET_FORWARD * sin
                + Constants.BioBuzz.SHOOTER_OFFSET_LEFT * cos;

        double shooterX = robotPose.x() + offsetX;
        double shooterY = robotPose.y() + offsetY;

        // A projectile inherits both robot translation and velocity from rotation.
        double shooterVelocityX = robotVelocityX - robotAngularVelocity * offsetY;
        double shooterVelocityY = robotVelocityY + robotAngularVelocity * offsetX;

        double delay = shotType == ShooterSubsystem.ShotType.POLLEN
                ? Constants.BioBuzz.POLLEN_RELEASE_DELAY_SECONDS
                : Constants.BioBuzz.NECTAR_RELEASE_DELAY_SECONDS;
        double secondsPerInch = shotType == ShooterSubsystem.ShotType.POLLEN
                ? Constants.BioBuzz.POLLEN_SECONDS_PER_INCH
                : Constants.BioBuzz.NECTAR_SECONDS_PER_INCH;

        double flightTime = delay;
        double aimX = targetX - shooterX;
        double aimY = targetY - shooterY;
        double distance = Math.hypot(aimX, aimY);

        // Recalculate because flight time depends on the led distance.
        for (int i = 0; i < 3; i++) {
            flightTime = delay + distance * secondsPerInch;
            aimX = targetX - (shooterX + shooterVelocityX * flightTime);
            aimY = targetY - (shooterY + shooterVelocityY * flightTime);
            distance = Math.hypot(aimX, aimY);
        }

        double fieldAngle = Math.atan2(aimY, aimX);
        double turretAngle = normalizeRadians(fieldAngle - robotPose.heading());
        double shooterVelocity = shotType == ShooterSubsystem.ShotType.POLLEN
                ? Constants.BioBuzz.POLLEN_BASE_TICKS_PER_SECOND
                    + distance * Constants.BioBuzz.POLLEN_TICKS_PER_SECOND_PER_INCH
                : Constants.BioBuzz.NECTAR_BASE_TICKS_PER_SECOND
                    + distance * Constants.BioBuzz.NECTAR_TICKS_PER_SECOND_PER_INCH;

        return new AimSolution(
                targetX,
                targetY,
                distance,
                flightTime,
                fieldAngle,
                turretAngle,
                shooterVelocity
        );
    }

    private static double normalizeRadians(double angle) {
        while (angle > Math.PI) angle -= 2.0 * Math.PI;
        while (angle < -Math.PI) angle += 2.0 * Math.PI;
        return angle;
    }
}
