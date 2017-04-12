package org.usfirst.frc.team4226.robot;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class Robot extends IterativeRobot {
	RobotDrive chassis;
	Talon climb;
	Joystick leftStick;
	Joystick rightStick;
	Joystick xbox,xbox2;
	AnalogInput sonar;
	DoubleSolenoid solenoid;
	DoubleSolenoid solenoid2;
	Relay relay;
	boolean runAuto;
	SendableChooser chooser;
	String vision,forwards;
	private double centerX;
	private VisionThread visionThread;
	private static final int IMG_WIDTH = 320;
	private static final int IMG_HEIGHT = 240;
	private final Object imgLock = new Object();
	
	public void robotInit() {	
		runAuto=true;
		chooser=new SendableChooser();
		sonar=new AnalogInput(0);
		chooser.addDefault("Vision", vision);
		chooser.addObject("Straight Forward", forwards);
		//SmartDashboard.putData("Select Auto",chooser);
		chassis=new RobotDrive(0,1,2,3);
		leftStick=new Joystick(0);
		rightStick=new Joystick(1);
		xbox=new Joystick(2);
		xbox2=new Joystick(3);
		climb=new Talon(4);
		solenoid=new DoubleSolenoid(2,3);
		solenoid2=new DoubleSolenoid(4,5);
		relay=new Relay(1);
	    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
	    camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
	    camera.setExposureManual(50);
	    camera.setBrightness(5);
	    visionThread = new VisionThread(camera, new Pipeline(), pipeline -> {
	        if (!pipeline.filterContoursOutput().isEmpty()) {
	            Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
	            synchronized (imgLock) {
	                centerX = r.x + (r.width / 2);
	            }
	        }
	    });
	    visionThread.start();

	}
 
	public void autonomousInit() {

	}


	public void autonomousPeriodic() {
		vision="vision";
		forwards="forwards";
		//if(chooser.getSelected()=="forwards"){
			while(isEnabled()&&isAutonomous()&&runAuto){
				chassis.setSafetyEnabled(false);
				chassis.drive(0.4,-.05);
				Timer.delay(5);
				chassis.drive(0,0);
				Timer.delay(0.1);
				runAuto=false;
				
			}
		//}
	}


	public void teleopPeriodic() {
		while(isEnabled()&&isOperatorControl()){
			SmartDashboard.putString("Sonar",""+sonar.getValue());
			//chassis.tankDrive(leftStick.getY()*-1, rightStick.getY()*-1, true);	
			chassis.tankDrive(xbox.getRawAxis(1)*-2, xbox.getRawAxis(5)*-2,true);
			if(xbox.getRawButton(6)){
				solenoid.set(DoubleSolenoid.Value.kForward);
				solenoid2.set(DoubleSolenoid.Value.kReverse);
			}
			else if(xbox.getRawButton(5)){
				solenoid.set(DoubleSolenoid.Value.kReverse);
				solenoid2.set(DoubleSolenoid.Value.kForward);
			}
			else if(xbox2.getRawButton(6)){
				solenoid.set(DoubleSolenoid.Value.kForward);
				solenoid2.set(DoubleSolenoid.Value.kReverse);
			}
			else if(xbox2.getRawButton(5)){
				solenoid.set(DoubleSolenoid.Value.kReverse);
				solenoid2.set(DoubleSolenoid.Value.kForward);
			}
			if(xbox.getRawButton(1)){
				climb.set(1);
			}
			else if(xbox.getRawButton(2)){
				climb.set(-1);
			}
			else if(xbox2.getRawButton(1)){
				climb.set(1);
			}
			else if(xbox2.getRawButton(2)){
				climb.set(-1);
			}
			else{
				climb.set(0);
			}
		}
	}


	public void testPeriodic() {

	}
}

