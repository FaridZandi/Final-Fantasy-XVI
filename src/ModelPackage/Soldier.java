package ModelPackage;

import ControlPackage.Control;
import ControlPackage.Drawable;
import ViewPackage.GamePanel;
import ViewPackage.View;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

/**
 * Created by Y50 on 5/1/2016.
 */
public abstract class Soldier extends GameObject implements Drawable
{

    private int walkingSpriteStartingRow;
    private int attackingSpriteStartingRow;
    private int castingStartingRow;
    private int numberOfAttackingFrames;
    private int numberOfWalkingFrames;
    private int numberOfCastingFrames;
    private int animationPlayFrameRate;
    private boolean isStanding;
    private boolean isWalking;
    private boolean isCasting;
    private boolean isAttacking;
    private int currentAnimationStep;
    private int locationX;
    private int locationY;
    private int direction;


    private BufferedImage soldierSpriteSheet;
    private File spriteSheetFileName;
    private int currentHealth;
    private int currentMagic;
    private int energyPoints;
    private SoldierType type;
    private String soldierTypeName;
    private ArrayList<Buff> buffs;
    private ArrayList<Item> inventory;
    private Story story;
    private GamePanel gamePanel;

    private ArrayList<Ability> abilities;
    public Soldier(String soldierTypeName, String name, ArrayList<Ability> abilities, File spriteSheetFileName)
    {

        this.setName(name);
        this.soldierTypeName = soldierTypeName;
        this.abilities = abilities;
        inventory = new ArrayList<>();
        buffs = new ArrayList<>();
        this.spriteSheetFileName = spriteSheetFileName;
        walkingSpriteStartingRow = 7;
        attackingSpriteStartingRow = 11;
        castingStartingRow = -1;
        numberOfAttackingFrames = 5;
        numberOfWalkingFrames = 8;
        numberOfCastingFrames = 6;
        animationPlayFrameRate = 5;
        currentAnimationStep = -animationPlayFrameRate;
        isStanding = true;
        isWalking = false;
        isCasting = false;
        isAttacking = false;
    }

    public void init(GamePanel gamePanel , Story story)
    {
        this.gamePanel = gamePanel;
        try {
            soldierSpriteSheet = ImageIO.read(spriteSheetFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.story = story;
        try {
            this.type = (SoldierType) story.getGameObjectsHolder().find(soldierTypeName);
        }
        catch (ClassCastException e)
        {
            View.show("Save file corrupted!");
            System.exit(0);
        }

        if(type.getAbilities() != null)
        for (Ability ability : type.getAbilities()) {
                abilities.add((Ability)Model.deepClone(ability));
        }
        buffs = new ArrayList<>();
        if(type.getDefaultBuffs()!=null)
        for (Buff buff : type.getDefaultBuffs()) {
            this.addBuff((Buff)Model.deepClone(buff));
        }
        this.currentMagic = type.getMaximumMagic();
        this.currentHealth = type.getMaximumHealth();
        this.energyPoints = type.getEnergyPoints();
    }

    public void draw(Graphics2D g2d, Control control) {

        BufferedImage spriteSheet = this.getSoldierSpriteSheet();
        int row = walkingSpriteStartingRow;
        if(isWalking || isStanding)
        {
            row = direction + walkingSpriteStartingRow;
        }
        else if(isAttacking)
        {
            row = direction + attackingSpriteStartingRow;
        }
        else if(isCasting)
        {
            row = direction + castingStartingRow;
        }

        int column = 1 + currentAnimationStep/animationPlayFrameRate;

        Image subImage = getSubImage(spriteSheet , row , column);

        g2d.drawImage(subImage , locationX , locationY, null);
    }

    public static Image getSubImage(BufferedImage spriteSheet, int row, int column) {
        final int SpriteSize = 64;
        BufferedImage subImg = spriteSheet.getSubimage(SpriteSize * column , SpriteSize * row , SpriteSize , SpriteSize);
        return subImg.getScaledInstance(GameMap.CellSize, GameMap.CellSize, Image.SCALE_DEFAULT);
    }

    public abstract ArrayList<Soldier> getArmy();

    public abstract ArrayList<Soldier> getOpponentArmy();

    public void describe()
    {

    }

    public void addItem(Item item) {
        getInventory().add(item);
    }

    public void removeItem(String itemName)
    {
        for (Item item : inventory) {
            if(item.getName().equals(itemName))
            {
                inventory.remove(item);
                break;
            }
        }
    }

    public void getAttacked(int damage)
    {
        int cH = this.currentHealth;
        cH -= damage;
        if(cH > 0)
        {
            this.currentHealth = cH;
        }
        else
        {
            this.currentHealth = 0;
        }
        new SwingWorker()
        {
            @Override
            protected Object doInBackground() throws Exception {
                MyText txt = new MyText(" - " + damage , locationX , locationY , 300 , 300,  50 , Color.RED);
                gamePanel.getDrawables().add(txt);
                for (int i = 0; i < Control.FPS; i++) {
                    txt.setY(txt.getY() - 1);
                    Thread.sleep( 1000 / Control.FPS);
                }
                gamePanel.removeDrawable(txt);

                return null;
            }
        }.execute();

        View.show(this.getName() + " was attacked for "+ damage + "damage, current health is " + this.currentHealth + ".");
    }

    public void getHealed(int heal)
    {
        System.out.println("+" + heal);
        int cH = this.currentHealth;
        int mH = this.calculateMaximumHealth();
        int increase = heal;
        cH += heal;
        if(cH < mH)
        {
            this.currentHealth = cH;
        }
        else
        {
            increase -= (cH - mH);
            this.currentHealth = mH;
        }
        if(increase != 0)
        new SwingWorker()
        {
            @Override
            protected Object doInBackground() throws Exception {
                MyText txt = new MyText(" + " + heal , locationX , locationY , 300 , 300,  50 , Color.GREEN);
                gamePanel.getDrawables().add(txt);
                for (int i = 0; i < Control.FPS; i++) {
                    txt.setY(txt.getY() - 1);
                    Thread.sleep( 1000 / Control.FPS);
                }
                gamePanel.removeDrawable(txt);
                return null;
            }
        }.execute();
    }

    public void getMagicPoint(int magicPoints) {
        int currentMagic = this.currentMagic;
        int maximumMagic = this.calculateMaximumMagic();
        currentMagic += magicPoints;
        if(currentMagic < maximumMagic)
        {
            this.currentMagic = currentMagic;
        }
        else
        {
            this.currentMagic = maximumMagic;
        }
    }


    public void getEnergyPoints(int EP)
    {
        this.energyPoints += EP;
    }

    public void revive()
    {
        //TODO : this must be called once again at the beginning of the battle for every soldier in the battle.
        currentHealth = calculateMaximumHealth();
        currentMagic = calculateMaximumMagic();
        energyPoints = calculateMaximumEnergyPoint();
    }

    public void timeBasedPutIntoEffect()
    {



        //auto cast abilities must be cast here
        for (Ability ability : this.getAbilities()) {
            if(ability.isCastable())
            {
                CastableAbility castableAbility = (CastableAbility)ability;
                if(castableAbility.getLevel() > 0)
                if(castableAbility.getCastableData().get(castableAbility.getLevel() - 1).isAutoCast())
                {
                    this.cast(castableAbility.getName() , null);
                }

            }
        }

        //cooldown of items and abilities must reduce here and also the buffs
        for (Ability ability : this.getAbilities()) {
            if(ability.isCastable())
            {
                int temp = ((CastableAbility)ability).getTurnsToUseAgain() - 1;
                if(temp >= 0) {
                    ((CastableAbility)ability).setTurnsToUseAgain(temp);
                }
            }
        }
        for (Item item : this.getInventory()) {
            if(item.isCastable())
            {
                int temp = ((CastableItem)item).getTurnsToUseAgain() - 1;
                if(temp > 0)
                {
                    ((CastableItem)item).setTurnsToUseAgain(temp);
                }
            }
        }
        int buffsNumber = this.getBuffs().size();
        for (int i = 0; i < buffsNumber; i++) {
            if(!this.getBuffs().get(i).isPermanent())
            {
                int temp = this.getBuffs().get(i).getDuration() - 1;
                if (temp == 0)
                {
                    removeBuff(this.getBuffs().get(i).getName());
                    i--;
                    buffsNumber--;
                }
                else
                {
                    this.getBuffs().get(i).setDuration(temp);
                }
            }
        }

        //health increase by passage of time
        int maximum , increase;

        maximum = calculateMaximumHealth();
        increase = (int)(maximum *(type.getHealthRefillRatePercentage() / 100.0));
        this.getHealed(increase);


        //magic increase by passage of time
        maximum = calculateMaximumMagic();
        increase = maximum * (type.getMagicRefillRatePercentage());
        this.getMagicPoint(increase);

        // energy points filled to maximum each time
        energyPoints = calculateMaximumEnergyPoint();

    }

    public void addBuff(Buff buff)
    {
        int increase;
        int maximum;
        increase = buff.getMaximumHealthIncrease();
        if(increase != 0)
        {
            maximum = calculateMaximumHealth();
            double ratio = ((double)(maximum + increase)) / (maximum);
            currentHealth = (int) (ratio * currentHealth);
        }
        increase = buff.getMaximumMagicIncrease();
        if(increase != 0)
        {
            maximum = calculateMaximumMagic();
            double ratio = ((double)(maximum + increase)) / (maximum);
            currentMagic = (int) (ratio * currentMagic);
        }
        increase = buff.getEnergyPointIncrease();
        if(increase != 0)
        {
            this.energyPoints += increase;
        }
        buffs.add(buff);
    }

    public void removeBuff(String buffName)
    {
        for (Buff buff : buffs) {
            if(buff.getName().equals(buffName))
            {
                int increase;
                int maximum;
                increase = buff.getMaximumHealthIncrease();
                if(increase != 0)
                {
                    maximum = calculateMaximumHealth();
                    double ratio = ((double)(maximum - increase)) / (maximum);
                    currentHealth = (int) (ratio * currentHealth);
                }
                increase = buff.getMaximumMagicIncrease();
                if(increase != 0)
                {
                    maximum = calculateMaximumMagic();
                    double ratio = ((double)(maximum - increase)) / (maximum);
                    currentMagic = (int) (ratio * currentMagic);
                }
                increase = buff.getEnergyPointIncrease();
                if(increase != 0)
                {
                    this.energyPoints -= increase;
                }
                buffs.remove(buff);
                break;
            }
        }
    }

    public void attack(Soldier target , double attackMultiplier)
    {
        SwingWorker attackAnimation = new SwingWorker()
        {
            @Override
            protected Object doInBackground() throws Exception{
                go();
                attackAnimation();
                int attackDamage = calculateAttackDamage(attackMultiplier);
                int splashPercentageTotal = calculateSplashPercentage();

                int splashedDamage = (int)(((splashPercentageTotal)/100.0) * attackDamage);
                ArrayList<Soldier> opponents = target.getArmy();
                target.getAttacked(attackDamage);
                Thread.sleep(20 * 1000 / Control.FPS);

                if(splashPercentageTotal != 0) {
                    for (Soldier opponent : opponents)
                    {
                        if(!opponent.equals(target))
                            opponent.getAttacked(splashedDamage);
                    }
                }
                goBack();
                return null;
            }

            private void attackAnimation() throws InterruptedException {
                for (int i = 0; i < numberOfAttackingFrames * animationPlayFrameRate; i++) {
                    currentAnimationStep++;
                    Thread.sleep(1000 / Control.FPS);
                }
                currentAnimationStep = -animationPlayFrameRate;
            }

        };

        attackAnimation.execute();
    }



    public void cast(String abilityName , String targetName)
    {

        Ability abilitySearchResult = findAbility(abilityName);

        if(abilitySearchResult == null)
        {
            View.show("Ability Not Found!");
            return;
        }
        if(!abilitySearchResult.isCastable())
        {
            View.show("this ability is not castable,please try again");
            return;
        }

        final CastableAbility castableAbility = (CastableAbility)abilitySearchResult ;

        if(castableAbility.getLevel() == 0)
        {
            View.show("You haven't acquired this ability yet, please try again");
            return;
        }
        if(castableAbility.getTurnsToUseAgain() > 0)
        {
            View.show("Your desired ability is still in cooldown");
            return;
        }

        ArrayList<Soldier> target = new ArrayList<>();


        if(targetName == null) {
            CastableData castableData = castableAbility.getCastableData().get(castableAbility.getLevel() - 1);
            if (castableData.isGlobalEnemy() || castableData.isGlobalFriendly()) {
                if (castableData.isGlobalEnemy()) {
                    target.addAll(this.getOpponentArmy());
                }
                if (castableData.isGlobalFriendly()) {
                    target.addAll(this.getArmy());
                }
            }
            else
            {
                View.show("you have to specify a target to use this ability! please try again.");
                return;
            }
        }
        else
        {
            ArrayList<Soldier> enemies = this.getOpponentArmy();
            ArrayList<Soldier> friendlies = this.getArmy();
            Boolean isTargetInEnemyArmy = true;
            for (Soldier enemy : enemies) {
                if(enemy.getName().toLowerCase().equals(targetName.toLowerCase()))
                {
                    target.add(enemy);
                    break;
                }
            }
            if(target.isEmpty())
            {
                for (Soldier friendly : friendlies) {
                    if(friendly.getName().toLowerCase().equals(targetName.toLowerCase()))
                    {
                        target.add(friendly);
                        isTargetInEnemyArmy = false;
                        break;
                    }
                }
            }
            if(target.isEmpty())
            {
                View.show("Target not found, please try again.");
                return;
            }

            if(isTargetInEnemyArmy && !castableAbility.isCastableOnEnemies())
            {
                View.show("This ability can not be cast on an enemy soldier, please try again");
                return;
            }
            if(!isTargetInEnemyArmy && !castableAbility.isCastableOnFriendlies())
            {
                View.show("This ability can not be cast on a friendly soldier, please try again");
            }
        }



        if (!checkPricePay(castableAbility)) return;

        Soldier caster = this;
        SwingWorker castAnimation = new SwingWorker()
        {
            @Override
            protected Object doInBackground() throws Exception{
                go();
                castAnimation();

                for (Soldier soldier : target) {
                    castableAbility.cast(soldier , caster);
                }

                goBack();
                return null;
            }

            private void castAnimation() throws InterruptedException {
                for (int i = 0; i < numberOfCastingFrames * animationPlayFrameRate; i++) {
                    currentAnimationStep++;
                    Thread.sleep(1000 / Control.FPS);
                }
                currentAnimationStep = -animationPlayFrameRate;
            }

        };

        castAnimation.execute();
    }

    private boolean checkPricePay(CastableAbility castableAbility) {
        if(this.getClass() == Hero.class)
        {
            Price castPrice = castableAbility.getCastPrices().get(castableAbility.getLevel() - 1);
            if(((Hero)this).payPrice(castPrice,true));
            {
                return true;
            }
        }
        View.show("you don't have sufficient ep/mp/xp/gold to cast this ability");
        return false;
    }

    public int calculateMaximumMagic()
    {
        int maximumMagic = 0;
        for(Buff buff : buffs)
        {
            maximumMagic += buff.getMaximumMagicIncrease();
        }
        maximumMagic += this.type.getMaximumMagic();
        return maximumMagic;
    }

    public int calculateMaximumHealth()
    {
        int maximumHealth = 0;
        for(Buff buff : buffs)
        {
            maximumHealth += buff.getMaximumHealthIncrease();
        }
        maximumHealth += this.type.getMaximumHealth();
        return maximumHealth;
    }

    public int calculateMaximumEnergyPoint()
    {
        int maximumEP = 0;
        for(Buff buff : buffs)
        {
            maximumEP += buff.getEnergyPointIncrease();
        }
        maximumEP += this.type.getEnergyPoints();
        return maximumEP;
    }

    public int calculateAttackDamage(double attackMultiplier)
    {
        int attackPlus = 0;
        double criticalMultiTotal = 1 + attackMultiplier;
        for (Buff buff:buffs)
        {
            attackPlus += buff.getAttackPowerIncrease();
            int randomNumber = (int)(Math.random()*100 + 1);
            if(randomNumber < buff.getCriticalDamageChance())
            {
                criticalMultiTotal += buff.getCriticalDamageMultiplier();
            }
        }
        int attackDamage = this.type.getAttackPower()+attackPlus;
        attackDamage*=criticalMultiTotal;

        return attackDamage;
    }

    public int calculateSplashPercentage()
    {
        int splashPercentageTotal = 0;

        for (Buff buff:buffs)
        {
            splashPercentageTotal+=buff.getDamageSplashPercentage();
        }

        return splashPercentageTotal;
    }

    public boolean haveSpaceInInventory()
    {
        int totalCapacity = this.type.getInventorySize();
        int currentInventorySpaceFilled = inventory.size();
        if(totalCapacity - currentInventorySpaceFilled >0)
        {
            return true;
        }
        return false;
    }

    public int getEnergyPoints() {
        return energyPoints;
    }

    public int getCurrentMagic() {
        return currentMagic;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void setCurrentMagic(int currentMagic) {
        this.currentMagic = currentMagic;
    }

    public void setEnergyPoints(int energyPoints) {
        this.energyPoints = energyPoints;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public Ability findAbility(String abilityName) {
        for (Ability ability : abilities) {
            if(ability.getName().toLowerCase().equals(abilityName.toLowerCase()))
            {
                return ability;
            }
        }
        return null;
    }


    private void go() throws InterruptedException {
        isStanding = false;
        isWalking = true;
        walk();
        currentAnimationStep = -animationPlayFrameRate;
        isWalking = false;
        isAttacking = true;
    }

    private void goBack() throws InterruptedException {
        isAttacking = false;
        isWalking = true;
        turnAround();
        walk();
        isWalking = false;
        isStanding = true;
        turnAround();
    }

    private void turnAround() throws InterruptedException {
        int temp;
        temp = direction;
        direction = 3;
        Thread.sleep(animationPlayFrameRate * 1000 / Control.FPS);
        direction = 6 - temp;
        currentAnimationStep = -animationPlayFrameRate;
    }



    private void walk() throws InterruptedException {
        for (int i = 0; i < numberOfWalkingFrames * animationPlayFrameRate; i++) {
            currentAnimationStep++;
            if(direction == Player.East)
                locationX += 5;
            else if(direction == Player.West)
                locationX -= 5;
            Thread.sleep(1000 / Control.FPS);
        }
    }

    public void addAbility(Ability ability)
    {
        this.abilities.add(ability);
    }

    public ArrayList<Ability> getAbilities(){ return this.abilities;}

    public int getMaximumHealth(){return this.type.getMaximumHealth();}

    public int getMaximumMagic(){ return  this.type.getMaximumMagic();}

    public SoldierType getType(){return this.type;}

    public Story getStory()
    {
        return story;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public ArrayList<Buff> getBuffs() {
        return buffs;
    }

    public BufferedImage getSoldierSpriteSheet() {
        return soldierSpriteSheet;
    }

    public void setLocationX(int locationx) {
        this.locationX = locationx;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getAnimationPlayFrameRate() {
        return animationPlayFrameRate;
    }

    public int getLocationY() {
        return locationY;
    }

    public int getLocationX() {
        return locationX;
    }
}