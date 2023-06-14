/**
 * [Explosion.java]
 * Class represent explosion
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
public class Explosion extends Attack{

    private final int maxRadius;

    private int radius;

    private int lastRadius;

    /**
     * Explosion
     * Constructs explosion class
     * @param x x
     * @param y y
     * @param isFriendly friendly
     * @param maxRadius Max possible radius
     * @param damageBoost multiplier
     */
    Explosion(int x, int y, boolean isFriendly, int maxRadius, double damageBoost) {
        super(x, y, 0, 0, (int) (150 * damageBoost), 0, isFriendly, 15);
        this.maxRadius = maxRadius;
        this.radius = 0;
        this.lastRadius = 0;
    }

    /**
     * expand
     * Expands the explosion
     */
    public void expand() {
        // Size is based on current life vs max lifetime
        this.lastRadius = radius;
        this.radius = maxRadius * this.getAbilityDuration()/this.getMaxAbilityDuration();
        this.translate(-radius + lastRadius, -radius + lastRadius);
        this.width = radius * 2;
        this.height = radius * 2;
    }

    public int getMaxRadius() {
        return maxRadius;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
