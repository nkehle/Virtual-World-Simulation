import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DudeNotFull extends Dude{

    public DudeNotFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
    }

    // optional ent
    public void executeDudeNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Ent> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(new Tree("",new Point(0,0), null,0,0,0,0,0,0),
                new Sapling( "",new Point(0,0), null,0,0,0,0,0,0))));

        if (target.isEmpty() || !this.moveToNotFull(world, target.get(), scheduler) || !this.transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

    private boolean moveToNotFull(WorldModel world, Ent target, EventScheduler scheduler) {
        if (adjacent(this.position, target.position())) {
            this.resourceCount += 1;
            if (target instanceof Sapling sap){
                sap.subHealth();
            }
            if(target instanceof Tree tree){
                tree.subHealth();
            } else if (target instanceof Sapling sap){
                sap.subHealth();
            }
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.position());

            if (!this.position.equals(nextPos)) {
                moveEntity(world,scheduler,nextPos);
            }
            return false;
        }
    }
    private boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            DudeFull dude = new DudeFull(this.id, this.position, this.images, this.animationPeriod,this.actionPeriod, this.resourceLimit, 0);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }
    public void transformWizard(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        Wizard wiz = new Wizard(WIZARD_KEY, this.position, getImageList(imageStore,WIZARD_KEY),0.2,0.5,0,0);

        // remove the dude
        world.removeEntity(scheduler, this);
        scheduler.unscheduleAllEvents(this);

        //create wizard
        world.addEntity(wiz);
        wiz.scheduleActions(scheduler, world, imageStore);
    }
}
