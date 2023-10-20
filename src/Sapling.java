import processing.core.PImage;

import java.util.List;

public class Sapling extends Active {
    private int health;
    private int healthLimit;
    int health(){
        return this.health;
    }
    void subHealth(){
        this.health --;
    }
    int healthLimit(){
        return this.healthLimit;
    }

    public static final int SAPLING_HEALTH_LIMIT = 5;

    private static final String STUMP_KEY = "stump";
    private static final String TREE_KEY = "tree";
    private static final double TREE_ANIMATION_MAX = 0.600;
    private static final double TREE_ANIMATION_MIN = 0.050;
    private static final double TREE_ACTION_MAX = 1.400;
    private static final double TREE_ACTION_MIN = 1.000;
    private static final int TREE_HEALTH_MAX = 3;
    private static final int TREE_HEALTH_MIN = 1;

    public Sapling(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount, int healthLimit, int health) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
        this.healthLimit = SAPLING_HEALTH_LIMIT;
        this.health = 0; //changed to 0
    }

    public void executeSaplingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        if (!this.transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return transformSapling(world, scheduler, imageStore);
    }

    public boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Stump stump = new Stump(STUMP_KEY + "_" + this.id, this.position, getImageList(imageStore, STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (this.health >= this.healthLimit) {
            Tree tree = new Tree(TREE_KEY + "_" + this.id, this.position, getImageList(imageStore, TREE_KEY),getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), 0,0,0,getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }
        return false;
    }
}
