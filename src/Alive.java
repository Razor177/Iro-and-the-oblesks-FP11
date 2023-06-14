/**
 * [Alive.java]
 * This class represents an alive, moving game object
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
abstract public class Alive extends Moveable{

    private double health;
    private double maxHealth;

    private int direction;

    private int[] abilityDirection = {0, 0};

    private boolean abilityActive = false;

    private int immunityTimer = 0;

    private final int maxImmunity;


    /**
     * Alive
     * This method constructs an alive object
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     * @param health the health
     * @param maxHealth the max health
     * @param maxImmunity the max immunity timer
     */
    Alive(int x, int y, int width, int height, double health, double maxHealth, int maxImmunity) {
        super(x, y, width, height);
        this.health = health;
        this.maxHealth = maxHealth;
        this.maxImmunity = maxImmunity;
        this.direction = 1;
    }

    /**
     * immunityTick
     * This method ticks to count immunity frames
     */
    public void immunityTick() {

        // Counts up to max immunity
        if (this.immunityTimer > 0) {
            this.immunityTimer ++;
        }

        if (this.immunityTimer > this.maxImmunity) {
            this.immunityTimer = 0;
        }
    }

    public double getHealth() {
        return health;
    }
    public void setHealth(double health) {
        this.health = health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getImmunityTimer() {
        return immunityTimer;
    }

    public void setImmunityTimer(int immunityTimer) {
        this.immunityTimer = immunityTimer;
    }

    public int getMaxImmunity() {
        return maxImmunity;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isAbilityActive() {
        return abilityActive;
    }

    public void setAbilityActive(boolean abilityActive) {
        this.abilityActive = abilityActive;
    }

    public int getAbilityDirection(int index) {
        return abilityDirection[index];
    }

    public void setAbilityDirection(int x, int y) {
        this.abilityDirection[0] = x;
        this.abilityDirection[1] = y;
    }

}
