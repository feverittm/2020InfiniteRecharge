/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import org.team997coders.spartanlib.controllers.SpartanPID;
import org.team997coders.spartanlib.helpers.PIDConstants;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;

public class AutoDriveToDistance extends CommandBase {

  SpartanPID drivePidLoop, turnPidLoop;
  double target;
  double initAngle;
  long onTargetTime;

  public AutoDriveToDistance(double distance) { //in feets
    addRequirements(DriveTrain.getInstance());
    target = distance;
    drivePidLoop = new SpartanPID(new PIDConstants(Constants.Values.DRIVE_P, Constants.Values.DRIVE_I, Constants.Values.CLIMBER_D));
    turnPidLoop = new SpartanPID(new PIDConstants(Constants.Values.VISION_TURNING_P, Constants.Values.VISION_TURNING_I, Constants.Values.VISION_TURNING_D));
  }

  @Override
  public void initialize() {
    DriveTrain.getInstance().setBrake();
    initAngle = DriveTrain.getInstance().getGyroAngle();
    drivePidLoop.setSetpoint(target);
    turnPidLoop.setSetpoint(initAngle);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double forward = drivePidLoop.WhatShouldIDo(target - (DriveTrain.getInstance().getAverageEncoders() * Constants.Values.TICKS_TO_FEET), (Robot.getCurrentSeconds() * 1000));
    //double turn = turnPidLoop.WhatShouldIDo(initAngle - (DriveTrain.getInstance().getGyroAngle()), (Robot.getCurrentSeconds() * 1000));
    
    //DriveTrain.getInstance().setMotors(forward - turn, forward + turn);
    DriveTrain.getInstance().setMotors(forward, forward);

    if (Math.abs(target - (DriveTrain.getInstance().getAverageEncoders() * Constants.Values.TICKS_TO_FEET)) < Constants.Values.VISION_TOLERANCE) {
      onTargetTime++;
    } else {
      onTargetTime = 0;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return (onTargetTime >= 250);
  }
}
