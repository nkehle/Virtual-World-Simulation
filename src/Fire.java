import processing.core.PImage;

import java.util.List;

public class Fire extends Active{
    private static final String BURNT_KEY = "burnt";

    public Fire(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
        this.actionPeriod = 5;
    }

    public void executeFireActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        //transformBurnt(world, scheduler, imageStore);
    }
}
