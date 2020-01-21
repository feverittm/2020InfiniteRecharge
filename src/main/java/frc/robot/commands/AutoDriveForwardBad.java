/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import java.lang.module.ModuleDescriptor.Requires;

import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.subsystems.DriveTrain;

public class AutoDriveForwardBad extends CommandBase {
  /**
   * Creates a new AutoDoNothing.
   */
    
    private double rightEncoderTarget = 0;
    private double leftEcncoderTarget = 0;
    private double errorMargin = 0;

public AutoDriveForwardBad(double leftEncoderTarget, double rightEncoderTarget, double errorMargin) {
    // Use addRequirements() here to declare subsystem dependencies.
		addRequirements(DriveTrain.getInstance());
		this.leftEcncoderTarget = leftEncoderTarget;
		this.rightEncoderTarget = rightEncoderTarget;
		this.errorMargin = errorMargin;
  }
public double calculateError(double current, double target){
    return (current - target);
}


  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    DriveTrain.getInstance().resetEncoders();
		DriveTrain.getInstance().resetGyroAngle();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double current = DriveTrain.getInstance().getGyroAngle();
    double error = targetAngle-current;
    double positionAdjustment = error*0.00277; //100 divided by 360 divided by 100
    double currentLeftPosition = DriveTrain.getInstance().getLeftSensor();
    double currentRightPosition = DriveTrain.getInstance().getRightSensor();

    double leftPosition = (currentLeftPosition- positionAdjustment);
    double rightPosition = (currentRightPosition + positionAdjustment);
    DriveTrain.getInstance().setPosition(leftPosition, rightPosition, 0);
		}
  
  

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    boolean onLeftTarget = (Math.abs(calculateError(DriveTrain.getInstance().getLeftSensor(), leftEcncoderTarget))) < errorMargin;
    boolean onRightTarget = (Math.abs(calculateError(DriveTrain.getInstance().getRightSensor(), rightEncoderTarget))) < errorMargin;
		boolean onAngleTarget = (Math.abs(calculateError(DriveTrain.getInstance().getGyroAngle(), 0))) < errorMargin;
    return (onLeftTarget && onRightTarget && onAngleTarget);
  }
}