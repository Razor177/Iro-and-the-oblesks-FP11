/**
 * [Crystal.java]
 * This class represents a crystal that spawns orbs when destroyed
 * @author - Michael Khart & Ian Leung
 * @version 1.0 June 8, 2023
 */
public class Crystal extends Wall {

    private String boostType;

    private int boostValue;

    private int health;

    private final int maxHealth;

    private int immunityTimer;

    private int respawnTimer;


    /**
     * Crystal
     * This constructs a crystal
     * @param x the x positon
     * @param y the y psotion
     * @param width the width
     * @param height the height
     * @param boostType the type of boost
     */
    Crystal(int x, int y, int width, int height, String boostType) {
        super(x, y, width, height, false);
        this.boostType = boostType;
        // Provides a random boost amount from 50 to 70
        this.boostValue = (int) (Math.random() * 3 + 5) * 10;
        this.maxHealth = 35;
        this.health = 35;

    }

    /**
     * crystalTick
     * This method handles immunity and respawning for crystals
     */
    public void crystalTick() {

        // Counts down
        if (this.immunityTimer > 0) {
            this.immunityTimer--;
        }

        if (this.respawnTimer > 0) {
            this.respawnTimer--;
        }

        // Crystal death
        if (this.health < 0) {
            this.respawnTimer = 1500;
            this.health = maxHealth;
        }
    }

    /**
     * takeHit
     * This method interacts an attack and a crystal
     * @param attack the attack
     */
    public void takeHit(Attack attack) {
        // Attack is ignored if Crystal is immune
        if ((immunityTimer == 0) && (this.respawnTimer == 0)) {
            this.health -= attack.getAttackDamage();
            this.immunityTimer = 25;
        }
    }

    public String getBoostType() {
        return boostType;
    }

    public int getBoostValue() {
        return boostValue;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getRespawnTimer() {
        return respawnTimer;
    }

}

