package Roboxer;
import robocode.*;
import robocode.util.Utils ;
//import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Atom - a robot by (Joao Victor)
 */
public class Atom extends AdvancedRobot
{
	/**
	 * run: Atom's default behavior
	 */
	public void run() {
		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		setTurnGunRight(Double.POSITIVE_INFINITY);
		int counter = 0;

		while(true) {
			if (getRadarTurnRemaining() == 0.0) {
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			}
			if (counter < 16){
				setAhead(100);
			} else{
				setBack(100);
				counter = (counter + 1) % 32;
				execute();
			}
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
		double turnToEnemy = Utils.normalRelativeAngle(angleToEnemy - getRadarHeadingRadians());
		double extraTurn = Math.atan(36.0 / e.getDistance()) * (turnToEnemy >= 0 ? 1 : -1);		
		
		setTurnRadarRightRadians(turnToEnemy + extraTurn);
		setTurnRight(e.getBearing() + 90);
		//setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
		shoot(e);
	}
	
	public void shoot(ScannedRobotEvent e) {
		double absoluteBearing = e.getBearingRadians() + getHeadingRadians();
		double gunTurn = absoluteBearing - getGunHeadingRadians();
		double firePower = decideFirePower(e);
		double future = e.getVelocity() * Math.sin(e.getHeadingRadians() - absoluteBearing) / Rules.getBulletSpeed(firePower);
		setTurnGunRightRadians(Utils.normalRelativeAngle(gunTurn + future));
		setFire(firePower);

	}
	
	public double decideFirePower(ScannedRobotEvent e){
		double firePower = getOthers() == 1 ? 2.0 : 3.0;
		
		if (e.getDistance() > 400) {
			firePower = 1.0;
		} else if (e.getDistance() < 200) {
			firePower = 3.0;
		}
		
		if (getEnergy() < 1) {
			firePower = 0.1;
		} else if (getEnergy() < 10) {
			firePower = 1.0;
		}
		return Math.min(e.getEnergy() / 4, firePower);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
