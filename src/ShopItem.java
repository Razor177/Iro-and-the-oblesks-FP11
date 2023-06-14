/**
 * [Shopitem.java]
 * This class represents a shop item with a name, description and price
 * @author Ian Leung, Michael Khart
 * @version 1.0, June 12, 2023
 */
public class ShopItem extends GameObject{

    private String name;

    private String description;

    private final int price;

    ShopItem(int x, int y, int width, int height, String name, int price) {
        super(x, y, width, height);
        this.name = name;

        // Shop description based on name
        switch (name) {
            case "Hammer":
                this.description = "A bigger, stronger sword with a blindspot";
                break;

            case "Bow":
                this.description = "A ranged weapon that gets stronger the longer you aim it";
                break;

            case "Rocket":
                this.description = "Use 100 energy to create a massive explosion";
                break;

            case "TripleJump":
                this.name = "TripleJump";
                this.description = "I wonder what this does";
                break;

            case "Health":
                this.description = "Increase max health by 100";
                break;

            case "Energy":
                this.description = "Increase max energy by 100";
                break;

            case "Damage":
                this.description = "Multiplies damage by 1.2x";
                break;
        }
        this.price = price;
    }

    public boolean buy(Player player) {
        // Player must have enoguh money
        if (player.getTotalGold() >= price) {

            player.setTotalGold((int) (player.getTotalGold() - price));
            // Effect based on name
            switch (name) {
                case "Hammer":
                    player.getWeapons().add("Hammer");
                    break;

                case "Bow":
                    player.getWeapons().add("Bow");
                    break;

                case "Rocket":
                    player.getWeapons().add("Rocket");
                    break;

                case "TripleJump":
                    player.setMaxJumps(3);
                    break;

                case "Health":
                    player.setMaxHealth(player.getMaxHealth() + 100);
                    player.setHealth(player.getMaxHealth());
                    break;

                case "Energy":
                    player.setMaxEnergy(player.getMaxEnergy() + 100);
                    player.setEnergy(player.getMaxEnergy());
                    break;

                case "Damage":
                    player.setDamageBoost(1.2);
                    break;
            }
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

}
