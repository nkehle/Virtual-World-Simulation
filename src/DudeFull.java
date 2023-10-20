import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Specific kind of entity called DudeFull
 */

public class DudeFull extends Dude{
    public DudeFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
    }

    public boolean moveToFull(WorldModel world, Ent target, EventScheduler scheduler) {
        if (adjacent(this.position, target.position())) {
            return true;
        } else {
            Point nextPos = nextPositionDude(world, target.position());
            if (!this.position.equals(nextPos)) {
                moveEntity(world,scheduler,nextPos);
            }
            return false;
        }
    }
    public void executeDudeFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Ent> fullTarget = world.findNearest(this.position, new ArrayList<>(List.of(new House("",new Point(0,0),null))));

        if (fullTarget.isPresent() && this.moveToFull(world, fullTarget.get(), scheduler)) {
            this.transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNotFull dude = new DudeNotFull(this.id, this.position, this.images, this.animationPeriod, this.actionPeriod, this.resourceLimit, 0);

        world.removeEntity(scheduler, this);

        world.addEntity(dude);  // fix
        dude.scheduleActions(scheduler, world, imageStore);
    }
}
