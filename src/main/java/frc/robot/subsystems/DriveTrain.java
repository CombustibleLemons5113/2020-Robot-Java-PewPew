/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
//import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.ChassisConstants.*;

public class DriveTrain extends SubsystemBase {
  /**
   * Creates a new DriveTrain.
   */

  private WPI_TalonFX leftMaster = new WPI_TalonFX(kFLChassis);
  private WPI_TalonFX rightMaster = new WPI_TalonFX(kFRChassis);
  private WPI_TalonFX leftSlave = new WPI_TalonFX(kBLChassis);
  private WPI_TalonFX rightSlave = new WPI_TalonFX(kBRChassis);

  private SpeedControllerGroup leftSide = new SpeedControllerGroup(leftMaster, leftSlave); //SpeedControllerGroup allows for multiple SpeedControllers to be linked together
  private SpeedControllerGroup rightSide = new SpeedControllerGroup(rightMaster, rightSlave);

  private DifferentialDrive driveBase = new DifferentialDrive(leftMaster, rightMaster); //allows for us to 
  //private AHRS gyro = new AHRS(SPI.Port.kMXP); //we might need to set the update rate to 60 hz

  //private DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(getHeading());

  private SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(kSChassis, kVChassis, kAChassis);
  
  //private double kp = 0.001;
  //private double ki = 0.00001;
  //private double kd = 0.1;
  
  //private double setPoint = 100;
  
  //private PIDController pid = new edu.wpi.first.wpilibj.controller.PIDController(kp, ki, kd);

  public DriveTrain() {

    leftMaster.configFactoryDefault();
    leftSlave.configFactoryDefault();
    rightMaster.configFactoryDefault();
    rightSlave.configFactoryDefault();

    
    leftSlave.set(ControlMode.Follower, leftMaster.getDeviceID());
    rightSlave.set(ControlMode.Follower, rightMaster.getDeviceID());  
    
    //leftMaster.config_kP(1, 2, 30);
    //leftMaster.config_kI(1, 0.001, 30);
    //leftMaster.config_kD(1, 0, 30);

    leftMaster.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 30);
    rightMaster.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 30);
    leftSlave.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 30);
    rightSlave.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 30);

    leftMaster.setNeutralMode(NeutralMode.Brake);
    rightMaster.setNeutralMode(NeutralMode.Brake);
    leftSlave.setNeutralMode(NeutralMode.Brake);
    rightSlave.setNeutralMode(NeutralMode.Brake);
    
    //rightMaster.configClosedloopRamp(10);
    //leftMaster.configClosedloopRamp(10);
    //rightMaster.configOpenloopRamp(10);
    //leftMaster.configOpenloopRamp(10);

    //gyro.reset();

   // this.setMaxVoltage(0.5);

    
    //gyro.enableLogging(true);
   // System.out.println(gyro.getFirmwareVersion());
    //gyro.isCalibrating();
    //System.out.println(gyro.isRotating());

    //pid.setSetpoint(setPoint);
    //leftMaster.set(ControlMode.Velocity, 2000);

  }

  public void resetEncoders() {
    rightMaster.setSelectedSensorPosition(0);
    rightSlave.setSelectedSensorPosition(0);
    leftMaster.setSelectedSensorPosition(0);
    leftSlave.setSelectedSensorPosition(0);
  }

  //public Rotation2d getHeading() {
  //  return Rotation2d.fromDegrees(Math.IEEEremainder(gyro.getAngle(), 360));
  //}

  //public Pose2d getPose() {
  //  return odometry.getPoseMeters();
  //}

  //public void resetOdometry(Pose2d pose) {
 //   odometry.resetPosition(pose, getHeading());
  //}

  public DifferentialDriveWheelSpeeds getWheelSpeeds() 
  {
    return (new DifferentialDriveWheelSpeeds(
      leftMaster.getSelectedSensorVelocity() / 7.29 * 2 * Math.PI * Units.inchesToMeters(4), 
      rightMaster.getSelectedSensorVelocity() / 7.29 * 2 * Math.PI * Units.inchesToMeters(4))); // change the wheel radius
  }

  public SimpleMotorFeedforward getFeedForward() {
    return feedforward;
  }

  public double getAverageEncoderDistance() {
    return (rightMaster.getSelectedSensorPosition()+leftMaster.getSelectedSensorPosition())/2;
  }

  public void setMaxVoltage(double max) {
    driveBase.setMaxOutput(12.3);
  }

  public double getTurnRate() {
    return 0;//gyro.getRate();
  }

  public void driveCartesian(double left, double right) {

    
    leftMaster.set(ControlMode.PercentOutput, left);
    rightMaster.set(ControlMode.PercentOutput, -right);

    //SmartDashboard.putNumber("left", left);
    //SmartDashboard.putNumber("right", right);
    SmartDashboard.putNumber("rightFront", rightMaster.getSupplyCurrent());
    SmartDashboard.putNumber("leftFront", leftMaster.getSupplyCurrent());
    SmartDashboard.putNumber("rightBack", rightSlave.getSupplyCurrent());
    SmartDashboard.putNumber("leftBack", leftSlave.getSupplyCurrent());

  }

  public void tankDriveVolts(double leftVolts, double rightVolts) {
    leftSide.setVoltage(rightVolts);
    rightSide.setVoltage(-leftVolts);
    driveBase.feed();
  }

  public void setLeftSpeed() 
  {
    //leftMaster.setVoltage(pid.calculate(getLeftRPM(),setPoint) + feedforward.calculate(setPoint));
    //System.out.println(leftMaster.getSelectedSensorVelocity(0));
    //System.out.println(leftMaster.getSupplyCurrent());
    
  }

  
  public double getLeftRPM() 
  {
    return leftMaster.getSelectedSensorVelocity();
  }
  public double getRightRPM() 
  {
    return rightMaster.getSelectedSensorVelocity();
  }

  //@Override
  //public void periodic() {
  //  odometry.update(getHeading(), leftMaster.getSelectedSensorPosition(), rightMaster.getSelectedSensorPosition());
  //}

}
