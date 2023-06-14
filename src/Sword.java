/**
 * [Sword.java]
 * Class represent sword
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
public class Sword extends Attack{


    /**
     * Sword
     * Constructs sword
     * @param x x
     * @param y y
     * @param direction direction
     * @param isFriendly frinedly
     * @param damageBoost multiplier
     */
    Sword(int x, int y, int direction, boolean isFriendly, double damageBoost) {
        super(x, y, 150, 200, (int) (30 * damageBoost), direction, isFriendly, 12);
    }
}
