/**
 * [Projectile.java]
 * Class represent projectile
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
abstract class Projectile extends Attack{

    private int xSpeed;

    private int ySpeed;

    private int energyCost;

    /**
     * Projectile
     * @param x x
     * @param y y
     * @param width width
     * @param height height
     * @param attackDamage damage
     * @param direction direction
     * @param isFriendly friendly
     * @param maxDuration max
     * @param energyCost energy
     */
    Projectile(int x, int y, int width, int height, int attackDamage, int direction, boolean isFriendly,int maxDuration, int energyCost) {
        super(x, y, width, height, attackDamage, direction, isFriendly, maxDuration);
        this.energyCost = energyCost;
    }

    /**
     * move
     * translates projectile
     */
    public void move() {
        this.translate(xSpeed, -ySpeed);
    }

    public int getXSpeed() {
        return xSpeed;
    }

    public void setXSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }

    public int getYSpeed() {
        return ySpeed;
    }

    public void setYSpeed(int ySpeed) {
        this.ySpeed = ySpeed;
    }
}


