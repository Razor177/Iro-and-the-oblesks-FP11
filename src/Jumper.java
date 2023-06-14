/**
 * File Name - [Jumper.java]
 * Description -  Jumper
 * @Author - Michael Khart & Ian Leung
 * @Date - June 8, 2023
 */

import java.util.ArrayList;


public class Jumper extends Enemy{

    private int chargeUpCounter = Constants.jumperGameLoopChargeUp;
    private int landedFrenzy = 0;
    private double respawnX, respawnY;



    /**
     * Jumper
     * constructor general
     * @param x            - The x-coordinate of the jumper.
     * @param y            - The y-coordinate of the jumper.
     * @param width        - The width of the jumper.
     * @param height       - The height of the jumper.
     * @param health       - The current health of the jumper.
     * @param totalHealth  - The total health of the jumper.
     * @param damage       - The damage caused by the jumper.
     * @param goldReward   - The amount of gold rewarded for defeating the jumper.
     * @param respawnX     - The x-coordinate for the jumper's respawn point.
     * @param respawnY     - The y-coordinate for the jumper's respawn point.
     */
    Jumper(int x, int y, int width, int height, double health, double totalHealth, double damage, double goldReward, double respawnX, double respawnY) {

        super(x, y, width, height, health, totalHealth, damage, goldReward, respawnX, respawnY);

        this.respawnX = respawnX;
        this.respawnY = respawnY;
        this.setTotalCooldownTimer(100);
        this.setXSpeed(Constants.jumperSpeed);

    }
    /**
     * Jumper
     * constructor for obelisk enemy
     * @param x               - The x-coordinate of the jumper.
     * @param y               - The y-coordinate of the jumper.
     * @param width           - The width of the jumper.
     * @param height          - The height of the jumper.
     * @param health          - The current health of the jumper.
     * @param totalHealth     - The total health of the jumper.
     * @param damage          - The damage caused by the jumper.
     * @param goldReward      - The amount of gold rewarded for defeating the jumper.
     * @param respawnX        - The x-coordinate for the jumper's respawn point.
     * @param respawnY        - The y-coordinate for the jumper's respawn point.
     * @param obeliskEnemy    - Indicates if the jumper is an obelisk enemy.
     */
    Jumper(int x, int y, int width, int height, double health, double totalHealth, double damage, double goldReward, double respawnX, double respawnY, boolean obeliskEnemy) {

        super(x, y, width, height, health, totalHealth, damage, goldReward, respawnX, respawnY, obeliskEnemy);

        this.respawnX = respawnX;
        this.respawnY = respawnY;
        this.setTotalCooldownTimer(100);
        this.setXSpeed(Constants.jumperSpeed);

    }

    /**
     * move
     * Moves the jumper based on its behavior
     * @param player    - The player object.
     * @param proximity - The list of walls in proximity to the jumper.
     */

    public void move(Player player, ArrayList<Wall> proximity) {

        if (isAbilityActive()) {

            if (chargeUpCounter > 0) {
                chargeUp(player); // prepared the attack

            } else { // does the attack (jump calculatoins)
                this.translate(this.getXSpeed(), -this.getYSpeed());
                this.setYSpeed(this.getYSpeed() - Constants.gravity);

                if (landedFrenzy > Constants.jumperMaxJumps) {
                    landedFrenzy = 0;
                    this.setAbilityActive(false);
                    this.setCooldownTimerAbility(Constants.jumperAbilityCooldown);
                    chargeUpCounter = Constants.jumperGameLoopChargeUp;
                }

            }


        } else { // if ability not active

            double distance = Math.sqrt( Math.pow((player.getCenterY() - this.getCenterY()) , 2) +  Math.pow((player.getCenterX() - this.getCenterX()) , 2) );

            if (distance < Constants.jumperVision) { // if roughly in the area
                if (this.distanceToPlayer(player, proximity, false) <= Constants.jumperVision) { // raytracing

                    if (this.getCooldownTimerAbility() <= 0) { // if we are not in a cooldown start ability

                        this.setAbilityActive(true);
                        this.chargeUp(player);

                    } else { // if we are in cooldown, continue movement
                        this.translate(this.getXSpeed(), -this.getYSpeed());
                        this.setYSpeed(this.getYSpeed() - Constants.gravity);

                        if (this.getCooldownTimerAbility() > 0) {
                            this.setCooldownTimerAbility(this.getCooldownTimerAbility() - 1);
                        }

                    }
                } else { // if raytracing fails
                    this.translate(this.getXSpeed(), -this.getYSpeed());
                    this.setYSpeed(this.getYSpeed() - Constants.gravity);

                    if (this.getCooldownTimerAbility() > 0) {
                        this.setCooldownTimerAbility(this.getCooldownTimerAbility() - 1);
                    }
                }
            } else { // if not in area, continue movement, and then dont initate more

                this.translate(this.getXSpeed(), -this.getYSpeed());
                this.setYSpeed(this.getYSpeed() - Constants.gravity);

                if (this.getCooldownTimerAbility() > 0) {
                    this.setCooldownTimerAbility(this.getCooldownTimerAbility() - 1);
                }

            }
        }

        if (this.getXSpeed() > 0) {
            this.setDirection(1);
        } else if (this.getXSpeed() < 0){
            this.setDirection(-1);
        }
    }



    /**
     * chargeUp
     * Charges up the jumper before initiating the frenzy abilit
     * @param player - The player
     */
    public void chargeUp(Player player) {

        this.setXSpeed(this.getXSpeed() / 2);
        this.setYSpeed(this.getYSpeed() / 2);

        this.chargeUpCounter--;

        if (chargeUpCounter == 0) {
            setUpAttack(player);
        }

    }



    /**
     * setUpAttack
     * Sets up the attack ability for the jumpe
     * @param player - The player
     */

    public void setUpAttack(Player player) {

        double dX = player.getCenterX() - this.getCenterX();
        double dY = (this.getCenterY() - player.getCenterY());

        // some calculatoins for the jump to look good and then sets the movement
        if (dY <= 450) {
            double r1 = (-60 - Math.sqrt(3600 - 8 * dY))/-4;
            double r2 = (-60 + Math.sqrt(3600 - 8 * dY))/-4;
            double ticks = Math.max(r1, r2);
            this.setXSpeed((int) Math.round(dX/(ticks*2)));
            this.setYSpeed(60);
        } else {
            this.setYSpeed(80);
            this.setXSpeed((int) Math.round(dX/30));
        }

    }


    /**
     * collision
     * how jumper should respond when collididng with different objects
     * @param otherObject - other object
     */
    public void collision(GameObject otherObject) {

        if (otherObject instanceof Spike) {
            this.setHealth(-1);
        } else {

            double enemyBottom = this.getY() + this.getHeight();
            double otherObjectTop = otherObject.getY();
            double enemyRight = this.getX() + this.getWidth();
            double otherObjectLeft = otherObject.getX();
            double enemyLeft = this.getX();
            double otherObjectRight = otherObject.getX() + otherObject.getWidth();


            // if we colllide (land) on top of an object reset some values and change some speed to make it more physicy
            if ((enemyBottom > otherObjectTop) && (this.getY() + this.getYSpeed() < otherObjectTop) && (enemyRight - this.getXSpeed() - 2 > otherObjectLeft) && (enemyLeft - this.getXSpeed() + 2 < otherObjectRight)) {

                this.setLocation((int) this.getX(), (int) (otherObjectTop - this.getHeight()));
                this.setYSpeed(0);
                this.setXSpeed(this.getXSpeed()/2);

                this.landedFrenzy++;

                // if we colllide (below) on top of an object reset some values and change some speed to make it more physicy
            } else if (this.getY() < otherObjectTop + otherObject.getHeight() && enemyBottom > otherObjectTop + otherObject.getHeight() && (enemyRight - this.getXSpeed() - 2 > otherObjectLeft) && (enemyLeft - this.getXSpeed() + 2 < otherObjectRight)) {

                this.setLocation((int) this.getX(), (int) (otherObjectTop + otherObject.getHeight()));
                this.setYSpeed(0);

                this.landedFrenzy++;

                // if we colllide (right) on top of an object reset some values and change some speed to make it more physicy

            } else if (enemyRight > otherObjectLeft && enemyLeft < otherObjectLeft && enemyBottom > otherObjectTop && this.getY() < otherObjectTop + otherObject.getHeight()) {
                this.setLocation((int) (otherObjectLeft - this.getWidth()), (int) this.getY());
                this.setXSpeed(this.getXSpeed() * -1);

                this.landedFrenzy++;


                // if we colllide (left) on top of an object reset some values and change some speed to make it more physicy

            } else if (this.getX() < otherObjectRight && enemyRight > otherObjectRight && enemyBottom > otherObjectTop && this.getY() < otherObjectTop + otherObject.getHeight()) {
                this.setLocation((int) (otherObjectRight), (int) this.getY());
                this.setXSpeed(this.getXSpeed() * -1);

                this.landedFrenzy++;



            }
        }
    }

    /**
     * update
     * updates some counter
     */
    public void update() {

        this.immunityTick();

        this.setRespawnTimer(this.getRespawnTimer() - 1);



    }

}
