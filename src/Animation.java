/**
 * Specifed type of action
 */
public class Animation extends Action{
    public Animation(Animate entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        this.executeAnimationAction(scheduler);
    }

    public void executeAnimationAction(EventScheduler scheduler) {
        if (this.entity instanceof Animate animate){
            animate.nextImage();
            if (this.repeatCount != 1) {
                scheduler.scheduleEvent(this.entity, animate.createAnimationAction
                        (Math.max(this.repeatCount - 1, 0)), animate.getAnimationPeriod());
            }
        }

    }
}
