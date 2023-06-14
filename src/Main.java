/**
 * [Main.java]
 * This program is a platformer game
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        GameEngine map = new GameEngine();
        MapDisplay window = new MapDisplay(map);

        while (map.playing) {
            window.refresh();

            if (!map.isInShop()) {
                map.moveAll();
                map.checkCollisions();
            }
            window.refresh();
            try {Thread.sleep(map.getRefreshDelay());} catch(Exception e){}

        }

    }
}