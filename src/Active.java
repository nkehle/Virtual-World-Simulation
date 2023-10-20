import processing.core.PImage;

import java.util.List;

public abstract class Active extends Animate {

    protected double actionPeriod;
    protected int resourceLimit;
    protected int resourceCount;

    double actionPeriod(){
        return this.actionPeriod;
    }
    int resourceLimit(){
        return this.resourceLimit;
    }
    int resourceCount(){
        return this.resourceCount;
    }

    public Active(String id, Point position, List<PImage> images,double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount){
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }

    //for all actives
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent( this, createActivityAction( world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, createAnimationAction( 0), this.getAnimationPeriod());
    }

    public Activity createActivityAction(WorldModel world, ImageStore imageStore) {
        return new Activity(this, world, imageStore, 0);
    }

}
