package ModelPackage;

import java.io.Serializable;
import org.omg.PortableServer.ServantActivator;

/**
 * Created by Y50 on 5/1/2016.
 */
abstract public class GameObject implements Serializable{
    private String name;

    abstract public void describe();

    public String getName()
    {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
