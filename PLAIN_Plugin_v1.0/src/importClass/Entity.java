package importClass;

import java.util.Set;

public abstract class Entity {

    public abstract Set<String> getEntitySet();
    public abstract Set<String> getFullEntitySet();
    public abstract String getClassOrigin();

    public abstract void initializeNewEntitySet();
    public abstract void resetNewEntitySet();
    public abstract Set<String> getNewEntitySet();
}
