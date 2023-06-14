/**
 * File Name - [Mosquito.java]
 * Description - Mosquito
 * @Author - Michael Khart & Ian Leung
 * @Date - June 8, 2023
 */

import java.util.ArrayList;

public class Mosquito extends Enemy{

    private int chargeUpCounter = Constants.mosquitoGameLoopChargeUp;
    private int chargeUpDistanceTraveled = 0;
    private int startingChargeUpX;
    private int startingChargeUpY;


    /**
     * constructor
     * Creates a Mosquito object with specified parameters.
     *
     * @param x             the x-coordinate of the Mosquito's position
     * @param y             the y-coordinate of the Mosquito's position
     * @param width         the width of the Mosquito's hitbox
     * @param height        the height of the Mosquito's hitbox
     * @param health        the current health of the Mosquito
     * @param totalHealth   the total health of the Mosquito
     * @param damage        the damage inflicted by the Mosquito
     * @param goldReward    the amount of gold rewarded by the Mosquito
     * @param respawnX      the x-coordinate of the Mosquito's respawn position
     * @param respawnY      the y-coordinate of the Mosquito's respawn position
     */
    Mosquito(int x, int y, int width, int height, double health, double totalHealth, double damage, double goldReward, double respawnX, double respawnY) {
        super(x, y, width, height, health, totalHealth, damage, goldReward, respawnX, respawnY);

        this.setTotalCooldownTimer(100);
        this.setXSpeed(Constants.mosquitoSpeed);
    }

    /**
     * constructor
     * Creates a Mosquito object with specified parameters
     * @param x             the x-coordinate of the Mosquito's position
     * @param y             the y-coordinate of the Mosquito's position
     * @param width         the width of the Mosquito's hitbox
     * @param height        the height of the Mosquito's hitbox
     * @param health        the current health of the Mosquito
     * @param totalHealth   the total health of the Mosquito
     * @param damage        the damage inflicted by the Mosquito
     * @param goldReward    the amount of gold rewarded by the Mosquito
     * @param respawnX      the x-coordinate of the Mosquito's respawn position
     * @param respawnY      the y-coordinate of the Mosquito's respawn position
     * @param obeliskEnemy  specifies if the Mosquito is an obelisk enemy
     */
    Mosquito(int x, int y, int width, int height, double health, double totalHealth, double damage, double goldReward, double respawnX, double respawnY, boolean obeliskEnemy) {
        super(x, y, width, height, health, totalHealth, damage, goldReward, respawnX, respawnY, obeliskEnemy);

        this.setTotalCooldownTimer(100);
        this.setXSpeed(Constants.mosquitoSpeed);
    }


    /**
     * move
     * moveent algorith for player
     * @param player - the player
     * @param proximity - prox
     */
    public void move(Player player, ArrayList<Wall> proximity) {

        if (isAbilityActive()) { // wif we already attacking,

            if (this.chargeUpCounter > 0) { // charge up animation/moevemtn
                this.chargeUp(player);
            } else { // continue on attacking
                this.movementAbility(player, proximity);
            }

        } else {

            double distance = Math.sqrt( Math.pow((player.getCenterY() - this.getCenterY()) , 2) +  Math.pow((player.getCenterX() - this.getCenterX()) , 2) );

            if (distance < Constants.mosquitoVision) {
                if (this.distanceToPlayer(player, proximity, false) <= Constants.mosquitoVision) {
                    // if we in distance and raytracing successfull

                    if (this.getCooldownTimerAbility() == 0) { // restart ability if no cooldown

                        chargeUpDistanceTraveled = 0;
                        startingChargeUpX = (int) this.getCenterX();
                        startingChargeUpY = (int) this.getCenterY();
                        this.chargeUpCounter = Constants.mosquitoGameLoopChargeUp;

                        this.chargeUp(player);

                    } else {
                        // if we finished attack do cooldown

                        this.cooldownMovement(player);

                    }
                } else {
                    this.defaultMovement();
                }



            } else {

                this.defaultMovement();

            }
        }

        if (this.getXSpeed() > 0) {
            this.setDirection(1);
        } else if (this.getXSpeed() < 0){
            this.setDirection(-1);
        }
    }

    /**
     * defaultMovement
     * the default movement of the fly
     */
    public void defaultMovement() {

        // if its going too fast, slowdown
        if (Math.abs(this.getXSpeed()) > Constants.mosquitoSpeed) {
            this.setXSpeed((this.getXSpeed()/2));
        }

        this.setYSpeed(this.getYSpeed()/2);

        this.translate(this.getXSpeed(), this.getYSpeed());

    }

    /**
     * cooldownMovement
     *  cooldownMovement algorith
     *  moves it away from the player
     * @param player - the player
     */
    public void cooldownMovement(Player player) {

        double cooldownXSpeed = 0;

        double dX = player.getCenterX() - this.getCenterX();

        // assigns speed to walk away from player
        if (dX > 0) {
            if (this.getXSpeed() > Constants.mosquitoSpeed) {
                cooldownXSpeed = Constants.mosquitoSpeed * (-1);
            }
        } else if (dX < 0) {

            if (this.getXSpeed() < Constants.mosquitoSpeed) {
                cooldownXSpeed = Constants.mosquitoSpeed;
            }

        }

        if (Math.abs(this.getYSpeed()) > (Constants.mosquitoSpeed)) {
            this.setYSpeed((this.getYSpeed()/2));
        }

        this.translate((int) ( cooldownXSpeed ) , (this.getYSpeed()));

    }


    /**
     * moveementAbility
     * the attack of the moquito
     * @param player - the player
     * @param proximity -the prox
     */
    public void movementAbility(Player player, ArrayList<Wall> proximity ) {

        // if we in range of the player attack to it
        if (this.distanceToPlayer(player, proximity, true) < (Constants.mosquitoMovementAbilityTotal + chargeUpDistanceTraveled)) {
            this.setXSpeed(this.getAbilityDirection(0));
            this.setYSpeed(this.getAbilityDirection(1));
            this.translate(this.getXSpeed(), -this.getYSpeed());

        } else { // if we not in range, start cooldown and ability false

            this.setAbilityActive(false);
            this.setCooldownTimerAbility(this.getTotalCooldownTimer());
        }

    }

    /**
     * chargeUP
     * the chargeupCode for algorith
     * @param player - the player
     */
    public void chargeUp(Player player) {

        this.setAbilityActive(true);

        double dX = player.getCenterX() - this.getCenterX();
        double dY = -(player.getCenterY() - this.getCenterY());

        double interval = Constants.mosquitoMovementAbilitySpeed / (Math.abs(dX) + Math.abs(dY) + 1); // finds inverval

        this.setAbilityDirection((int) (dX * interval), (int) (dY * interval));

        // small calculatoin for chargeup speed xa dn y away from player
        int xTranslation = -(int) (this.getAbilityDirection(0) * interval * (this.chargeUpCounter / (Constants.mosquitoGameLoopChargeUp / 2 )));
        int yTranslation = (int) (this.getAbilityDirection(1) * interval * (this.chargeUpCounter / (Constants.mosquitoGameLoopChargeUp / 2 )));

        this.translate(xTranslation, yTranslation);

        this.chargeUpCounter--;

        if (chargeUpCounter == 0) {
            this.chargeUpDistanceTraveled = (int) (Math.sqrt(Math.pow((this.getCenterX() - this.startingChargeUpX), 2) + Math.pow((this.getCenterY() - this.startingChargeUpY), 2)));
        }

    }

    /**
     * collision
     * collison decisions for the enemy
     * @param otherObject - the collider
     */
    public void collision(GameObject otherObject) {

        if (otherObject instanceof Spike) {
            this.setHealth(-1); // it dies
        } else {

            double enemyBottom = this.getY() + this.getHeight();
            double colliderTop = otherObject.getY();
            double enemyRight = this.getX() + this.getWidth();
            double colliderLeft = otherObject.getX();
            double enemyLeft = this.getX();
            double colliderRight = otherObject.getX() + otherObject.getWidth();

            // if we hit floor, negative y speed
            if ((enemyBottom > colliderTop) && (this.getY() + this.getYSpeed() < colliderTop) && (enemyRight - this.getXSpeed() - 2 > colliderLeft) && (enemyLeft - this.getXSpeed() + 2 < colliderRight)) {

                this.setLocation((int) this.getX(), (int) (colliderTop - this.getHeight()));
                this.setYSpeed(this.getYSpeed() * (-1));

                // if we hit celling, reflect y speed
            } else if (this.getY() < colliderTop + otherObject.getHeight() && enemyBottom > colliderTop + otherObject.getHeight() && (enemyRight - this.getXSpeed() - 2 > colliderLeft) && (enemyLeft - this.getXSpeed() + 2 < colliderRight)) {

                this.setLocation((int) this.getX(), (int) (colliderTop + otherObject.getHeight()));
                this.setYSpeed(this.getYSpeed() * (-1));

                // if we hit right reflect x movement
            } else if (enemyRight > colliderLeft && enemyLeft < colliderLeft && enemyBottom > colliderTop && this.getY() < colliderTop + otherObject.getHeight()) {
                this.setLocation((int) (colliderLeft - this.getWidth()), (int) this.getY());
                this.setXSpeed(this.getXSpeed() * -1);

                // if we hit left reflect x movement
            } else if (this.getX() < colliderRight && enemyRight > colliderRight && enemyBottom > colliderTop && this.getY() < colliderTop + otherObject.getHeight()) {
                this.setLocation((int) (colliderRight), (int) this.getY());
                this.setXSpeed(this.getXSpeed() * -1);

            }
        }
    }


    /**
     * update
     * updates some counters
     */
    public void update() {

        this.immunityTick();

        if (this.getCooldownTimerAbility() > 0)  {
            this.setCooldownTimerAbility(this.getCooldownTimerAbility() - 1);
        }

        this.setRespawnTimer(this.getRespawnTimer() - 1);

    }
}
