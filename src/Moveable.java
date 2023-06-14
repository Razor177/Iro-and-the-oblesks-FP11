/**
 * Moveable
 * This class represents a moving object
 * @author Ian Leung, Michael Khart
 * @version 1.0 June 12, 2023
 */
abstract public class Moveable extends GameObject{

    private int xSpeed, ySpeed = 0;

    /**
     * Moveable
     * Constructs a moveable object
     * @param x the x position
     * @param y the y posotion
     * @param width the width
     * @param height the height
     */
    Moveable(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public int getXSpeed() {
        return xSpeed;
    }

    public void setXSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }

    public int getYSpeed() {
        return ySpeed;
    }

    public void setYSpeed(int ySpeed) {
        this.ySpeed = ySpeed;
    }











}
