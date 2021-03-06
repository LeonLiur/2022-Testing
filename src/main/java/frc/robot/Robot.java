// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or the package after creating this project, you must also
 * update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer. This will perform all our button bindings,
    // and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and
   * test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled
    // commands, running already-scheduled commands, removing finished or
    // interrupted commands,
    // and running subsystem periodic() methods. This must be called from the
    // robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your
   * {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  // returns the distance (hypotenuse) from the robot
  private double getDistance(double ty, double height){
    ty = Math.toRadians(ty);
    try{
      ty %= 360;
      return height / Math.sin(ty);
    }catch(ArithmeticException e){
      System.out.println("Caught arithmetic exception");
      return height / Math.sin(ty + 1);
    }
  }

  private double getDistWithActFormula(double ty, double height){
    try{
      return height/Math.tan(ty);
    }catch(ArithmeticException e){
      System.out.println("bad");
      return height/Math.tan(ty+0.01);
    }
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  // initialize previous X, previous Y, and previous area
  double prevX = 0;
  double prevY = 0;
  double prevArea = 0;
  double prevHor = 0;
  double preVert = 0;
  int xFlucCount = 0, yFlucCount = 0, areaFlucCount = 0, vertFlucCount = 0, horFlucCount = 0;
  final double EPSILON = 1;

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // testing code for limelight here:
    // displaying reading of limelight
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tv = table.getEntry("tv");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tHor = table.getEntry("thor");
    NetworkTableEntry tVert = table.getEntry("tvert");


    // read values periodically
    double v = tv.getDouble(0.0);
    double x = tx.getDouble(0.0);
    // x = Math.round(x * 100) / 100;
    double y = ty.getDouble(0.0);
    // y = Math.round(y * 100) / 100;
    double area = ta.getDouble(0.0);
    double hor = tHor.getDouble(0.0);
    double vert = tVert.getDouble(0.0);


    if(Math.abs(x - prevX) >= EPSILON){
      xFlucCount ++;
    }
    if(Math.abs(y - prevY) >= EPSILON){
      yFlucCount ++;
    }
    if(Math.abs(area - prevArea) >= Math.sqrt(EPSILON)){
      areaFlucCount ++;
    }
    if(Math.abs(hor - prevHor) >= EPSILON){
      horFlucCount ++;
    }
    if(Math.abs(vert - preVert) >= EPSILON){
      vertFlucCount ++;
    }

    System.out.printf("x fluctuate count: %s, y fluctuate: %s," +  
    "area fluctuate: %s hor: %s, vert: %s\n",
    xFlucCount, yFlucCount, areaFlucCount, horFlucCount, vertFlucCount);
    
    prevX = x;
    prevY = y;
    prevArea = area;
    prevHor = hor;
    preVert = vert;

    // post to smart dashboard periodically
    SmartDashboard.putNumber("Valid Targets", v);
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
    SmartDashboard.putNumber("hor", hor);
    SmartDashboard.putNumber("vert", vert);

    // table.getEntry("ledMode").setNumber(2);
    // // if target detected
    // if(v != 0){
    //   System.out.printf("the distance found is %s\n", getDistance(y, 5.1));
    //   // System.out.printf("actual distance: %s\n", getDistWithActFormula(y, 5.1));
    //   SmartDashboard.putNumber("Dist", getDistance(y, 5.1));
    // }

  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }
}
