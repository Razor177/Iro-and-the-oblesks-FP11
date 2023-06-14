/**
 * File Name - [Enemy.java]
 * Description -  enemy
 * @Author - Michael Khart & Ian Leung
 * @Date - June 8, 2023
 */

import java.util.ArrayList;
abstract public class Enemy extends Alive {//

    private double damage;
    private double goldReward;
    private int respawnTimer;
    private int cooldownTimerAbility;
    private int totalCooldownTimer;
    private double respawnX, respawnY;

    private boolean obeliskEnemy;



    /**
     * Enemy
     Constructs an Enemy object with the specified parameters.
     @param x the x-coordinate of the enemy's position
     @param y the y-coordinate of the enemy's position
     @param width the width of the enemy
     @param height the height of the enemy
     @param health the current health of the enemy
     @param totalHealth the total health of the enemy
     @param damage the damage inflicted by the enemy
     @param goldReward the amount of gold rewarded upon defeating the enemy
     @param respawnX the x-coordinate of the enemy's respawn location
     @param respawnY the y-coordinate of the enemy's respawn location
     */
    Enemy(int x, int y, int width, int height, double health, double totalHealth, double damage, double goldReward, double respawnX, double respawnY) {
        super(x, y, width, height, health, totalHealth, 15);
        this.damage = damage;
        this.goldReward = goldReward;
        this.respawnX = respawnX;
        this.respawnY = respawnY;
        obeliskEnemy = false;
    }

    /**
    Enemy
     Constructs an Enemy object with the specified parameters, including the obeliskEnemy flag.
     @param x the x-coordinate of the enemy's position
     @param y the y-coordinate of the enemy's position
     @param width the width of the enemy
     @param height the height of the enemy
     @param health the current health of the enemy
     @param totalHealth the total health of the enemy
     @param damage the damage inflicted by the enemy
     @param goldReward the amount of gold rewarded upon defeating the enemy
     @param respawnX the x-coordinate of the enemy's respawn location
     @param respawnY the y-coordinate of the enemy's respawn location
     @param obeliskEnemy a flag indicating if the enemy is associated with an obelisk
     */
    Enemy(int x, int y, int width, int height, double health, double totalHealth, double damage, double goldReward, double respawnX, double respawnY, boolean obeliskEnemy) {
        super(x, y, width, height, health, totalHealth, 15);
        this.damage = damage;
        this.goldReward = goldReward;
        this.respawnX = respawnX;
        this.respawnY = respawnY;
        this.obeliskEnemy = obeliskEnemy;
    }




    public abstract void move(Player player, ArrayList<Wall> proximity); // how all the enemies will move

    public abstract void update(); // something to update all their counters

    abstract public void collision(GameObject otherObject); // how they will interact with other objects


    /**
     * distanceToPlayer
     * uses ray tracing and an advanced calucations for line length between objects
     * @param player - the player
     * @param listObjects - the list of walls in game
     * @param justDistance - boolean if the clal wants just a distance return without raytracing
     * @return - distance
     */
    public double distanceToPlayer(Player player, ArrayList<Wall> listObjects, boolean justDistance) {

        double distance;

        boolean enemyVerticleExit = false;
        boolean enemyHorizontalExit = false;

        boolean playerVerticleExit = false;
        boolean playerHorizontalExit = false;

        double enemyYpointIntersection = 0;
        double enemyXpointIntersection = 0;

        double playerYpointIntersection = 0;
        double playerXpointIntersection = 0;

        String playerPosition = "";
        String enemyPosition = "";

        double enemyY = 0;
        double playerY = 0;

        double m, b;

        // finding the equation of line between centers
        if (Math.abs(player.getCenterX()) - Math.abs(this.getCenterX()) == 0) {
            m = Integer.MAX_VALUE;
        } else {
            m = ((player.getCenterY() - this.getCenterY()) / (player.getCenterX() - this.getCenterX()));
        }

        b = ((this.getCenterY() - (m * this.getCenterX())));

        enemyY = ((m * this.getX()) + b);
        playerY = ((m * player.getX()) + b);


        // finding if the line exits vertically or horizontally for enemy and player
        if ((enemyY <= this.getY() + this.getHeight()) && (enemyY >= this.getY())) {
            enemyHorizontalExit = true;
        } else {
            enemyVerticleExit = true;
        }

        if ((playerY <= player.getY() + player.getHeight()) && (playerY >= player.getY())) {
            playerHorizontalExit = true;
        } else {
            playerVerticleExit = true;
        }


        // seeing which side it comes out of for both player and enenmy
        if (enemyHorizontalExit) {
            if (player.getX() - this.getX() >= 0) {
                playerPosition = "right";
                enemyPosition = "left";
            } else if (player.getX() - this.getX() < 0) {
                playerPosition = "left";
                enemyPosition = "right";

            }


        } else if (enemyVerticleExit) {

            if (player.getY() - this.getY() >= 0) {
                playerPosition = "down";
                enemyPosition = "up";
            } else if (player.getY() - this.getY() < 0) {
                playerPosition = "up";
                enemyPosition = "down";
            }

        }



        // finding the point of exit based on the exit sides for both player and enemy
        // find the unknown cordinate on the slide
        if (enemyVerticleExit) {

            if (playerPosition.equals("up")) {
                enemyYpointIntersection = (this.getY());
                enemyXpointIntersection = ((enemyYpointIntersection - b) / m);

            } else if (playerPosition.equals("down")) {
                enemyYpointIntersection = (this.getY() + this.getHeight());
                enemyXpointIntersection = ((enemyYpointIntersection - b) / m);

            }

        } else if (enemyHorizontalExit) {

            if (playerPosition.equals("right")) {
                enemyXpointIntersection = (this.getX() + this.getWidth());
                enemyYpointIntersection = ((m * enemyXpointIntersection) + b);

            } else if (playerPosition.equals("left")) {
                enemyXpointIntersection = (this.getX());
                enemyYpointIntersection = ((m * enemyXpointIntersection) + b);
            }

        }


        if (playerVerticleExit) {

            if (enemyPosition.equals("up")) {
                playerYpointIntersection = (player.getY());
                playerXpointIntersection = ((playerYpointIntersection - b) / m);

            } else if (enemyPosition.equals("down")) {
                playerYpointIntersection = (player.getY() + player.getHeight());
                playerXpointIntersection = ((playerYpointIntersection - b) / m);

            }

        } else if (playerHorizontalExit) {

            if (enemyPosition.equals("right")) {
                playerXpointIntersection = (player.getX() + player.getWidth());
                playerYpointIntersection = ((m * playerXpointIntersection) + b);

            } else if (enemyPosition.equals("left")) {
                playerXpointIntersection = (player.getX());
                playerYpointIntersection = ((m * playerXpointIntersection) + b);
            }

        }

        // this is the ditance between both objects excluding their widths and lengths
        distance = Math.sqrt( Math.pow((playerXpointIntersection - enemyXpointIntersection), 2) + Math.pow((playerYpointIntersection - enemyYpointIntersection), 2) );

        if (justDistance) { // return if they want just distance
            return distance;
        }

        // number of points for raytracing to check
        int numXPoints = (int) Math.abs(enemyXpointIntersection - playerXpointIntersection) / Constants.rayTracingStep;
        int numYPoints = (int) Math.abs(enemyYpointIntersection - playerYpointIntersection) / Constants.rayTracingStep;

        double testX = enemyXpointIntersection;
        double testY = enemyYpointIntersection;

        boolean intersected = false;

        // checking all the points across the line for both x and y distance of raytracing step - there to stop a very verticle or horizontal line from breaking code
        // uses known exit points and sides to check all points
        if (enemyVerticleExit) {

            if (playerPosition.equals("up")) {

                for (int i = 1; i < (numYPoints - 1); i++) {

                    testY = enemyYpointIntersection - (i * Constants.rayTracingStep);
                    testX = ((testY - b) / m);

                    for (GameObject gameObject: listObjects) {
                        if (gameObject.contains(testX, testY)) {
                            intersected = true;
                        }
                    }


                }



            } else if (playerPosition.equals("down")) {

                for (int i = 1; i < (numYPoints - 1); i++) {

                    testY = enemyYpointIntersection + (i * Constants.rayTracingStep);
                    testX = ((testY - b) / m);


                    for (GameObject gameObject: listObjects) {
                        if (gameObject.contains(testX, testY)) {
                            intersected = true;
                        }
                    }

                }

            }

        } else if (enemyHorizontalExit) {

            if (playerPosition.equals("right")) {

                for (int i = 1; i < (numXPoints - 1); i++) {

                    testX = enemyXpointIntersection + (i * Constants.rayTracingStep);
                    testY = (m * testX) + b;


                        for (GameObject gameObject: listObjects) {
                        if (gameObject.contains(testX, testY)) {
                            intersected = true;
                        }
                    }

                }


            } else if (playerPosition.equals("left")) {


                for (int i = 1; i < (numXPoints - 1); i++) {

                    testX = enemyXpointIntersection - (i * Constants.rayTracingStep);
                    testY = (m * testX) + b;


                    for (GameObject gameObject: listObjects) {
                        if (gameObject.contains(testX, testY)) {
                            intersected = true;
                        }
                    }

                }

            }


        }



        if (!(intersected)) { // returns the distance if there is nothing between
            return distance;
        } else {
            return Integer.MAX_VALUE; // this means there is wall between so infinite length
        }

    }


    /**
     * knockback
     * physics of how knockback should work
     * @param attack- incoing attack - for calculations
     */
    public void knockback(Attack attack) {

        double interval, dX, dY;

        if (this.getImmunityTimer() == 0) { // if we can take damage

            if (attack instanceof Explosion) { // if we hit by rocket, set x and y spped from rocket x and y and take damage
                dX = (this.getCenterX() - attack.getCenterX());
                dY = (attack.getCenterY() - this.getCenterY());
                double distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
                distance -= Math.sqrt((Math.pow(this.getWidth()/2, 2) + Math.pow(this.getHeight()/2, 2)));
                if (distance <= ((Explosion) attack).getRadius()) {
                    interval = 75/(Math.abs(dX) + Math.abs(dY) + 1);
                } else {
                    interval = 0;
                }

            } else { // otherwise just knockback away from attack and take damage without such complex speed calculations
                this.setXSpeed((int) (attack.getAttackDamage() * attack.getDirection()));
                this.setYSpeed(10);
                dX = (this.getCenterX() - attack.getCenterX());
                dY = (attack.getX() + attack.getHeight() - this.getCenterY());
                interval = 15/(Math.abs(dX) + Math.abs(dY) + 1);
            }

            this.setImmunityTimer(1);
            this.setHealth(this.getHealth() - attack.getAttackDamage());
            this.setXSpeed(this.getXSpeed() + (int) (dX * interval));
            this.setYSpeed(this.getYSpeed() + (int) (dY * interval));
        }
    }


    /**
     * onEdge
     * checks if the enemy is on an edge of an object
     * @param otherObject - collider
     * @return - if we are on edge
     */
    public boolean onEdge(GameObject otherObject) {

        double bottomRightX = (int) (this.getX() + this.getWidth());
        double middleBottomRightX = bottomRightX - 10;
        double bottomY = (int) (this.getY() + this.getHeight());
        double middleBottomLeftX = this.getX() + 10;


        // if we are touching half on the collider and other half not on the collider (roughly)
        if (   ( ((otherObject.contains(bottomRightX, bottomY)) && ((otherObject.contains(middleBottomRightX, bottomY))) )  && ((!otherObject.contains(this.getX(), bottomY)) && (!otherObject.contains(middleBottomLeftX, bottomY)) ) )
            || ( ((!otherObject.contains(bottomRightX, bottomY)) && ((!otherObject.contains(middleBottomRightX, bottomY))) )  && ((otherObject.contains(this.getX(), bottomY)) && (otherObject.contains(middleBottomLeftX, bottomY)) ) )   )
        {
            this.setHealth(-1);
            return true;

        // if we are touching at three points move back away from ledge

        } else if (    ( ((otherObject.contains(middleBottomRightX, bottomY)) && (otherObject.contains(middleBottomLeftX, bottomY))) && (((otherObject.contains(this.getX(), bottomY)) || (otherObject.contains(bottomRightX, bottomY))) && ((!otherObject.contains(this.getX(), bottomY)) || (!otherObject.contains(bottomRightX, bottomY)) )    )          )
                &&   ( (((!otherObject.contains(this.getX(), bottomY)) || (!otherObject.contains(bottomRightX, bottomY))) && ((!otherObject.contains(this.getX(), bottomY)) || (!otherObject.contains(bottomRightX, bottomY)) ) )    )    ) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * getrespawnx
     * gets the respawnX
     * @return respawnx
     */
    public double getRespawnX() {
        return respawnX;
    }

    /**
     * getRespawnY
     * get respawn y
     * @return respawn y
     */
    public double getRespawnY() {
        return respawnY;
    }

    /**
     * respawnaX
     * retunrs respawn x
     * @param respawnX
     */
    public void setRespawnX(double respawnX) {
        this.respawnX = respawnX;
    }

    /**
     * sterespawnY
     * set respawny
     * @param respawnY
     */
    public void setRespawnY(double respawnY) {
        this.respawnY = respawnY;
    }

    /**
     * getDamage
     * get damage
     * @return damage
     */
    public double getDamage() {
        return damage;
    }

    /**
     * get gold reward
     * @return gold reawed
     */
    public double getGoldReward() {
        return goldReward;
    }

    /**
     * getRespanwtimer
     * gets respawn timer
     * @return - respawntimer
     */
    public int getRespawnTimer() {
        return respawnTimer;
    }


    /**
     * set repsawn tiemr
     * @param respawnTimer
     */

    public void setRespawnTimer(int respawnTimer) {
        this.respawnTimer = respawnTimer;
    }

    /**
     * gets gooldownability
     * @return - cooldown ability
     */
    public int getCooldownTimerAbility() {
        return cooldownTimerAbility;
    }

    /**
     * sets cooldowntimer
     * @param cooldownTimerAbility - timer
     */
    public void setCooldownTimerAbility(int cooldownTimerAbility) {
        this.cooldownTimerAbility = cooldownTimerAbility;
    }

    /**
     * gettotal timer
     * @return gettotal cooldowntimer
     */
    public int getTotalCooldownTimer() {
        return totalCooldownTimer;
    }

    /**
     * setstotalcooldowntier
     * @param totalCooldownTimer - totalcooldowntimer
     */
    public void setTotalCooldownTimer(int totalCooldownTimer) {
        this.totalCooldownTimer = totalCooldownTimer;
    }

    /**
     * if oblesk enemy
     * @return
     */
    public boolean isObeliskEnemy() {
        return obeliskEnemy;
    }

}
