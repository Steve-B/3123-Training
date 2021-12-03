package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.mechanism.Carousel;
import org.firstinspires.ftc.teamcode.mechanism.Color;
import org.firstinspires.ftc.teamcode.mechanism.Hopper;
import org.firstinspires.ftc.teamcode.mechanism.Lift;
import org.firstinspires.ftc.teamcode.mechanism.chassis.MecanumChassis;
import org.firstinspires.ftc.teamcode.opencv.ShippingElementRecognizer;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "Auto (Red)", group = "Sensor")
public class FullAutoRed extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private MecanumChassis chassis = new MecanumChassis();
    private Carousel carousel = new Carousel(Color.RED);
    private Lift lift = new Lift();
    private Hopper hopper = new Hopper();
    OpenCvWebcam webcam;

    public void runOpMode() {
        chassis.init(hardwareMap);
        carousel.init(hardwareMap);
        lift.init(hardwareMap);
        hopper.init(hardwareMap);

        // Camera things
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);
        ShippingElementRecognizer pipeline = new ShippingElementRecognizer();
        webcam.setPipeline(pipeline);
        webcam.setMillisecondsPermissionTimeout(2500);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.SIDEWAYS_LEFT);
            }

            @Override
            public void onError(int errorCode) {
                // This will be called if the camera could not be opened
            }
        });

        waitForStart();
        // Start button is pressed

        // Get the placement of the shipping element 100 times and pick the most frequent position
        int level;
        int[] counts = {0,0,0};
        for(int i=0;i<50;i++) {
            delay(10);
            if(pipeline.getShippingHubLevel() == 0) {
                i = 0;
                continue;
            }
            counts[pipeline.getShippingHubLevel() - 1]++;
        }
        if(counts[0] > counts[1] && counts[0] > counts[2]) {
            level = 1;
        } else if(counts[1] > counts[0] && counts[1] > counts[2]) {
            level = 2;
        } else {
            level = 3;
        }
        telemetry.addData("Shipping Hub Level", level);
        telemetry.update();

        // Drive to the the shipping hub
        chassis.moveBackwardWithEncoders(0.6,100);
        delay(250);
        chassis.strafeLeftWithEncoders(0.6,1050);
        delay(250);
        chassis.moveBackwardWithEncoders(0.6,650);

        // Deposit the box on the correct level
        if(level == 1) {
            lift.goTo(450,0.8);
            delay(300);
        } else if (level == 2) {
            lift.goTo(900,0.8);
            delay(400);
        } else {
            lift.goTo(1450, 0.8);
            delay(500);
        }
        hopper.hopper.setPosition(0.33);
        delay(1200);
        hopper.hopper.setPosition(0);
        delay(200);
        lift.goTo(0,0.8);

        // Move to the carousel and spin it
        chassis.moveForwardWithEncoders(0.6,600);
        chassis.turnRightWithEncoders(0.6,1500);
        delay(250);
        chassis.moveBackwardWithEncoders(0.6,700);
        chassis.moveBackwardWithEncoders(0.3,200);
        chassis.moveForwardWithEncoders(0.5,100);
        chassis.strafeLeftWithEncoders(0.6,1650);
        chassis.moveBackwardWithEncoders(0.3,400);
        chassis.moveForwardWithEncoders(0.6,100);
        chassis.strafeLeftWithEncoders(0.3,300);
        carousel.turnCarousel();
        delay(3000);
        carousel.carouselMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        carousel.carouselMotor.setPower(0);

        // Drive into the warehouse
        chassis.moveForwardWithEncoders(0.6,250);
        chassis.turnRightWithEncoders(0.6,750);
        chassis.moveBackwardWithEncoders(0.3,500);
        chassis.moveForwardWithEncoders(1, 5500);
    }

    public void delay(int time) {
        double startTime = runtime.milliseconds();
        while (runtime.milliseconds() - startTime < time) {
        }
    }
}
