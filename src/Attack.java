/**
 * [Attack.java]
 * Class represent attack
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
abstract public class Attack extends GameObject{//

    private double attackDamage;
    private boolean isFriendly;
    private int abilityDuration;

    private final int maxAbilityDuration;

    private final int direction;

    /**
     * Attack
     * COnstructs Attack
     * @param x x
     * @param y y
     * @param width width
     * @param height height
     * @param attackDamage damage
     * @param direction direction
     * @param isFriendly friendly to player
     * @param maxDuration how long it last
     */
    Attack(int x, int y, int width, int height, int attackDamage, int direction, boolean isFriendly, int maxDuration) {
        super(x, y, width, height);
        this.attackDamage = attackDamage;
        this.direction = direction;
        this.isFriendly = isFriendly;
        this.abilityDuration = 0;
        this.maxAbilityDuration = maxDuration;
    }

    public double getAttackDamage() {
        return attackDamage;
    }


    public boolean isFriendly() {
        return isFriendly;
    }

    public int getAbilityDuration() {
        return abilityDuration;
    }

    public void setAbilityDuration(int abilityDuration) {
        this.abilityDuration = abilityDuration;
    }

    public int getMaxAbilityDuration() {
        return maxAbilityDuration;
    }

    public int getDirection() {
        return direction;
    }
}
