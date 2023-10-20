import processing.core.PImage;
import java.util.*;

public abstract class Animate extends Ent{
    protected double animationPeriod;

    double animationPeriod(){
        return this.animationPeriod;
    }
    public Animate(String id, Point position, List< PImage > images, double animationPeriod){
        super(id,position,images);
        this.animationPeriod = animationPeriod;
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1; //imageindex +1
    }

    public Animation createAnimationAction(int repeatCount) {
        return new Animation(this, null, null, repeatCount);
    }

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

    // for only obstacle
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this, this.createAnimationAction( 0), this.getAnimationPeriod());
    }

}
