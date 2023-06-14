/**
 * [Hammer.java]
 * Class represent hammer
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
public class Hammer extends Attack{

    /**
     * Hammer
     * Constructs a hammer
     * @param x x
     * @param y y
     * @param direction direction
     * @param isFriendly frinely
     * @param damageBoost multiplier
     */
    Hammer(int x, int y, int direction, boolean isFriendly, double damageBoost) {
        super(x, y, 250, 250, (int) (40 * damageBoost), direction, isFriendly, 20);
    }
}
