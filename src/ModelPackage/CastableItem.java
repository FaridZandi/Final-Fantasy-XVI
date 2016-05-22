package ModelPackage;

import com.sun.javafx.sg.prism.NGShape;

import java.util.ArrayList;

/**
 * Created by Y50 on 5/2/2016.
 */
public class CastableItem extends Item {

    private int charges;

    private int chargesLeft;

    private CastableData castableData;

    private int turnsToUseAgain;

    public void cast(Soldier target)
    {
        System.out.println("hello there");
    }

    public CastableItem()
    {

    }

    @Override
    public void purchasedBy(Hero buyer) {
        if(!isEverythingOkToBuy(buyer))
        {
            return;
        }
        CastableItem temp = (CastableItem)Model.deepClone(this);
        buyer.addItem(temp);
        buyer.addBuff((Buff)Model.deepClone(getAffectingBuffAfterBuying()));
    }

    public int getTurnsToUseAgain() {
        return turnsToUseAgain;
    }

    public void setTurnsToUseAgain(int turnsToUseAgain) {
        this.turnsToUseAgain = turnsToUseAgain;
    }
}
