package Roboxer;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import robocode.util.*;

public class Atom extends RateControlRobot {
    /**
     * run: executado quando o round for iniciado
     */
    public void run() {
        setColors(Color.red, Color.black, Color.red); //cor do robo
        // Definindo posição inicial e direção do robo
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        // Dando vida ao robo em um loop infinito
        while (true) {
            setVelocityRate(5);
            setTurnRateRadians(0);
            execute();
            turnRadarRight(360);
        }
    }
    /**
     * onScannedRobot: Executado quando o radar encontra um robo.
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        double forcaTiro = Math.min(2.0, getEnergy());
        double distancia = getHeadingRadians() + e.getBearingRadians();
        double posX = getX() + e.getDistance() * Math.sin(distancia);
        double posY = getY() + e.getDistance() * Math.cos(distancia);
        double posDoInimigo = e.getHeadingRadians();
        double veloDoInimigo = e.getVelocity();
        double alturaDaArena = getBattleFieldHeight(), larguraDaArena = getBattleFieldWidth();
        double prevX = posX, prevY = posY;
        
        prevX += Math.sin(posDoInimigo) * veloDoInimigo;
        prevY += Math.cos(posDoInimigo) * veloDoInimigo;
        if (prevX < 18.0 || prevY < 18.0 || prevX > larguraDaArena - 18.0 || prevY > alturaDaArena - 18.0) {
            prevX = Math.min(Math.max(18.0, prevX), larguraDaArena - 18.0);
            prevY = Math.min(Math.max(18.0, prevY), alturaDaArena - 18.0);
        }
        
        double anguloAbs = Utils.normalAbsoluteAngle(
            Math.atan2(
                prevX - getX(), prevY - getY()
            )
        );
        
        setTurnRightRadians(distancia / 2 * - 1 - getRadarHeadingRadians());
        setTurnRadarRightRadians(Utils.normalRelativeAngle(distancia - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(anguloAbs - getGunHeadingRadians()));
        fire(forcaTiro);
        
        if (getVelocityRate() > 0){
            setVelocityRate(getVelocityRate() + 1);
        } 
        else {
            setVelocityRate(- 1);
        }

        if (getVelocityRate() > 0 && ((getTurnRate() < 0 && distancia > 0) || (getTurnRate() > 0 && distancia < 0))) {
            setTurnRate(getTurnRate() * -1);
        }
    }
    /**
     * onHitByBullet: É executado quando o robô leva um tiro.
     */
    public void onHitByBullet(HitByBulletEvent e) {
        double Radar = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getRadarHeading());
        setTurnRadarRight(Radar);
        setTurnLeft(-3);
        setTurnRate(3);
        setVelocityRate(-1 * getVelocityRate());
    }
    /**
     * onHitWall: É executado quando o robô colide com a parede.
     */
    public void onHitWall(HitWallEvent e) {
        setVelocityRate(-1 * getVelocityRate());
        setTurnRate(getTurnRate() + 2);
        execute();
    }
    /**
     * onHitRobot: É executado quando o robô bate em outro robô.
     */
    public void onHitRobot(HitRobotEvent e) {
        double Canhao = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading());
        turnGunRight(Canhao);
        setFire(3);
        setVelocityRate(getVelocity() + 3);
        execute();
    }
    /**
     * onWin: É executado quando o robô ganha o round.
     */
    public void onWin(WinEvent e) {
        int win = 1;
        while (true) {
            turnRight(90 * win);
            win = win * -1;
        }
    }
}