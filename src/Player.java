/**
 * [Player.java]
 * This class represents a player that the user controls
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */

import java.util.ArrayList;

public class Player extends Alive {

    private int totalGold;
    private double energy;
    private double maxEnergy;
    private int jumpNum;
    private int maxJumps;
    private boolean dashUnlocked;
    private boolean bashUnlocked;
    private boolean dashUsed = false;
    private boolean bashUsed = false;
    private String currentWeapon = "Sword";
    private int abilityTravelled = 0;
    private boolean movingRight;
    private boolean movingLeft;
    private int[] respawnPoint;
    private Wall lastWall;
    private ArrayList<String> weapons;
    private int attackCooldown;
    private double damageBoost;


    /**
     * Creates a Player object with specified parameters.
     *
     * @param x             the x-coordinate of the Player's position
     * @param y             the y-coordinate of the Player's position
     * @param width         the width of the Player's hitbox
     * @param height        the height of the Player's hitbox
     * @param health        the current health of the Player
     * @param energy        the current energy of the Player
     * @param maxEnergy     the maximum energy of the Player
     * @param maxJumps      the maximum number of jumps the Player can perform
     * @param dashUnlocked  specifies if the Player has unlocked the dash ability
     * @param bashUnlocked  specifies if the Player has unlocked the bash ability
     * @param damageBoost   the damage boost value for the Player
     */
    Player(int x, int y, int width, int height, double health, double MaxHealth, double energy, double maxEnergy, int maxJumps, boolean dashUnlocked, boolean bashUnlocked, double damageBoost, int totalGold) {
        super(x, y, width, height, health, MaxHealth, 30);
        this.energy = energy;
        this.maxEnergy = maxEnergy;
        this.totalGold = totalGold;
        this.jumpNum = 0;
        this.maxJumps = maxJumps;
        this.setImmunityTimer(1);
        this.weapons = new ArrayList<>();
        this.weapons.add("Sword");
        this.respawnPoint = new int[]{x, y};
        this.dashUnlocked = dashUnlocked;
        this.bashUnlocked = bashUnlocked;
        this.damageBoost = damageBoost;
        this.attackCooldown = 0;

    }


    /**
     * move
     * move for player
     */
    public void move() {

        if (isAbilityActive()) {
            this.movementAbility(); // if ability active continue using it
        }
        if (attackCooldown > 0) {
            attackCooldown --;
        }

        // keep moving y speed and - gravity
        this.translate(this.getXSpeed(),  -this.getYSpeed());
        this.setYSpeed(this.getYSpeed() - Constants.gravity);

        // if player tryna move in a direction, and its under the max speed in the direction, add to the speed
        if (movingRight && this.getXSpeed() < Constants.maxXSpeed) {
            this.setXSpeed(this.getXSpeed() + Constants.XSpeedAddition);
        } else if (movingLeft && this.getXSpeed() > (-1) * (Constants.maxXSpeed)) {
            this.setXSpeed(this.getXSpeed() - Constants.XSpeedAddition);
        } else if (this.getXSpeed() != 0) { // slows down a player
            this.setXSpeed(this.getXSpeed() - this.getXSpeed()/Math.abs(this.getXSpeed()) * Constants.XSpeedAddition);
            if (Math.abs(this.getXSpeed()) <= Constants.XSpeedAddition) {
                this.setXSpeed(0);
            }
        }

        // reset health and energy if they overhealed / energied
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }

        if (this.energy > this.maxEnergy) {
            this.energy = maxEnergy;
        }
    }

    /**
     * jump
     * add the y speed of jump to player speed
     */
    public void jump() {

        if (this.getJumpNum() < this.getMaxJumps()) {
            this.setYSpeed(Constants.jumpBoost);
            this.setJumpNum(this.getJumpNum() + 1);
        }

    }

    /**
     * dash
     * setup for b=movemet ability, sets x speed toa bunch and doesnt give y speed so that you only go in one direction
     */
    public void dash() {
        this.setAbilityActive(true);
        this.setAbilityDirection(Constants.abilitySpeed * this.getDirection(), 0);
        this.dashUsed = true;

    }

    /**
     * bash
     * sets up the inital speeds for bash
     * @param targetX - the players choice of where to bash x
     * @param targetY - players choice of where to bash y
     */
    public void bash(int targetX, int targetY) {

        this.setAbilityActive(true);

        double dX = targetX - this.getCenterX();
        double dY = -(targetY - this.getCenterY());

        double interval = Constants.abilitySpeed/Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

        this.setAbilityDirection((int) (dX * interval), (int) (dY * interval));

        this.bashUsed = true;
    }

    /**
     * movementAbility
     * the thing thatll continue tmoveing the player when they in player ability
     */
    public void movementAbility() {

        if (this.abilityTravelled < Constants.movementAbilityTotal) { // if we can still ability, keep going at current speed
            this.setXSpeed(this.getAbilityDirection(0));
            this.setYSpeed(this.getAbilityDirection(1));
            this.abilityTravelled += Math.sqrt(Math.pow(this.getAbilityDirection(0), 2) + Math.pow(this.getAbilityDirection(1), 2));

        } else {  // stop doing the movement ability
            this.abilityTravelled = 0;
            this.setAbilityActive(false);
        }
    }

    /**
     * collide
     * how the player collides with objects
     * @param otherObject - the ocllider
     */
    public void collide(GameObject otherObject) {

        if ((otherObject instanceof Spike) || (otherObject instanceof Enemy)) {

            if (this.getImmunityTimer() == 0) { // if we can take damage, take damage, set x and y speed based on collision
                double dX = (this.getCenterX() - otherObject.getCenterX());
                double dY = (otherObject.getCenterY() - this.getCenterY());

                double interval = 35/(Math.abs(dX) + Math.abs(dY));

                this.setXSpeed((int) (dX * interval));

                if ((this.getXSpeed() < 10) && (this.getXSpeed() != 0)) {
                    this.setXSpeed(this.getXSpeed()/Math.abs(this.getXSpeed()) * 20);
                } if (this.getXSpeed() == 0) {
                    this.setXSpeed(-20 * this.getDirection());
                }

                this.setYSpeed((int) (dY * interval));
                this.setImmunityTimer(1);

                if (otherObject instanceof Enemy) {
                    this.setHealth(this.getHealth() - ((Enemy) otherObject).getDamage());
                } else {
                    this.setHealth(this.getHealth() - ((Spike) otherObject).getDamage());
                }
            }
            // if its just  wall:
        } else {
            double playerBottom = this.getY() + this.getHeight();
            double otherObjectTop = otherObject.getY();
            double playerRight = this.getX() + this.getWidth();
            double otherObjectLeft = otherObject.getX();
            double playerLeft = this.getX();
            double otherObjectRight = otherObject.getX() + otherObject.getWidth();

            // if the player hits a floor
            if ((playerBottom > otherObjectTop) && (this.getY() + this.getYSpeed() < otherObjectTop) && (playerRight - this.getXSpeed() - 2 > otherObjectLeft) && (playerLeft - this.getXSpeed() + 2 < otherObjectRight)) {
                this.setLocation((int) this.getX(), (int) (otherObjectTop - this.getHeight()));
                this.setYSpeed(0); // Stop the player's vertical movement
                this.wallReset();

                // if the wall allows the player to spawn on it, spawn on it
                if ((otherObject != lastWall) && (((Wall) otherObject).isrespawnable())) {
                    this.respawnPoint[0] = (int) this.getX();
                    this.respawnPoint[1] = (int) this.getY();
                    lastWall = (Wall) otherObject;
                }

                // if we hit the celling
            } else if (this.getY() < otherObjectTop + otherObject.getHeight() && playerBottom > otherObjectTop + otherObject.getHeight() && (playerRight - this.getXSpeed() - 2 > otherObjectLeft) && (playerLeft - this.getXSpeed() + 2 < otherObjectRight)) {
                this.setLocation((int) this.getX(), (int) (otherObjectTop + otherObject.getHeight()));
                this.setYSpeed(0);

                // if we hit a object to the right
            } else if (playerRight > otherObjectLeft && playerLeft < otherObjectLeft && playerBottom > otherObjectTop && this.getY() < otherObjectTop + otherObject.getHeight()) {
                this.setLocation((int) (otherObjectLeft - this.getWidth()), (int) this.getY());
                this.setXSpeed(0); // Reverse the player's horizontal speed

                if (this.getYSpeed() < 0) {
                    this.setYSpeed(this.getYSpeed() + 3);
                }

                this.wallReset();

                // if we hit a object to the left
            } else if (this.getX() < otherObjectRight && playerRight > otherObjectRight && playerBottom > otherObjectTop && this.getY() < otherObjectTop + otherObject.getHeight()) {
                this.setLocation((int) (otherObjectRight), (int) this.getY());
                this.setXSpeed(0); // Reverse the player's horizontal speed
                if (this.getYSpeed() < 0) {
                    this.setYSpeed(this.getYSpeed() + 3);
                }
                this.wallReset();
            }
        }

    }

    /**
     * wallReset
     * resets a bunch of values we need reset when player hits wall
     */
    public void wallReset() {
        this.jumpNum = 0;
        this.dashUsed = false;
        this.bashUsed = false;
    }

    /**
     * respawn
     * respawn method for player
     */
    public void respawn() {
        // resets all values and spans at respawn x and y

        this.setLocation(this.respawnPoint[0], this.respawnPoint[1]);
        this.setHealth(this.getMaxHealth());
        this.setEnergy(this.getMaxEnergy());
        this.setXSpeed(0);
        this.setYSpeed(0);
        this.totalGold = (int) (this.totalGold * 0.7);

    }



    /**
     * getMaxEnergy
     * Returns the maximum energy of the Player.
     *
     * @return - the maximum energy of the Player
     */
    public double getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * setMaxEnergy
     * Sets the maximum energy of the Player.
     *
     * @param maxEnergy - the maximum energy to set
     */
    public void setMaxEnergy(double maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    /**
     * getCurrentWeapon
     * Returns the current weapon of the Player.
     *
     * @return - the current weapon of the Player
     */
    public String getCurrentWeapon() {
        return currentWeapon;
    }

    /**
     * setCurrentWeapon
     * Sets the current weapon of the Player.
     *
     * @param currentWeapon - the current weapon to set
     */
    public void setCurrentWeapon(String currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    /**
     * getTotalGold
     * Returns the total gold of the Player.
     *
     * @return - the total gold of the Player
     */
    public double getTotalGold() {
        return totalGold;
    }

    /**
     * setTotalGold
     * Sets the total gold of the Player.
     *
     * @param totalGold - the total gold to set
     */
    public void setTotalGold(int totalGold) {
        this.totalGold = totalGold;
    }

    /**
     * getEnergy
     * Returns the current energy of the Player.
     *
     * @return - the current energy of the Player
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * setEnergy
     * Sets the current energy of the Player.
     *
     * @param energy - the current energy to set
     */
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    /**
     * getJumpNum
     * Returns the jump number of the Player.
     *
     * @return - the jump number of the Player
     */
    public int getJumpNum() {
        return jumpNum;
    }

    /**
     * setJumpNum
     * Sets the jump number of the Player.
     *
     * @param jumpNum - the jump number to set
     */
    public void setJumpNum(int jumpNum) {
        this.jumpNum = jumpNum;
    }

    /**
     * getMaxJumps
     * Returns the maximum number of jumps the Player can perform.
     *
     * @return - the maximum number of jumps
     */
    public int getMaxJumps() {
        return maxJumps;
    }

    /**
     * setMaxJumps
     * Sets the maximum number of jumps the Player can perform.
     *
     * @param maxJumps - the maximum number of jumps to set
     */
    public void setMaxJumps(int maxJumps) {
        this.maxJumps = maxJumps;
    }

    /**
     * isDashUnlocked
     * Returns whether the dash ability is unlocked for the Player.
     *
     * @return - true if dash ability is unlocked, false otherwise
     */
    public boolean isDashUnlocked() {
        return dashUnlocked;
    }

    /**
     * setDashUnlocked
     * Sets whether the dash ability is unlocked for the Player.
     *
     * @param dashUnlocked - true to unlock dash ability, false otherwise
     */
    public void setDashUnlocked(boolean dashUnlocked) {
        this.dashUnlocked = dashUnlocked;
    }

    /**
     * isBashUnlocked
     * Returns whether the bash ability is unlocked for the Player.
     *
     * @return - true if bash ability is unlocked, false otherwise
     */
    public boolean isBashUnlocked() {
        return bashUnlocked;
    }

    /**
     * setBashUnlocked
     * Sets whether the bash ability is unlocked for the Player.
     *
     * @param bashUnlocked - true to unlock bash ability, false otherwise
     */
    public void setBashUnlocked(boolean bashUnlocked) {
        this.bashUnlocked = bashUnlocked;
    }

    /**
     * getWeapons
     * Returns the list of weapons owned by the Player.
     *
     * @return - the list of weapons owned by the Player
     */
    public ArrayList<String> getWeapons() {
        return weapons;
    }

    /**
     * isDashUsed
     * Returns whether the dash ability has been used by the Player.
     *
     * @return - true if dash ability has been used, false otherwise
     */
    public boolean isDashUsed() {
        return dashUsed;
    }

    /**
     * isBashUsed
     * Returns whether the bash ability has been used by the Player.
     *
     * @return - true if bash ability has been used, false otherwise
     */
    public boolean isBashUsed() {
        return bashUsed;
    }

    /**
     * setMovingRight
     * Sets the flag indicating whether the Player is moving to the right.
     *
     * @param movingRight - true if the Player is moving to the right, false otherwise
     */
    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    /**
     * setMovingLeft
     * Sets the flag indicating whether the Player is moving to the left.
     *
     * @param movingLeft - true if the Player is moving to the left, false otherwise
     */
    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    /**
     * getRespawnPoint
     * Returns the respawn point of the Player.
     *
     * @return - the respawn point of the Player as an array of coordinates
     */
    public int[] getRespawnPoint() {
        return respawnPoint;
    }

    /**
     * setRespawnPoint
     * Sets the respawn point of the Player.
     *
     * @param respawnPoint - the respawn point to set as an array of coordinates
     */
    public void setRespawnPoint(int[] respawnPoint) {
        this.respawnPoint = respawnPoint;
    }

    /**
     * getAttackCooldown
     * Returns the attack cooldown of the Player.
     *
     * @return - the attack cooldown of the Player
     */
    public int getAttackCooldown() {
        return attackCooldown;
    }

    /**
     * setAttackCooldown
     * Sets the attack cooldown of the Player.
     *
     * @param attackCooldown - the attack cooldown to set
     */
    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    /**
     * getDamageBoost
     * Returns the damage boost of the Player.
     *
     * @return - the damage boost of the Player
     */
    public double getDamageBoost() {
        return damageBoost;
    }

    /**
     * setDamageBoost
     * Sets the damage boost of the Player.
     *
     * @param damageBoost - the damage boost to set
     */
    public void setDamageBoost(double damageBoost) {
        this.damageBoost = damageBoost;
    }
}