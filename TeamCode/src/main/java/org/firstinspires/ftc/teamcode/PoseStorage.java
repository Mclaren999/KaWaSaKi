package org.firstinspires.ftc.teamcode;

import com.pedropathing.math.Pose;

/**
 * Shares the global Pedro pose between Autonomous and TeleOp while the Robot
 * Controller process remains alive. Autonomous should update currentPose in
 * stop(), and TeleOp starts from the saved value.
 */
public final class PoseStorage {

    public static Pose currentPose = Pose.zero();

    private PoseStorage() {
    }
}
