package frc.robot;

import java.util.ArrayList;


import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj.smartdashboard.*;
import frc.robot.commands.auto.*;
import frc.robot.commands.hopper.*;

import frc.robot.subsystems.*;

public class Robot extends TimedRobot {

  private static double lastUpdate = 0.0;
  public static double initAngle = 0.0;
  private ArrayList<String> commandList;

  public static long cycles = 0;
  public static final boolean verbose = false; //debug variable, set to true for command logging in the console and non-essential smartdashboard bits.

  private Command m_autonomousCommand;
  private Command mHopperCommand;
  public static boolean autoLoadHopper = false;

  Command autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  
  @Override
  public void robotInit() {

    // CameraServer.getInstance().startAutomaticCapture(0);
    CommandScheduler.getInstance().cancelAll();

    m_chooser.setDefaultOption("Do Nothing", new AutoDoNothing());

    Shooter.getInstance();
    Hopper.getInstance();
    Climber.getInstance();

    OI.getInstance();

    commandList = new ArrayList<String>();
    if (verbose) {
      CommandScheduler.getInstance().onCommandExecute(command -> {
        commandList.add(command.getName());
      });
    }

    SmartDashboard.putData(m_chooser);
    SmartDashboard.putNumber("Driver/Set Initial Angle", 0.0); // set the init angle of the robot in disabled with this. 0 is straight forwards.

    mHopperCommand = new HopperAutoIndex();
  }

  @Override
  public void robotPeriodic() {
    
    if (verbose) {
      if (commandList.size() > 0) {
        for (String cmdName : commandList) {
          System.out.println("Ran " + cmdName + " on cycle " + cycles);
        }
      }
    }
    commandList.clear();
    cycles++;

    lastUpdate = getCurrentSeconds();

    OI.getInstance().update();
  }

  @Override
  public void disabledInit() {

    Intake.getInstance().setPiston(false);

    if (mHopperCommand != null) mHopperCommand.cancel();
    mHopperCommand = new HopperAutoIndex();

  }

  @Override
  public void disabledPeriodic() {
    CommandScheduler.getInstance().run();

    Hopper.getInstance().updateBallCount();
    initAngle = NetworkTableInstance.getDefault().getTable("SmartDashboard").getEntry("Driver/Set Init Angle").getDouble(0.0);
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

    mHopperCommand.schedule();
  }

  @Override
  public void teleopPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() { }

  public static double getDeltaT() { return getCurrentSeconds() - lastUpdate; }

  public static double getCurrentSeconds() { return System.currentTimeMillis() / 1000.0; }

}
