/**
 * [Spike.java]
 * This class represents a spike which does damage to the player and acts as a boundary
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */

public class Spike extends Wall{
    private final double damage;

    /**
     * spike
     *  amkes a spike object
     * @param x - the x
     * @param y - the y
     * @param width - the width
     * @param height - the length
     * @param damage - the damage
     */

    Spike(int x, int y, int width, int height, int damage) {
        super(x, y, width, height);
        this.damage = damage;
    }


    /**
     * get damage
     * gets damage
     * @return - damaghe
     */
    public double getDamage() {
        return this.damage;
    }
}
