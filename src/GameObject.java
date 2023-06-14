/**
 * [GameObject.java]
 * This class is the base template for all game objects
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
import java.awt.Rectangle;

abstract public class GameObject extends Rectangle {

    /**
     * GameObject
     * Creates a game object
     * @param x the x position
     * @param y the y positon
     * @param width the width
     * @param height the height
     */
    GameObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}

