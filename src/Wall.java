/**
 * [Wall.java]
 * This class represents a basic wall
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
public class Wall extends GameObject{

    private final boolean respawnable;

    /**
     * Wall
     * Constructs a wall
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     */
    Wall(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.respawnable = false;
    }

    /**
     * Wall
     * Constructs a wall with a custom respawnability
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     * @param respawnable if you can respawn on the wall
     */
    Wall(int x, int y, int width, int height, boolean respawnable) {
        super(x, y, width, height);
        this.respawnable = respawnable;
    }

    public boolean isrespawnable() {
        return respawnable;
    }

}
