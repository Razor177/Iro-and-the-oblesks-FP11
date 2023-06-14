/**
 * [Arrow.java]
 * Class represent arrow
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
public class Arrow extends Projectile {

    /**
     * Arrow
     * @param x x
     * @param y y
     * @param targetX targetx
     * @param targetY targety
     * @param direction direction
     * @param isFriendly frindly
     * @param Power damage
     * @param damageBoost multiplier
     */
    Arrow(int x, int y, int targetX, int targetY, int direction, boolean isFriendly, int Power, double damageBoost) {
        super(x, y, 45, 45, (int) (5 + Power * 0.5 * damageBoost), direction, isFriendly, 100, Constants.arrowCost);
        double dX = targetX - this.getCenterX();
        double dY = -(targetY - this.getCenterY());
        double interval = Power/Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2) + 1);
        this.setXSpeed((int) (dX * interval));
        this.setYSpeed((int) ((dY * interval) + (this.getXSpeed() == 0 ? dY * interval : dX/this.getXSpeed()/2)));
        if (this.getYSpeed() > Power * 2) {
            this.setYSpeed(Power * 2);
        }
    }

    /**
     * move
     * Moves the arrow and applies gravity
     */
    public void move() {
        super.move();
        this.setYSpeed(this.getYSpeed() - 1);
    }
}
