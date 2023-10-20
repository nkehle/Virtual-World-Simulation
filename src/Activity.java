/**
 * Specifed type of action
 */
public class Activity extends Action {

    public Activity(Active entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        this.executeActivityAction(scheduler);
    }

    public void executeActivityAction(EventScheduler scheduler) {
        if (this.entity instanceof Sapling sapling) {
            sapling.executeSaplingActivity(this.world, this.imageStore, scheduler);
        } else if (this.entity instanceof Tree tree) {
            tree.executeTreeActivity(this.world, this.imageStore, scheduler);
        } else if (this.entity instanceof Fairy fairy) {
            fairy.executeFairyActivity(this.world, this.imageStore, scheduler);
        } else if (this.entity instanceof DudeNotFull dnf) {
            dnf.executeDudeNotFullActivity(this.world, this.imageStore, scheduler);
        } else if (this.entity instanceof DudeFull df) {
            df.executeDudeFullActivity(this.world, this.imageStore, scheduler);
        } else if (this.entity instanceof Wizard wiz) {
            wiz.executeWizardActivity(this.world, this.imageStore, scheduler);
        } else if (this.entity instanceof Fire fire) {
            fire.executeFireActivity(this.world, this.imageStore, scheduler);
        } else if (this.entity instanceof Heart heart) {
            heart.executeHeartActivity(this.world, this.imageStore, scheduler);
        }else {
            throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", this.entity));
        }
    }

}
