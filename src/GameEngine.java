/**
 * [GameEngine.java]
 * This class is the engine and brain of the game, allowing it to run
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class GameEngine {

    private Player player;
    private ArrayList<Wall> surroundings;
    private ArrayList<Attack> attacks;
    private boolean abilityActive;
    private boolean attackActive;
    private ArrayList<Enemy> enemies;
    private ArrayList<Enemy> respawnList;
    private ArrayList<Orb> orbs;

    private int frameNum;


    boolean playing = true;

    private int refreshDelay;

    private ArrayList<Rectangle> obeliskHitboxes;

    private boolean inObelisk = false;

    private int currentObelisk = 0;

    private ArrayList<int[]> obeliskSpawns;

    private int requiredKills = 0;

    private boolean inShop = false;

    private ArrayList<ShopItem> shop;

    GameEngine() throws FileNotFoundException {

        // Checks for default or new save
        Scanner input = new Scanner(new File("src/Save2.txt"));
        if (input.next().equals("new")) {
            input = new Scanner(new File("src/Save.txt"));
        }
        this.player = new Player(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextInt(), input.nextBoolean(), input.nextBoolean(), input.nextDouble(), input.nextInt());
        this.surroundings = new ArrayList<>();
        this.attacks = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.orbs = new ArrayList<>();
        this.respawnList = new ArrayList<>();
        this.obeliskHitboxes = new ArrayList<>();
        this.shop = new ArrayList<>();
        this.obeliskSpawns = new ArrayList<>();

        this.refreshDelay = 17;

        // Reading save file
        while (input.hasNext()) {
            String objectType = input.next();
            switch (objectType) {
                case "Wall":
                    surroundings.add(new Wall(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextBoolean()));
                    break;
                case "Spike":
                    surroundings.add(new Spike(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt()));
                    break;
                case "Crystal":
                    surroundings.add(new Crystal(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.next()));
                    break;
                case "Slime":
                    enemies.add(new Slime(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt()));
                    break;
                case "Mosquito":
                    enemies.add(new Mosquito(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt()));
                    break;
                case "Jumper":
                    enemies.add(new Jumper(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt()));
                    break;
                case "Hitbox":
                    obeliskHitboxes.add(new Rectangle(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt()));
                    break;
                case "Shop":
                    shop.add(new ShopItem(input.nextInt(),
                            input.nextInt(),
                            input.nextInt(),
                            input.nextInt(),
                            input.next(),
                            input.nextInt()));
                    break;
                case "Weapon":
                    player.getWeapons().add(input.next());
                    break;
            }
        }

        input.close();
    }


    /**
     * moveAll
     * This method moves, and ticks all the objects on the map
     */
    public void moveAll() {

        if (inObelisk) {
            obeliskTick();
        }

        player.move();
        player.immunityTick();

        // Respawning
        if (player.getHealth() <= 0) {

            // Ends obelisk
            if (inObelisk) {
                for (int i = enemies.size() - 1; i >= 0; i --) {
                    Enemy enemy = enemies.get(i);
                    if (enemy.getRespawnY() > 99999) {
                        respawnList.add(enemy);

                        enemy.setRespawnTimer(Constants.respawnTimerEnemy);

                        enemies.remove(i);

                    }
                }

                for (int i = 0; i < 4; i ++) {
                    surroundings.remove(surroundings.size() - 1);
                }
                inObelisk = false;
            }
            player.respawn();
        }

        // Enemies
        for (int i = enemies.size() - 1; i >= 0; i --) {

            Enemy enemy = enemies.get(i);
            enemy.update();

            if (enemy.getHealth() > 0) {

                enemy.move(player, surroundings);

            } else if (enemy.getHealth() <= 0) { // Dead enemy

                if (enemy.isObeliskEnemy()) {
                    requiredKills --;
                }

                createOrbs(enemy);

                // Respawning
                respawnList.add(enemy);

                enemy.setRespawnTimer(Constants.respawnTimerEnemy);

                enemies.remove(i);
            }
        }



        // Attacks
        for (int i = attacks.size() - 1; i >= 0; i --) {
            Attack attack = attacks.get(i);

            // Attack duration functionality
            if (attack.getAbilityDuration() > attack.getMaxAbilityDuration()) {
                if (attack instanceof Rocket) {
                    attacks.set(i, new Explosion((int) (attack.getCenterX()), (int) (attack.getCenterY()), true, 300, player.getDamageBoost()));
                } else {
                    attacks.remove(i);
                }
            }

            if (attack instanceof Projectile) {
                ((Projectile) attack).move();
            }

            if (attack instanceof Explosion) {
                ((Explosion) attack).expand();
            }

            attack.setAbilityDuration(attack.getAbilityDuration() + 1);
        }

        // Orbs
        for (int i = orbs.size() - 1; i >= 0; i --) {
            if (!orbs.get(i).move(player)) {
                orbs.remove(i);
            }
        }

        // Enemy respawning
        for (Enemy enemy : respawnList) {

            // Respawns once timer is ended
            if ((enemy.getRespawnTimer() == 0) && (((enemy.getRespawnX()) != 0 && (enemy.getRespawnY() != 0)))) {

                enemy.setXSpeed(0);
                enemy.setYSpeed(0);
                enemy.setAbilityActive(false);
                enemy.setImmunityTimer(10);

                enemy.setLocation((int) enemy.getRespawnX(), (int) enemy.getRespawnY());
                if (enemy instanceof Slime) {
                    enemy.setHealth(Constants.slimeTotalHealth);

                } else if (enemy instanceof Mosquito) {
                    enemy.setHealth(Constants.mosquitoTotalHealth);

                } else if (enemy instanceof Jumper) {
                    enemy.setHealth(Constants.jumperMaxHP);
                }

                enemies.add(enemy);

            }
            enemy.update();

        }


        // Some enemies do not respawn
        respawnList.removeIf(enemy -> enemy.getRespawnTimer() < 0);
        respawnList.removeIf(enemy -> (enemy.getRespawnY() > 99999) );

    }

    /**
     * checkCollisions
     * This method handles the collisions in the game
     */
    public void checkCollisions() {
        // Checks walls
        for (Wall object: surroundings) {

            // Crystals only have collisions if they are alive
            if (object instanceof Crystal) {

                ((Crystal) object).crystalTick();

                if (((Crystal) object).getRespawnTimer() == 0) {

                    if (player.intersects(object)) {
                        player.collide(object);
                        player.setAbilityActive(false);
                    }
                }

            } else if (player.intersects(object)) {
                player.collide(object);
                player.setAbilityActive(false);
            }

            // Collision for enemies and wall
            for (Enemy enemy: enemies) {

                if (enemy.intersects(object)) {
                    enemy.collision(object);
                }

                if (player.intersects(enemy)) {
                    player.collide(enemy);
                    player.setAbilityActive(false);
                }
            }


            for (Orb orb : orbs) {
                if (orb.intersects(object)) {
                    orb.collision(object);
                }
            }
        }

        // Attack collisions
        for (int i = attacks.size() - 1; i >= 0; i --) {

            Attack attack = attacks.get(i);
            for (Enemy enemy: enemies) {

                if (enemy.intersects(attack) && attack.isFriendly()) {

                    // Rocket explodes on hit
                    if (attack instanceof Rocket) {
                        attacks.set(i, new Explosion((int) (attack.getCenterX()), (int) (attack.getCenterY()), true, 300, player.getDamageBoost()));
                    } else {
                        enemy.knockback(attack);
                    }
                }
            }

            for (Wall wall: surroundings) {

                if (wall instanceof Crystal) {

                    if (attack.intersects(wall)) {

                        // Crystal takes damage
                        ((Crystal) wall).takeHit(attack);

                        if (((Crystal) wall).getHealth() <= 0) {
                            createOrbs(wall);
                        }
                    }

                } else if (attack instanceof Projectile) {

                    if (attack.intersects(wall)) {

                        // Rockets explode
                        if (attack instanceof Rocket) {

                            attacks.set(i, new Explosion((int) (attack.getCenterX()), (int) (attack.getCenterY()), true, 300, player.getDamageBoost()));

                        } else {
                            attacks.remove(i);
                            break;
                        }
                    }
                }
            }
        }


        for (int i = orbs.size() - 1; i >= 0; i -- ) {

            if (orbs.get(i).intersects(player)) {

                orbAbsorb(orbs.get(i));
                orbs.remove(i);
            }
        }
    }

    /**
     * createOrbs
     * this method creates orbs from a dead object
     * @param object object to create orbs from
     */
    public void createOrbs(GameObject object) {

        if (object instanceof Enemy) {

            Enemy enemy = (Enemy) object;

            // Orb spawning
            for (int i = (int) enemy.getGoldReward(); i > 0; i -= 10) {
                orbs.add(new Orb((int) enemy.getCenterX(), (int) enemy.getCenterY(), Constants.orbDimensions, Constants.orbDimensions, 10, "Gold"));
            }

            // Health/energy orbs have a chance to spawn
            for (int i = 0; i < 3; i ++) {
                if (Math.random() < enemy.getGoldReward() / 800.0) {
                    orbs.add(new Orb((int) enemy.getCenterX(), (int) enemy.getCenterY(), Constants.orbDimensions, Constants.orbDimensions, 25, "Health"));

                } else if (Math.random() < enemy.getGoldReward() / 1200.0) {
                    orbs.add(new Orb((int) enemy.getCenterX(), (int) enemy.getCenterY(), Constants.orbDimensions, Constants.orbDimensions, 20, "Energy"));
                }
            }


        } else {
            Crystal crystal = (Crystal) object;
            for (int i = crystal.getBoostValue(); i > 0; i = i - 10) {
                orbs.add(new Orb((int) crystal.getCenterX(), (int) crystal.getCenterY(), Constants.orbDimensions, Constants.orbDimensions, 10, crystal.getBoostType()));
            }
        }
    }

    /**
     * createOrbs
     * Creates orbs based on input instead of object
     * @param orbAmount how many orbs to make
     * @param hitbox obelisk to spawn from
     * @param orbType type of orb
     */
    public void createOrbs(int orbAmount, Rectangle hitbox, String orbType) {
        for (int i = orbAmount; i > 0; i -= 10) {
            orbs.add(new Orb((int) (hitbox.getX() + hitbox.getWidth() * Math.random()), (int) (hitbox.getY() + Math.random() * 200), Constants.orbDimensions, Constants.orbDimensions, 10, orbType));
        }
    }

    /**
     * orbAbsorb
     * Player absorbs orb method
     * @param orb the orb
     */
    public void orbAbsorb(Orb orb) {
        // Effect depends on orb type
        switch (orb.getBoostType()) {
            case "Gold":
                player.setTotalGold((int) (player.getTotalGold() + orb.getBoostValue()));
                break;

            case "Health":
                player.setHealth(player.getHealth() + orb.getBoostValue());
                break;

            case "Energy":
                player.setEnergy(player.getEnergy() + orb.getBoostValue());
                break;
        }
    }

    /**
     * activateObelisk
     * Activates an obelisk
     * @param obeliskNum the obelisk in question
     */
    public void activateObelisk(int obeliskNum) {
        this.inObelisk = true;
        this.currentObelisk = obeliskNum;
        this.obeliskSpawns.clear();

        Wall baseWall = surroundings.get(0);

        // Walls to prevent player from exiting obelisk
        surroundings.add(new Wall((int) (baseWall.getX() + 7400), (int) (baseWall.getY() -900), 100, 300, false));
        surroundings.add(new Wall((int) (baseWall.getX() + 5800), (int) (baseWall.getY() -900), 100, 300, false));
        surroundings.add(new Wall((int) (baseWall.getX() + 12100), (int) (baseWall.getY() -4800), 100, 200, false));
        surroundings.add(new Wall((int) (baseWall.getX() + 20400), (int) (baseWall.getY() -800), 100, 300, false));

        // Obelisk waves are based on obelisk number
        switch (obeliskNum) {
            case 0:
                this.obeliskSpawns.add(new int[] {25, 0, 0});
                requiredKills = 25;
                break;

            case 1:
                // Each array represents a wave
                this.obeliskSpawns.add(new int[] {0, 5, 0});
                this.requiredKills = 5;
                this.obeliskSpawns.add(new int[] {5, 3, 0});
                this.obeliskSpawns.add(new int[] {0, 8, 0});
                this.obeliskSpawns.add(new int[] {10, 5, 0});
                break;

            case 2:
                this.obeliskSpawns.add(new int[] {0, 0, 4});
                this.requiredKills = 4;
                this.obeliskSpawns.add(new int[] {5, 3, 2});
                this.obeliskSpawns.add(new int[] {8, 2, 4});
                this.obeliskSpawns.add(new int[] {0, 5, 5});
                this.obeliskSpawns.add(new int[] {8, 10, 6});
                break;
        }
    }


    /**
     * obeliskTick
     * This method ticks an obelisk
     */
    public void obeliskTick() {
        // When all enemies are killed
        if (requiredKills <= 0) {
            requiredKills = 0;

            // Moves to next wave
            obeliskSpawns.remove(0);

            // If all waves are cleared
            if (obeliskSpawns.size() == 0) {

                // Removes obelisk walls
                for (int i = 0; i < 4; i ++) {
                    surroundings.remove(surroundings.size() - 1);
                }

                this.inObelisk = false;

                // Reward depends on which obelisk
                switch (currentObelisk) {
                    case 0:
                        if (player.getMaxJumps() < 2) {
                            player.setMaxJumps(2);
                        }
                        if (!player.isDashUnlocked()) {
                            player.setMaxHealth(player.getMaxHealth() + 100);
                            player.setDashUnlocked(true);
                        }

                        createOrbs(2000, obeliskHitboxes.get(0), "Gold");

                        break;

                    case 1:
                        if (!player.isBashUnlocked()) {
                            player.setMaxHealth(player.getMaxHealth() + 100);
                            player.setBashUnlocked(true);
                            createOrbs(4000, obeliskHitboxes.get(1), "Gold");
                        }
                        break;

                    case 2:
                        createOrbs(8000, obeliskHitboxes.get(2), "Gold");
                        break;
                }

            // Next wave
            } else {
                for (int i = 0; i < this.obeliskSpawns.get(0).length; i++) {
                    requiredKills += this.obeliskSpawns.get(0)[i];
                }

                // Orbs to heal player
                createOrbs(30, obeliskHitboxes.get(currentObelisk), "Health");
                createOrbs(50, obeliskHitboxes.get(currentObelisk), "Energy");
            }


        } else {

            Rectangle obelisk = this.obeliskHitboxes.get(this.currentObelisk);

            // Loops through enemies to spawn
            for (int i = 0; i < obeliskSpawns.get(0).length; i ++) {

                if (obeliskSpawns.get(0)[i] > 0) {

                    if (Math.random() < 0.1) {
                        obeliskSpawns.get(0)[i] --;

                        // index of number represents a type of enemy to spawn
                        switch (i) {
                            case 0:
                                enemies.add(new Slime((int) (obelisk.getX() - 100 + ((int) (Math.random() * 2)) * (obelisk.getWidth() + 200)), (int) (obelisk.getY() + obelisk.getHeight() - 100), 100, 100, 100, 100, 15, 0, 0, 150000, true));
                                break;

                            case 1:
                                enemies.add(new Mosquito((int) (obelisk.getX() + Math.random() * (obelisk.getWidth() - 50)), (int) (obelisk.getY() + Math.random() * (obelisk.getHeight() + 50)), 50, 50, 100, 100, 20, 0, 0, 150000, true));
                                break;

                            case 2:
                                enemies.add(new Jumper((int) (obelisk.getX() - 150 + Math.random() * (obelisk.getWidth() - 150)), (int) (obelisk.getY() + obelisk.getHeight() - 100), 150, 150, 150, 150, 40, 0, 0, 150000, true));
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * save
     * This method saves the progress
     * @throws FileNotFoundException if file not found
     */
    public void save() throws FileNotFoundException {

        // Saves to non-base file
        PrintWriter output = new PrintWriter(new File("src/Save2.txt"));
        output.println("notNew");

        //Player save
        output.println(player.getRespawnPoint()[0] + " " + player.getRespawnPoint()[1] + " " + (int) player.getWidth() + " " + (int) player.getHeight() + " " +
                (int) player.getHealth() + " " + (int) player.getMaxHealth() + " " + (int) player.getEnergy() + " " + (int) player.getMaxEnergy() + " " +
                player.getMaxJumps() + " " + player.isDashUnlocked() + " " + player.isBashUnlocked() + " " + player.getDamageBoost() + " " + (int) player.getTotalGold());

        // Weapons save
        for (String weapon: player.getWeapons()) {
            output.println("Weapon " + weapon);
        }

        // Walls save
        for (Wall wall: this.surroundings) {
            if (wall instanceof Spike){
                output.println(wall.getClass().getName() + " " + (int) wall.getX() + " " + (int) wall.getY() + " "  + (int) wall.getWidth() + " " + (int) wall.getHeight() + " " + (int) ((Spike) wall).getDamage());

            } else {
                output.println(wall.getClass().getName() + " " + (int) wall.getX() + " " + (int) wall.getY() + " "  + (int) wall.getWidth() + " " + (int) wall.getHeight() + " " + (wall instanceof Crystal ? ((Crystal) wall).getBoostType() :  wall.isrespawnable()));
            }
        }

        // Enemy save
        for (Enemy enemy: this.enemies) {
            output.println(enemy.getClass().getName() + " " + (int) enemy.getX() + " " + (int) enemy.getY() + " " + (int) enemy.getWidth() + " " + (int) enemy.getHeight() + " " +
                    (int) enemy.getHealth() + " " + (int) enemy.getMaxHealth() + " " + (int) enemy.getDamage() + " " + (int) enemy.getGoldReward() + " " + (int) enemy.getRespawnX() + " " + (int) enemy.getRespawnY());
        }

        // Shop save
        for (ShopItem item: this.shop) {
            output.println("Shop " + (int) item.getX() + " " + (int) item.getY() + " " + (int) item.getWidth() + " " + (int) item.getHeight() + " " + item.getName() + " " + item.getPrice());
        }

        // Obelisk save
        for (Rectangle hitbox: this.obeliskHitboxes) {
            output.println("Hitbox " + (int) hitbox.getX() + " " + (int) hitbox.getY() + " " + (int) hitbox.getWidth() + " " + (int) hitbox.getHeight());
        }

        output.close();
    }



    // ================================================================
    // Getters and setters
    // ================================================================

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Attack> getAttacks() {
        return attacks;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Wall> getSurroundings() {
        return surroundings;
    }

    public int getRefreshDelay() {
        return refreshDelay;
    }

    public void setRefreshDelay(int refreshDelay) {
        this.refreshDelay = refreshDelay;
    }

    public ArrayList<Orb> getOrbs() {
        return orbs;
    }


    public ArrayList<Enemy> getRespawnList() {
        return respawnList;
    }

    public ArrayList<Rectangle> getObeliskHitboxes() {
        return obeliskHitboxes;
    }

    public boolean isInObelisk() {
        return inObelisk;
    }

    public boolean isInShop() {
        return inShop;
    }

    public void setInShop(boolean inShop) {
        this.inShop = inShop;
    }

    public ArrayList<ShopItem> getShop() {
        return shop;
    }

}