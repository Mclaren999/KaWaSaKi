# KaWaSaKi hardware configuration and setup

Return to the [team README](../README.md).

## Development environment

Clone `https://github.com/Mclaren999/KaWaSaKi.git` and open the project root in Android Studio. Let Gradle sync before deploying the TeamCode app to the Robot Controller.

The repository's upstream README specifies Android Studio Narwhal 3 Feature Drop or later. `build.dependencies.gradle` declares FTC SDK 12.0.0, Pedro revhub 3.0.1, and Pedro tuning 1.0.1. `build.common.gradle` sets compile SDK 34 and Java 8 source/target compatibility.

Use the [FIRST Android Studio documentation](https://ftc-docs.firstinspires.org/programming_resources/android_studio_java/Android-Studio-Tutorial.html) for the controller connection and deployment workflow. No successful build or deployment is asserted by this guide.

## Hardware map

Names are case-sensitive. Assign devices to the real ports on your robot; the repository does not specify port numbers.

| Configuration name | Device used by the code | Purpose |
| --- | --- | --- |
| `leftFront` | Drive motor | Front left mecanum wheel |
| `leftRear` | Drive motor | Back left mecanum wheel |
| `rightFront` | Drive motor | Front right mecanum wheel |
| `rightRear` | Drive motor | Back right mecanum wheel |
| `pinpoint` | goBILDA Pinpoint localizer | Odometry |
| `intake` | DcMotorEx | Intake |
| `ramp` | Servo | Intake ramp |
| `bigShooter` | DcMotorEx | NECTAR flywheel |
| `smallShooter` | DcMotorEx | POLLEN flywheel |
| `bigGate` | Servo | Large shooter release gate |
| `smallGate` | Servo | Small shooter release gate |
| `turret` | DcMotorEx | Motor for the mechanically linked turrets |

The active drivetrain configuration is in [pedro/Constants.java](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/Constants.java). It uses reversed left motors, forward right motors, and the goBILDA 4-bar odometry-pod preset. Confirm those choices against your robot. The similarly named fields in the top-level `Constants.Drive` class do not configure the Pedro follower.

## Calibration order

1. **Drive and localization:** check motor directions, pod type, pod directions, units, and physical pod offsets. Both Pinpoint offsets currently equal zero and are labelled temporary.
2. **Pedro tuning:** use the procedures exposed in `pedro/Tuning.java` to measure the robot. Replace the complete starter Foresight block with configuration generated for your robot.
3. **Reference pose:** measure `R3_RESET_X`, `R3_RESET_Y`, and `R3_RESET_HEADING_RADIANS` in the field frame used by your setup. Press R3 only when the robot is at that known pose. It assigns a pose; it does not locate the robot automatically.
4. **Turret:** point the mechanism at its zero direction before INIT, because initialization resets its encoder. Measure ticks per radian, zero offset, travel limits, direction, and aiming tolerance.
5. **Shooter and gates:** measure safe servo endpoints, flywheel directions, useful velocity ranges, and readiness tolerances.
6. **Targeting:** set the intended alliance/cell, measure field targets and turret-axis offsets, then fit release delay, flight time per inch, and flywheel velocity against measured shots.

Top-level robot calibration lives in [Constants.java](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Constants.java). Intake and gate endpoints also live in their subsystem files.

## First controlled run

Use a clear test area with a stop operator. Verify directions and turret travel before loading game pieces. The scoring update commands both flywheels during the TeleOp loop, even when the fire trigger is released. Releasing the trigger closes the gates; it does not stop the flywheels.

Select **KaWaSaKi BIOBUZZ Auto** in the Driver Station TeleOp list. Check the pose and reference reset, then turret tracking and flywheel telemetry. Request release only after verifying calibration. The readiness flag checks encoder angle and velocity tolerances; it is not independent confirmation that the physical shot is safe or accurate.

`PoseStorage.currentPose` initially equals zero and persists only in memory. A Robot Controller process restart loses it. There is no team-specific autonomous OpMode in this snapshot to establish a starting pose.

For another team's robot, reuse the structure and calculation only after adapting dimensions, hardware, target coordinates, and calibration. Record actual measurements rather than copying starter values.
