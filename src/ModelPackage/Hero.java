package ModelPackage;

import ViewPackage.GamePanel;
import ViewPackage.View;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Y50 on 5/1/2016.
 */
public class Hero extends Soldier{


    Player player;

    public Hero(String soldierType, String name, ArrayList<Ability> abilities1, String spriteSheetFileName)
    {
        super(soldierType , name , abilities1, new File(spriteSheetFileName));
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void describe() {
        System.out.println("shit");
    }

    @Override
    public void init(GamePanel gamePanel, Story story) {
        super.init(gamePanel ,story);
        this.player = this.getStory().getGameObjectsHolder().getPlayer();
    }

    @Override
    public ArrayList<Soldier> getArmy() {
        return this.player.getCurrentBattle().getTeam(this , true);
    }

    @Override
    public ArrayList<Soldier> getOpponentArmy() {
        return player.getCurrentBattle().getTeam(this , false);
    }

    @Override
    public void getAttacked(int damage) {
        super.getAttacked(damage);
        if(this.getCurrentHealth() == 0)
        {
            if(this.player.getImmortalityPotions() > 0)
            {
                this.player.setImmortalityPotions(this.player.getImmortalityPotions() - 1);
                View.show("An immortality potion was used to revive " + this.getName() +"!");
                this.revive();
            }
            else
            {
                View.show(this.getName() + " died with glory! his soul will rest in peace!");
                this.player.setGameOver(true);
            }
        }
    }

    public boolean payPrice(Price price, boolean isPricePaid)
    {
        if(
                        getCurrentMagic() >= price.getMagicPrice() &&
                        getEnergyPoints() >= price.getEPPrice() &&
                        player.getGold() >= price.getGoldPrice() &&
                        player.getXp() >= price.getXPPrice())
        {
            if(isPricePaid == true)
            {
                setCurrentMagic(getCurrentMagic() - price.getMagicPrice());
                setEnergyPoints(getEnergyPoints() - price.getEPPrice());
                if(price.getHealthPrice() != 0)
                this.getAttacked(price.getHealthPrice());
                player.setGold(player.getGold() - price.getGoldPrice());
                player.setXp(player.getXp() - price.getXPPrice());
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean payPrice(Price price){ return payPrice(price,false);}

    public int getXP(){ return this.player.getXp();}

    public ArrayList<Ability> getAbilities(){return super.getAbilities();}

    public void sell(String itemName) {
        Item removingItem = null;
        for (Item item : this.getInventory()) {
            if(item.getName().toLowerCase().equals(itemName.toLowerCase()))
            {
                removingItem = item;
            }
        }
        if(removingItem == null)
        {

        }
        else {
            String removingBuff = removingItem.getAffectingBuffAfterBuying().getName();
            Price purchasePrice = removingItem.getPurchasePrice();
            int priceIncreaseRate = removingItem.getPriceIncreaseRate();
            int howManyPurchased = removingItem.getHowManyPurchased();
            int goldTotal = -(purchasePrice.getGoldPrice() + priceIncreaseRate * howManyPurchased) / 2;
            Price sellPrice = new Price(goldTotal);

            //TODO : There is a negative price being paid here and might not work !

            this.payPrice(sellPrice, true);

            removeBuff(removingBuff);

            removeItem(itemName);
        }
    }

}
