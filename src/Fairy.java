import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Fairy extends Movable {
    private static final String SAPLING_KEY = "sapling";
    private static final String STUMP_KEY = "stump";
    private static final String HEART_KEY = "heart";
    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;

    public Fairy(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
    }

    // optional ent
    public void executeFairyActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // fix
        Optional<Ent> fairyTarget = world.findNearest(this.position, new ArrayList<>(List.of(new Stump("tmp", new Point(0, 0), getImageList(imageStore, STUMP_KEY)))));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position();

            if (this.moveToFairy(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = new Sapling(SAPLING_KEY + "_" + fairyTarget.get().id(), tgtPos, getImageList(imageStore, SAPLING_KEY), SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, 0, SAPLING_HEALTH_LIMIT, 0);

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
    }

    public boolean moveToFairy(WorldModel world, Ent target, EventScheduler scheduler) {
        if (adjacent(this.position, target.position())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionFairy(world, target.position());
            if (!this.position.equals(nextPos)) {
                moveEntity(world, scheduler, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionFairy(WorldModel world, Point destPos) {
        //PathingStrategy ps = new AStarPathingStrategy();
        PathingStrategy ps = new SingleStepPathingStrategy();

        Point newPos = new Point(this.position.x(), this.position.y());

        // has the destination been reached?
        if (!adjacent(this.position, destPos)) {
            List<Point> path = ps.computePath(
                    this.position,
                    destPos,
                    p1 -> world.withinBounds(p1) && !world.isOccupied(p1),
                    this::adjacent, //doesnt call
                    ps.DIAG_NEIGHBORS
            );

            // returns the next step if target has not been reached
            if (path.size() > 0) {
                //newPos = path.get(0);
                newPos = path.get(path.size() - 1);
            }
        }
        // destination has already been reached
        return newPos;
    }

    public void transformHeart(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Heart heart = new Heart(HEART_KEY, this.position, getImageList(imageStore,HEART_KEY),0.1,0.20,0,0);

        // remove the fairy
        world.removeEntity(scheduler, this);
        scheduler.unscheduleAllEvents(this);

        //create heart
        world.addEntity(heart);
        heart.scheduleActions(scheduler, world, imageStore);
    }
}
