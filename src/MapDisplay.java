/**
 * [MapDisplay.java]
 * This class displays the game onto swing, and uses camera movement relative to the player
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

public class MapDisplay extends JFrame {
    // Game Window properties
    static GraphicsPanel canvas;
    static final int WIDTH = 1920;
    static final int HEIGHT = 1080;

    static int cameraX = 0;

    static int cameraY = 0;

    static int lastCamX;

    static int lastCamY;

    static int bowPower;

    boolean bowCharging;

    static GameEngine game;

    Player player;

    static boolean aimingBash = false;

    static double bashAngle = 0;

    static BufferedImage bashAimImage;

    static BufferedImage[] weaponIcons = new BufferedImage[4];

    static BufferedImage[] playerFrames = new BufferedImage[7];

    static BufferedImage slime;

    static BufferedImage mosquito;

    static BufferedImage jumper;

    static BufferedImage eButton;

    static BufferedImage eCrystal;

    static BufferedImage hCrystal;

    static Rectangle screenRect = new Rectangle(0, 0, 1920, 1080);
    static Rectangle buyRect = new Rectangle(560, 800, 200, 50);

    static Rectangle cancelRect = new Rectangle(1160, 800, 200, 50);

    static int currentShopItem = 0;

    /**
     * MapDisplay
     * Creates a display
     * @param game game to display
     */
    MapDisplay(GameEngine game){
        super("Game Window");
        this.setSize(WIDTH,HEIGHT);

        MapDisplay.game = game;

        this.player = game.getPlayer();
        cameraX = (int) player.getX() - 900;
        cameraY = (int) player.getY() - 500;
        bowPower = 1;
        this.bowCharging = false;

        // Images
        try {
            bashAimImage = image("Pictures/arrow.png");
            weaponIcons[0] = image("Pictures/SwordIcon.png");
            weaponIcons[1] = image("Pictures/HammerIcon.png");
            weaponIcons[2] = image("Pictures/BowIcon.png");
            weaponIcons[3] = image("Pictures/RocketIcon.png");

            for (int i = 0; i < playerFrames.length; i ++) {
                playerFrames[i] = image("Pictures/player" + i + ".png");
            }

            slime = image("Pictures/slime.png");
            mosquito = image("Pictures/mosquito.png");
            jumper = image("Pictures/jumper.png");
            eButton = image("Pictures/e.png");
            eCrystal = image("Pictures/eCrystal.png");
            hCrystal = image("Pictures/hCrystal.png");

            System.out.println("e");
        } catch (IOException ex){
            System.out.println("a");
        }


        canvas = new GraphicsPanel();
        this.add(canvas);

        // Listeners
        addKeyListener(new Keyboard());
        addMouseListener(new Mouse());
        addMouseMotionListener(new Mouse());

        this.setVisible(true);

    } // main method end

    /**
     * Image
     * Loads an image
     * @param path image location
     * @return the image
     * @throws IOException yes
     */
    public BufferedImage image(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    /**
     * refresh
     * This method refreshes the game to display and handle camera
     */
    public void refresh() {
        this.repaint();

        // Charges bow
        if (bowCharging && bowPower < 50) {
            bowPower ++;
        }

        // Moves the camera relative to the player
        cameraX = (int) player.getX() - 900;
        cameraY = (int) player.getY() - 500;

        int dX = lastCamX - cameraX;
        int dY = lastCamY - cameraY;

        // Camera has a max speed of 26
        if ((Math.abs(dX) > 26) && (Math.abs(dX) < 500)) {
            dX = dX/Math.abs(dX) * 26;
        }

        if ((Math.abs(dY) > 26) && (Math.abs(dY) < 500)) {
            dY = dY/Math.abs(dY) * 26;
        }



        // Translates all objects relative to the camera
        player.translate(dX, dY);

        player.setRespawnPoint(new int[]{player.getRespawnPoint()[0] + dX, player.getRespawnPoint()[1] + dY});

        for (int i = 0; i < game.getSurroundings().size(); i ++) {
            game.getSurroundings().get(i).translate(dX, dY);
        }

        for (Enemy enemy: game.getEnemies()) {
            enemy.setRespawnX((enemy.getRespawnX() + dX));
            enemy.setRespawnY((enemy.getRespawnY() + dY));

            enemy.translate(dX, dY);



        }

        for (Enemy enemy : game.getRespawnList()) {

            enemy.setRespawnX((enemy.getRespawnX() + dX));
            enemy.setRespawnY((enemy.getRespawnY() + dY));

        }

        for (int i = 0; i < game.getAttacks().size(); i ++) {
            Attack attack = game.getAttacks().get(i);
            if (attack instanceof Projectile || attack instanceof Explosion) {
                attack.translate(dX, dY);
            }
        }

        for (Orb orb: game.getOrbs()) {
            orb.translate(dX, dY);
        }

        for (Rectangle hitbox: game.getObeliskHitboxes()) {
            hitbox.translate(dX, dY);
        }

        for (ShopItem item: game.getShop()) {
            item.translate(dX, dY);
        }

    }

    /**
     * processWindowEvent
     * handles window closing
     * @param e  the window event
     */
    protected void processWindowEvent(WindowEvent e) {

        // Saves before closing the game
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            try {
                game.save();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        super.processWindowEvent(e);
    }

    static class GraphicsPanel extends JPanel{
        public GraphicsPanel(){
        }

        /**
         * paintComponent
         * this method draws the game
         * @param g the <code>Graphics</code> object to protect
         */
        public void paintComponent(Graphics g){
            super.paintComponent(g); //required
            Graphics2D g2d = (Graphics2D) g;

            // Draws walls
            for (Wall wall: game.getSurroundings()) {

                if (wall.intersects(screenRect)) {

                    g.setColor(Color.darkGray);

                    if (wall instanceof Spike) {
                        g2d.setColor(Color.red);
                    }

                    // Crystals have seperate images
                    if (wall instanceof Crystal) {
                        Crystal crystal = (Crystal) wall;

                        if (crystal.getRespawnTimer() == 0) {

                            if (crystal.getBoostType().equals("Energy")) {
                                g2d.drawImage(eCrystal, (int) crystal.getX(), (int) crystal.getY(), this);

                            } else {
                                g2d.drawImage(hCrystal, (int) crystal.getX(), (int) crystal.getY(), this);
                            }

                            // Health bar
                            if (crystal.getHealth() != crystal.getMaxHealth()) {
                                g2d.setColor(Color.red);
                                g2d.fillRect((int) crystal.getCenterX() - 25, (int) crystal.getY() - 50, 50, 20);
                                g2d.setColor(Color.green);
                                g2d.fillRect((int) crystal.getCenterX() - 25, (int) crystal.getY() - 50, (int) ((double) crystal.getHealth()/ (double) crystal.getMaxHealth() * 50.0), 20);
                            }
                        }
                    }  else {
                        g2d.fillRect((int) wall.getX(), (int) wall.getY(), (int) wall.getWidth(), (int) wall.getHeight());
                    }
                }
            }


            //Attacks
            g2d.setColor(Color.GREEN);
            for (int i = 0; i < game.getAttacks().size(); i ++) {
                Attack attack = game.getAttacks().get(i);

                if (attack.intersects(screenRect)) {

                    // Projectiles shoot at an angle
                    if (attack instanceof Projectile) {
                        double theta = Math.atan((double) ((Projectile) attack).getYSpeed() / ((Projectile) attack).getXSpeed());
                        g2d.rotate(-theta, attack.getX(), attack.getY());
                        g2d.fillRect((int) attack.getX(), (int) attack.getY(), (int) attack.getWidth() * 2, (int) attack.getHeight());
                        g2d.rotate(theta, attack.getX(), attack.getY());

                        // Explosions are circular
                    } else if (attack instanceof Explosion) {
                        g2d.setColor(Color.GREEN);
                        g2d.fillOval((int) attack.getX(), (int) attack.getY(), ((Explosion) attack).getRadius() * 2, ((Explosion) attack).getRadius() * 2);

                    } else {
                        g2d.fillRect((int) attack.getX(), (int) attack.getY(), (int) attack.getWidth(), (int) attack.getHeight());
                    }
                }
            }

            // Enemies
            g2d.setColor(Color.GREEN);
            for (int i = 0; i < game.getEnemies().size(); i ++) {

                Enemy enemy = game.getEnemies().get(i);

                if (enemy.intersects(screenRect)) {

                    // Enemies reflect based on the direction they are going in
                    AffineTransform originalTransform = g2d.getTransform();
                    AffineTransform tx = AffineTransform.getScaleInstance(enemy.getDirection(), 1);

                    if (enemy.getDirection() != 1) {
                        tx.translate(-(enemy.getX() + enemy.width), enemy.getY());

                    } else {
                        tx.translate(enemy.getX(), enemy.getY());
                    }

                    if (enemy instanceof Slime) {
                        g2d.drawImage(slime, tx, this);

                    } else if (enemy instanceof Mosquito) {
                        g2d.drawImage(mosquito, tx, this);

                    } else {
                        g2d.drawImage(jumper, tx, this);
                    }

                    g2d.setTransform(originalTransform);

                    // Health bar
                    if (enemy.getHealth() != enemy.getMaxHealth()) {
                        g2d.setColor(Color.red);
                        g2d.fillRect((int) enemy.getCenterX() - 25, (int) enemy.getY() - 50, 50, 20);
                        g2d.setColor(Color.green);
                        g2d.fillRect((int) enemy.getCenterX() - 25, (int) enemy.getY() - 50, (int) (enemy.getHealth()/enemy.getMaxHealth() * 50), 20);
                    }
                }
            }



            Player player = game.getPlayer();
            //Player flashes on and off when immunity is active
            if (player.getImmunityTimer()/5 % 2 == 0) {

                int speed = Math.abs(player.getXSpeed());

                // Player is reflected based on the direction it is going in
                AffineTransform originalTransform = g2d.getTransform();
                AffineTransform tx = AffineTransform.getScaleInstance(player.getDirection(), 1);

                if (player.getDirection() != 1) {
                    tx.translate(-(player.getX() + playerFrames[0].getWidth()), player.getY());

                } else {
                    tx.translate(player.getX(), player.getY());
                }

                // Player has different sprites based on x speed
                if (speed < 30) {
                    g2d.drawImage(playerFrames[speed / 5], tx, this);

                } else {
                    g2d.drawImage(playerFrames[6], tx, this);
                }

                g2d.setTransform(originalTransform);
            }

            // Bash arrow
            if (aimingBash) {
                // Rotation based on mouse position
                g2d.rotate(bashAngle - Math.PI, player.getCenterX(), player.getCenterY());
                g2d.drawImage(bashAimImage, (int) player.getX() - 100, (int) player.getCenterY() - 300, this);
                g2d.rotate(-bashAngle + Math.PI, player.getCenterX(), player.getCenterY());
            }

            // Orbs have different colors representing their boost type
            for (int i = 0; i < game.getOrbs().size(); i ++) {

                Orb orb = game.getOrbs().get(i);

                if (orb.intersects(screenRect)) {

                    switch (orb.getBoostType()) {
                        case "Gold":
                            g2d.setColor(Color.ORANGE);
                            break;

                        case "Health":
                            g2d.setColor(Color.PINK);
                            break;

                        case "Energy":
                            g2d.setColor(Color.CYAN);
                            break;
                    }

                    g2d.fillRect((int) orb.getX(), (int) orb.getY(), Constants.orbDimensions, Constants.orbDimensions);

                }
            }

            // Obelisk
            if (!game.isInObelisk()) {

                for (Rectangle hitbox: game.getObeliskHitboxes()) {

                    // E button to interact
                    if (player.intersects(hitbox)) {
                        g2d.drawImage(eButton, (int) hitbox.getCenterX() - 50, (int) hitbox.getCenterY() - 50, this);
                    }

                    g2d.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
                }
            }

            // Shop items
            g2d.setColor(new Color(22, 162, 206));
            for (ShopItem item: game.getShop()) {
                g2d.fillRect((int) item.getX(), (int) item.getY(), (int) item.getWidth(), (int) item.getHeight());

                // E interact button
                if (player.intersects(item)){
                    g2d.drawImage(eButton, (int) item.getCenterX() - 50, (int) item.getCenterY() - 50, this);
                }
            }



            // String stats
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Georgia", Font.PLAIN, 42));
            g2d.drawString("Bow Power: " + bowPower, 50, 50);
            g2d.drawString("Gold: $" + player.getTotalGold(), 50, 100);


            g2d.setColor(new Color(20, 20, 129));

            g2d.fillRect(90, 690, 295, 70);
            g2d.setColor(new Color(159, 243, 245));

            // Weapon selected icon
            switch (player.getCurrentWeapon()) {
                case "Sword":
                    g2d.fillRect(98, 698, 54, 54);
                    break;

                case "Hammer":
                    g2d.fillRect(98 + 75, 698, 54, 54);
                    break;

                case "Bow":
                    g2d.fillRect(98 + 150, 698, 54, 54);
                    break;

                case "Rocket":
                    g2d.fillRect(98 + 225, 698, 54, 54);
                    break;
            }

            // Weapon icons
            for (int i = 0; i < weaponIcons.length; i ++) {

                // Only shows unlocked weapons
                if (i == 0) {
                    g2d.drawImage(weaponIcons[i], 100, 700, this);

                } else if ((i == 1) && (player.getWeapons().contains("Hammer"))) {
                    g2d.drawImage(weaponIcons[i], 100 + 75 * i, 700, this);

                } else if ((i == 2) && (player.getWeapons().contains("Bow"))) {
                    g2d.drawImage(weaponIcons[i], 100 + 75 * i, 700, this);

                } else if ((i == 3) && (player.getWeapons().contains("Rocket"))) {
                    g2d.drawImage(weaponIcons[i], 100 + 75 * i, 700, this);

                } else {
                    g2d.setColor(Color.black);
                    g2d.fillRect(100 + 75 * i, 700, 50, 50);
                }


            }

            // Stat bars
            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.red);
            g2d.fillRect(102, 802, (int) (player.getMaxHealth() * 3) - 4, 46);

            g2d.setColor(Color.GREEN);
            g2d.fillRect(100, 800, (int) (player.getHealth() * 3), 50);

            g2d.setColor(Color.black);
            g2d.drawRect(100, 800, (int) (player.getMaxHealth() * 3), 50);

            g2d.setColor(new Color(211, 230, 255));
            g2d.drawString("HP: " + (int) player.getHealth() + " / " + (int) player.getMaxHealth(), 120, 840);

            g2d.setColor(new Color(29, 37, 80));
            g2d.fillRect(102, 902, (int) (player.getMaxEnergy() * 3) - 4, 46);

            g2d.setColor(new Color(32, 127, 178));
            g2d.fillRect(100, 900, (int) (player.getEnergy() * 3), 50);

            g2d.setColor(Color.black);
            g2d.drawRect(100, 900, (int) (player.getMaxEnergy() * 3), 50);

            g2d.setColor(new Color(211, 230, 255));
            g2d.drawString("Energy: " + (int) player.getEnergy() + " / " + (int) player.getMaxEnergy(), 120, 940);

            // Shop screen
            if (game.isInShop()) {
                drawShopScreen(g2d, game.getShop().get(currentShopItem));
            }

        } // paintComponent method end

        /**
         * drawShopScreen
         * Draws shop purchase screen
         * @param g2d graphic
         * @param item shop item
         */
        public void drawShopScreen(Graphics2D g2d, ShopItem item) {

            // Item info
            g2d.setColor(new Color(3, 72, 131, 205));
            g2d.fillRect(320, 180, 1280, 720);
            g2d.setColor(Color.green);
            g2d.fillRect(buyRect.x, buyRect.y - 25, buyRect.width, buyRect.height);
            g2d.setColor(Color.red);
            g2d.fillRect(cancelRect.x, cancelRect.y - 25, cancelRect.width, cancelRect.height);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(6));
            g2d.drawRect(320, 180, 1280, 720);
            g2d.drawRect(buyRect.x, buyRect.y - 25, buyRect.width, buyRect.height);
            g2d.drawRect(cancelRect.x, cancelRect.y - 25, cancelRect.width, cancelRect.height);

            // Buy and cancel buttons
            g2d.drawString("BUY", buyRect.x + 50, (int) (buyRect.getMaxY() - 35));
            g2d.drawString("CANCEL", cancelRect.x + 15, (int) (cancelRect.getMaxY() - 35));
            g2d.setColor(Color.WHITE);
            g2d.drawString(item.getName(), 875, 400);
            g2d.drawString(item.getDescription(), 400, 500);
            g2d.drawString("Price: $" + item.getPrice(), 400, 600);

        }
    } // GraphicsPanel class end



    class Mouse extends MouseAdapter {

        /**
         * Invoked when the mouse button has been clicked (pressed
         * and released) on a component.
         *
         * @param e
         */
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         *
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {

            // Left click = attack, right click = bash
            if (e.getButton() == MouseEvent.BUTTON1) {
                if ((player.getCurrentWeapon().equals("Bow")) && (player.getEnergy() >= Constants.arrowCost)) {
                    bowCharging = true;
                }
            }

            if ((e.getButton() == MouseEvent.BUTTON3) && (player.isBashUnlocked()) && (!player.isBashUsed())) {
                aimingBash = true;
                bashAngle = Math.atan2((player.getCenterY() - e.getY()), (player.getCenterX() - e.getX()));
                game.setRefreshDelay(75);
            }
        }

        /**
         * Invoked when a mouse button has been released on a component.
         *
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            // Left click = attack, right click = bash
            if (e.getButton() == MouseEvent.BUTTON1) {

                // Shop screen
                if (game.isInShop()) {

                    if (buyRect.contains(e.getPoint())) {

                        if (game.getShop().get(currentShopItem).buy(player)) {

                            if (game.getShop().get(currentShopItem).getPrice() != 6000) {
                                game.getShop().remove(currentShopItem);
                            }
                            game.setInShop(false);
                        }

                    } else if (cancelRect.contains(e.getPoint())) {
                        game.setInShop(false);
                    }

                } else {
                    if (player.getAttackCooldown() == 0) {
                        int direction = e.getX() > player.getCenterX() ? 1 : -1;

                        // attack based on current attack
                        switch (player.getCurrentWeapon()) {
                            case "Sword":
                                game.getAttacks().add(new Sword((int) (player.getX() + (player.getDirection() == 1 ? player.getWidth() : -150)),
                                        (int) (player.getY() - 50), player.getDirection(), true, player.getDamageBoost()));
                                player.setAttackCooldown(15);
                                break;

                            case "Hammer":
                                game.getAttacks().add(new Hammer((int) (player.getX() + (player.getDirection() == 1 ? player.getWidth() + 50 : -300)),
                                        (int) (player.getY() - 50), player.getDirection(), true, player.getDamageBoost()));
                                player.setAttackCooldown(50);
                                break;

                            case "Bow":
                                if (bowCharging) {
                                    if (bowPower > 20) {
                                        game.getAttacks().add(new Arrow((int) player.getCenterX(), (int) player.getCenterY() - 25,
                                                e.getX() + cameraX, e.getY() + cameraY, direction, true, bowPower, player.getDamageBoost()));
                                        player.setEnergy(player.getEnergy() - Constants.arrowCost);
                                    }
                                    bowPower = 1;
                                    bowCharging = false;
                                }
                                break;

                            case "Rocket":
                                if (player.getEnergy() >= Constants.rocketCost) {
                                    game.getAttacks().add(new Rocket((int) player.getCenterX() - 50, (int) player.getCenterY() - 25,
                                            e.getX() + cameraX, e.getY() + cameraY, direction, true, player.getDamageBoost()));
                                    player.setEnergy(player.getEnergy() - Constants.rocketCost);
                                    player.setAttackCooldown(100);
                                }
                                break;
                        }
                    }
                }

            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if ((!player.isAbilityActive()) && (player.isBashUnlocked()) && (!player.isBashUsed())) {
                    player.bash(e.getX() + cameraX, e.getY() + cameraY);
                    aimingBash = false;
                    game.setRefreshDelay(17);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // Bash angle calculation
            bashAngle = Math.atan2((player.getCenterY() - e.getY()), (player.getCenterX() - e.getX()));
        }

        /**
         * Invoked when the mouse enters a component.
         *
         * @param e
         */
        @Override
        public void mouseEntered(MouseEvent e) {

        }

        /**
         * Invoked when the mouse exits a component.
         *
         * @param e
         */
        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    class Keyboard implements KeyListener {

        /**
         * Invoked when a key has been pressed.
         * See the class description for {@link KeyEvent} for a definition of
         * a key pressed event.
         *
         * @param e
         */
        public void keyPressed(KeyEvent e) {
            // a,d movement, e to interact, shift to dash
            switch (Character.toLowerCase(e.getKeyChar())) {
                case 'a':
                    player.setMovingLeft(true);
                    player.setMovingRight(false);
                    player.setDirection(-1);
                    break;
                case 'd':
                    player.setMovingRight(true);
                    player.setMovingLeft(false);
                    player.setDirection(1);
                    break;

                case 'e':
                    if (!game.isInObelisk()) {
                        for (int i = 0; i < game.getObeliskHitboxes().size(); i ++) {
                            if (player.intersects(game.getObeliskHitboxes().get(i))) {
                                game.activateObelisk(i);
                            }
                        }
                    }
                    for (int i = 0; i < game.getShop().size(); i ++) {
                        if (player.intersects(game.getShop().get(i))) {
                            game.setInShop(true);
                            currentShopItem = i;
                        }
                    }
            }

            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                if ((!player.isAbilityActive()) && (player.isDashUnlocked()) && (!player.isDashUsed())) {
                    player.dash();
                }
            }
        }

        /**
         * Invoked when a key has been released.
         * See the class description for {@link KeyEvent} for a definition of
         * a key released event.
         *
         * @param e
         */
        public void keyReleased(KeyEvent e) {

            char key = Character.toLowerCase(e.getKeyChar());

            // a, d move, m teleport to shop
            switch (Character.toLowerCase(e.getKeyChar())) {
                case 'a':
                    player.setMovingLeft(false);
                    break;
                case 'd':
                    player.setMovingRight(false);
                    break;
                case 'm':
                    if (!(game.isInObelisk()) && (player.isBashUnlocked())) {
                        player.setLocation((int) game.getShop().get(0).getX(), (int) game.getShop().get(0).getY());
                    }
                    break;
            }
        }

        /**
         * Invoked when a key has been typed.
         * See the class description for {@link KeyEvent} for a definition of
         * a key typed event.
         *
         * @param e
         */
        public void keyTyped(KeyEvent e) {
            switch (Character.toLowerCase(e.getKeyChar())) {

                // Space to jump, numbers for weapon shopping
                case ' ':
                    player.jump();
                    break;
                case '1':
                    if (player.getWeapons().contains("Sword")) {
                        player.setCurrentWeapon("Sword");
                    }
                    break;
                case '2':
                    if (player.getWeapons().contains("Hammer")) {
                        player.setCurrentWeapon("Hammer");
                    }
                    break;
                case '3':
                    if (player.getWeapons().contains("Bow")) {
                        player.setCurrentWeapon("Bow");
                    }
                    break;
                case '4':
                    if (player.getWeapons().contains("Rocket")) {
                        player.setCurrentWeapon("Rocket");
                    }
                    break;
            }
        }

    }
} // UsingPictures class