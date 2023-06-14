/**
 * [Rocket.java]
 * Class represent rocket
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */

public class Rocket extends Projectile{

    /**
     * Rocket
     * @param x x
     * @param y y
     * @param targetX targetx
     * @param targetY targety
     * @param direction direction
     * @param isFriendly friendly
     * @param damageBoost multiplier
     */
    Rocket(int x, int y, int targetX, int targetY, int direction, boolean isFriendly, double damageBoost) {
        super(x, y, 25, 25, (int) (30 * damageBoost), direction, isFriendly, 60, Constants.rocketCost);
        double dX = targetX - this.getCenterX();
        double dY = -(targetY - this.getCenterY());
        double interval = 30/(Math.abs(dX) + Math.abs(dY) + 1);
        this.setXSpeed((int) (dX * interval));
        this.setYSpeed((int) (dY * interval));
    }
}
