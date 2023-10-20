/**
 * An action that can be taken by an entity
 */

public abstract class Action {
    protected Ent entity;
    protected WorldModel world;
    protected ImageStore imageStore;
    protected int repeatCount;

    public abstract void executeAction(EventScheduler scheduler);


}