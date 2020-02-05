package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Constants;

public class DriveTrain implements Subsystem {

  private double lastLeft = 0.0, lastRight = 0.0,
    deltaT = 0.0, lastUpdate = 0.0, acceleration = 0.1;

  private TalonFX frontLeft;
  private TalonFX frontRight;
  private TalonFX backLeft;
  private TalonFX backRight;

  private AHRS imu;

  private AnalogInput ultrasonic;

  private DriveTrain() {

    System.out.println("AHAHAHAHAHAHHAHAHAHAHAHAHAHHAHAHAHAHAHAHHAAHA");
    SupplyCurrentLimitConfiguration currentLimitConfig = new SupplyCurrentLimitConfiguration(true, 40, 50, 0.1);

    ultrasonic = new AnalogInput(Constants.Ports.ultrasonicChannel);
    imu = new AHRS(Port.kUSB);

    frontLeft = new TalonFX(Constants.Ports.motorFrontLeft);
    frontRight = new TalonFX(Constants.Ports.motorFrontRight);
    backLeft = new TalonFX(Constants.Ports.motorBackLeft);
    backRight = new TalonFX(Constants.Ports.motorBackRight);

    frontLeft.configSupplyCurrentLimit(currentLimitConfig, 10);
    frontRight.configSupplyCurrentLimit(currentLimitConfig, 10);
    backLeft.configSupplyCurrentLimit(currentLimitConfig, 10);
    backRight.configSupplyCurrentLimit(currentLimitConfig, 10);

    frontLeft.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 10);
    frontLeft.setSelectedSensorPosition(0, 0, 10);
    frontRight.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 10);
    frontRight.setSelectedSensorPosition(0, 0, 10);

    backLeft.follow(frontLeft);
    backRight.follow(frontRight);

    lastUpdate = System.currentTimeMillis() / 1000.0;

  }

  public void setMotors(double leftSpeed, double rightSpeed) {
    System.out.println("4444444444444444444444444444444444444444444444444");
    frontLeft.set(ControlMode.PercentOutput, -leftSpeed);
    frontRight.set(ControlMode.PercentOutput, rightSpeed);
  }

  public void simpleAccelControl(double left, double right) {
    double maxAdjust = acceleration * deltaT;
    if (Math.abs(left) > Math.abs(lastLeft) + maxAdjust) {
      int sign = (int)(Math.abs(lastLeft - left) / (left - lastLeft));
      left += maxAdjust * sign;
    }
    if (Math.abs(left) > Math.abs(lastLeft) + maxAdjust) {
      int sign = (int)(Math.abs(lastLeft - left) / (left - lastLeft));
      left += maxAdjust * sign;
    }

    lastLeft = left;
    lastRight = right;
  }

  public void accelerateMotors(double leftSpeed, double rightSpeed, double deltaT) {
    System.out.println("99999999999999999999999999999999999999999999");
    
    
    deltaT /= 60000;
    double adjustment = Constants.Values.acceleration * deltaT;
    System.out.println("1111111111111111111111111111111111111111");
    final double leftcurrent = frontLeft.getSelectedSensorVelocity(0);
    final double rightCurrent = frontRight.getSelectedSensorVelocity(0);
    System.out.println("7777777777777777777777777777777777777");
    /*
    if (Math.abs(leftSpeed - leftcurrent) > adjustment){
      System.out.println("----------------------------- " + leftSpeed);
      System.out.println("+++++++++++++++++++++++++++++++++++  " + leftcurrent);
      double error = leftcurrent - leftSpeed;
      double sign = Math.abs(error)/error;
      double output = (leftcurrent + adjustment)*sign;

      System.out.println("thiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" + "" + output);
      frontLeft.set(ControlMode.PercentOutput, output);
    }else {
      System.out.println("33333333333333333333333333333333333333333");
      frontLeft.set(ControlMode.PercentOutput, leftSpeed);
    }
*/
    
    if (leftSpeed > frontLeft.getMotorOutputPercent()) {
      double output = leftcurrent + (adjustment);
      frontLeft.set(ControlMode.PercentOutput, output);
    } else if (leftSpeed < frontLeft.getMotorOutputPercent() ) {
     frontLeft.set(ControlMode.PercentOutput, frontLeft.getMotorOutputPercent() - Constants.Values.acceleration);
    } else {
      frontLeft.set(ControlMode.PercentOutput, leftSpeed);
    }

    //if (rightSpeed > frontRight.getMotorOutputPercent()) {
      //frontRight.set(ControlMode.PercentOutput, frontRight.getMotorOutputPercent() + Constants.Values.acceleration);
    //} else if (rightSpeed < frontRight.getMotorOutputPercent()) {
      //frontRight.set(ControlMode.PercentOutput, frontRight.getMotorOutputPercent() - Constants.Values.acceleration);
    //} else {
     // frontRight.set(ControlMode.PercentOutput, rightSpeed);
    ///}

  }

  public double getLeftSensor() {
    return frontLeft.getSelectedSensorPosition(0);
  }

  public double getRightSensor() {
    return frontRight.getSelectedSensorPosition(0);
  }

  public double getGyroAngle() {
    return imu.getAngle();
  }

  @Override
  public void periodic() {

    double currentTime = System.currentTimeMillis() / 1000.0;
    deltaT = currentTime - lastUpdate;
    lastUpdate = currentTime;

    SmartDashboard.putNumber("DriveTrain/Right Motors Position", getRightSensor());
    SmartDashboard.putNumber("DriveTrain/Left Motors Position", getLeftSensor());

    SmartDashboard.putNumber("DriveTrain/Left Motor Velocity", frontLeft.getSelectedSensorVelocity(0));
    SmartDashboard.putNumber("DriveTrain/Right Motors Velocity", frontRight.getSelectedSensorVelocity(0));

    SmartDashboard.putNumber("DriveTrain/Front Left Motor Temperature", frontLeft.getTemperature());
    SmartDashboard.putNumber("DriveTrain/Front Right Motor Temperature", frontLeft.getTemperature());
    SmartDashboard.putNumber("DriveTrain/Back Left Motor Temperature", frontLeft.getTemperature());
    SmartDashboard.putNumber("DriveTrain/Back Right Motor Temperature", frontLeft.getTemperature());

    SmartDashboard.putNumber("DriveTrain/Gyro", getGyroAngle());
    SmartDashboard.putNumber("DriveTrain/Ultrasonic", ultrasonic.getVoltage() / Constants.Values.voltageToFeet); // displays
                                                                                                                 // feet
                                                                                                                 // from
                                                                                                                 // target.
  }

  private static DriveTrain instance;

  public static DriveTrain getInstance() {
    if (instance == null) {
      instance = new DriveTrain();
      System.out.println("Inited========================================================================");
    }
    return instance;
  }
}