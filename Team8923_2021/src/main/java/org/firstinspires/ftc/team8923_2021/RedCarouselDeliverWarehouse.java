package org.firstinspires.ftc.team8923_2021;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "RedCarouselDeliverWarehouse")
public class RedCarouselDeliverWarehouse extends MasterAutonomous {
    public void runOpMode() throws InterruptedException {
        initAuto();
        waitForStart();
        double referenceAngle = imu.getAngularOrientation().firstAngle;
        imuPivot(referenceAngle, 30, 35, 0.015, 3.0);
        moveForward(-3.9, 10, 10);
        spinCarouselRed();
        moveForward(35.0, 10, 10);
        imuPivot(referenceAngle, 180, 35, 0.015, 3.0);
        imuPivot(referenceAngle, -20, 35, 0.015, 3.0);
        autoDeliver();
    }
}