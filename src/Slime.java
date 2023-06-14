/**
 * File Name - [slime.java]
 * Description - slime
 * @Author - Michael Khart & Ian Leung
 * @Date - June 8, 2023
 */

import java.util.ArrayList;

public class Slime extends Enemy{



    /**
     * Creates a Slime object with specified parameters.
     *
     * @param x             the x-coordinate of the Slime's position
     * @param y             the y-coordinate of the Slime's position
     * @param width         the width of the Slime's hitbox
     * @param height        the height of the Slime's hitbox
     * @param health        the current health of the Slime
     * @param totalHealth   the total health of the Slime
     * @param damage        the damage inflicted by the Slime
     * @param goldReward    the amount of gold rewarded by the Slime
     * @param respawnX      the x-coordinate of the Slime's respawn position
     * @param respawnY      the y-coordinate of the Slime's respawn position
     */
    Slime(int x, int y, int width, int height, double health, double totalHealth, double damage, double goldReward, double respawnX, double respawnY) {
        super(x, y, width, height, health, totalHealth, damage, goldReward, respawnX, respawnY);

        this.setXSpeed(Constants.slimeSpeed);

    }

    /**
     * Creates a Slime object with specified parameters.
     *
     * @param x             the x-coordinate of the Slime's position
     * @param y             the y-coordinate of the Slime's position
     * @param width         the width of the Slime's hitbox
     * @param height        the height of the Slime's hitbox
     * @param health        the current health of the Slime
     * @param totalHealth   the total health of the Slime
     * @param damage        the damage inflicted by the Slime
     * @param goldReward    the amount of gold rewarded by the Slime
     * @param respawnX      the x-coordinate of the Slime's respawn position
     * @param respawnY      the y-coordinate of the Slime's respawn position
     * @param obeliskEnemy  specifies if the Slime is an obelisk enemy
     */
    Slime(int x, int y, int width, int height, double health, double totalHealth, double damage, double goldReward, double respawnX, double respawnY, boolean obeliskEnemy) {
        super(x, y, width, height, health, totalHealth, damage, goldReward, respawnX, respawnY, obeliskEnemy);

        this.setXSpeed(Constants.slimeSpeed);

    }


    /**
     * move
     * amove algorith for this enemy
     * @param player - the player
     * @param proximity - the prox
     */

    public void move(Player player, ArrayList<Wall> proximity) {

        double distance = Math.sqrt( Math.pow((player.getCenterY() - this.getCenterY()) , 2) +  Math.pow((player.getCenterX() - this.getCenterX()) , 2) );

        if (distance < Constants.slimeVision) {
            if (this.distanceToPlayer(player, proximity, false) <= Constants.slimeVision) { // if raytracing found nothing

                // run at player
                if ((player.getCenterX() - this.getCenterX() >= 0) && (this.getXSpeed() < Constants.slimeSpeed)) {
                    this.setXSpeed(this.getXSpeed() + 1);
                } else if ((player.getCenterX() - this.getCenterX() < 0) && (this.getXSpeed() > -Constants.slimeSpeed)) {
                    this.setXSpeed(this.getXSpeed() - 1);
                }

                this.setYSpeed(this.getYSpeed() - Constants.gravity);
                this.translate(this.getXSpeed(), -this.getYSpeed());

            } else {

                // keep moving in current direction
                this.setYSpeed(this.getYSpeed() - Constants.gravity);
                this.translate(this.getXSpeed(), -this.getYSpeed());
            }

        } else {

            // keep moving
            this.setYSpeed(this.getYSpeed() - Constants.gravity);
            this.translate(this.getXSpeed(), -this.getYSpeed());

        }

        if (Math.abs(this.getXSpeed()) > Constants.slimeSpeed) {
            this.setXSpeed(this.getXSpeed() - this.getXSpeed()/Math.abs(this.getXSpeed()) * 2);
        }

    }


    /**
     * collision
     * how the lisme collides with stuff
     * @param otherObject
     */
    public void collision(GameObject otherObject) {

        if (otherObject instanceof Spike) {
            this.setHealth(-1); // die
        } else {

            double enemyBottom = this.getY() + this.getHeight();
            double otherObjectTop = otherObject.getY();
            double enemyRight = this.getX() + this.getWidth();
            double otherobjecttLeft = otherObject.getX();
            double enemyLeft = this.getX();
            double otherobejctRight = otherObject.getX() + otherObject.getWidth();

            // if we land on something
            if ((enemyBottom > otherObjectTop) && (this.getY() + this.getYSpeed() < otherObjectTop) && (enemyRight - this.getXSpeed() - 2 > otherobjecttLeft) && (enemyLeft - this.getXSpeed() + 2 < otherobejctRight)) {
                this.setLocation((int) this.getX(), (int) (otherObjectTop - this.getHeight()));
                this.setYSpeed(0); // stop falling


                if (onEdge(otherObject)) { // if we are on edge go back

                    this.setXSpeed(this.getXSpeed() * -1);
                    this.setLocation((int) this.getX() + this.getXSpeed(), (int) this.getY() - this.getYSpeed());

                }

                // if collide with roof
            } else if (this.getY() < otherObjectTop + otherObject.getHeight() && enemyBottom > otherObjectTop + otherObject.getHeight() && (enemyRight - this.getXSpeed() - 2 > otherobjecttLeft) && (enemyLeft - this.getXSpeed() + 2 < otherobejctRight)) {
                this.setLocation((int) this.getX(), (int) (otherObjectTop + otherObject.getHeight()));
                this.setYSpeed(0);

                //if collide with somethin on the right, reverse x speed d
            } else if (enemyRight > otherobjecttLeft && enemyLeft < otherobjecttLeft && enemyBottom > otherObjectTop && this.getY() < otherObjectTop + otherObject.getHeight()) {
                this.setLocation((int) (otherobjecttLeft - this.getWidth()), (int) this.getY());
                this.setXSpeed(this.getXSpeed() * -1);

                //if collide with somethin on the left, reverse x speed d

            } else if (this.getX() < otherobejctRight && enemyRight > otherobejctRight && enemyBottom > otherObjectTop && this.getY() < otherObjectTop + otherObject.getHeight()) {
                this.setLocation((int) (otherobejctRight), (int) this.getY());
                this.setXSpeed(this.getXSpeed() * -1);

            }
        }
    }


    /**
     * update
     * updates some things
     */
    public void update() {

        this.immunityTick();

        if (this.getXSpeed() > 0) {
            this.setDirection(-1);
        } else if (this.getXSpeed() < 0) {
            this.setDirection(1);
        }

        this.setRespawnTimer(this.getRespawnTimer() - 1);


    }
}
