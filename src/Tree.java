import processing.core.PImage;

import java.util.List;

public class Tree extends Active{
    private int health;
    private final int healthLimit;
    int health(){
        return this.health;
    }
    void subHealth(){
        this.health --;
    }
    int healthLimit(){
        return this.healthLimit;
    }
    private static final String STUMP_KEY = "stump";

    public Tree (String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod,int resourceLimit, int resourceCount, int healthLimit, int health) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
        this.healthLimit = healthLimit;
        this.health = health;
    }

    public void executeTreeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!this.transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

    public boolean transformTree(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Stump stump = new Stump(STUMP_KEY + "_" + this.id, this.position, getImageList(imageStore, STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        }

        return false;
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return transformTree(world, scheduler, imageStore);
    }
}
