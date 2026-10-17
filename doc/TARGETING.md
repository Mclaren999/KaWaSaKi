# How KaWaSaKi estimates a moving shot

Return to the [team README](../README.md) or [setup guide](TEAM_SETUP.md).

The calculation is in [BioBuzzTargeting.java](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems/BioBuzzTargeting.java). It accepts a Pedro pose, field-frame X/Y velocity, angular velocity, alliance, cell, and shot type. It returns target coordinates, compensated distance, estimated time, field aiming angle, robot-relative turret angle, and flywheel velocity.

## Units and frame

Use inches for distances, inches per second for translation, radians for angles, radians per second for angular velocity, and encoder ticks per second for flywheel commands. The top-level constants describe a field frame with its origin at the bottom-left. Verify its orientation and your localization setup against your actual field.

The target comes from the configured alliance and cell. Those constants are application configuration, not a substitute for measuring the field.

## Geometry

Let the robot pose be `(x, y, h)`, and the turret-axis offset in robot coordinates be `(f, l)`, meaning forward and left.

Rotate the offset into the field frame:

```text
offsetX = f*cos(h) - l*sin(h)
offsetY = f*sin(h) + l*cos(h)
shooterX = x + offsetX
shooterY = y + offsetY
```

Account for translation and tangential velocity at that offset:

```text
shooterVx = robotVx - angularVelocity*offsetY
shooterVy = robotVy + angularVelocity*offsetX
```

Starting with the direct distance, the code performs three iterations:

```text
flightTime = releaseDelay + distance*secondsPerInch
aimX = targetX - (shooterX + shooterVx*flightTime)
aimY = targetY - (shooterY + shooterVy*flightTime)
distance = hypot(aimX, aimY)
```

It then uses `atan2(aimY, aimX)` for the field angle and subtracts robot heading to obtain the turret angle, normalized to [-pi, pi]. Flywheel velocity is a linear fit:

```text
velocity = baseTicksPerSecond + distance*ticksPerSecondPerInch
```

POLLEN and NECTAR have separate timing and velocity coefficients. The returned `flightTime` is the value computed at the beginning of the final iteration; it is not recomputed after that iteration changes the distance.

## Connecting the calculation to the robot

[BioBuzzScoringSubsystem](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems/BioBuzzScoringSubsystem.java) calculates a solution for each shot type. Because the turrets are mechanically linked, it averages their directions using summed sine and cosine components. Each flywheel receives its own velocity command.

A fire request opens both gates only when the turret reports that it is aimed and both flywheels are within their configured velocity tolerances. With no fire request, tracking and flywheel commands continue but the gates stay closed.

## Assumptions and limits

This is an empirical lead model. It assumes constant shooter-point velocity over the estimated release/flight interval. It does not model acceleration, gravity, drag, mechanism flex, or measured shot dispersion. It has no convergence test beyond the fixed three iterations.

The solver does not validate a physical firing envelope. The turret subsystem normalizes and clamps its target to configured limits, which can differ from the requested geometric direction. A readiness flag therefore needs to be interpreted alongside travel limits and actual calibration.

Before adapting the model, measure offsets and coefficients on your robot. Useful future numerical checks include zero velocity, heading wraparound, both alliances/cells, nonzero pivot offsets, rotation in both directions, and shots near travel limits. Hardware checks should compare stationary and moving shots with recorded distances, velocities, and misses. These checks are proposed work; this documentation does not claim they have passed.
