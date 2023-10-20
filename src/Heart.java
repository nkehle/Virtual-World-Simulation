import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Heart extends Movable{
    private static final String SAPLING_KEY = "sapling";
    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;
    private static final String HEART_KEY = "heart";
    private int newHeartCtr = 1;
    private boolean newLocation = true;
    private Point nextLocation;
    public Heart(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
    }
    public Point nextHeartLocation(WorldModel world) {
        int ctr = 0;
        while (ctr<1000) {
            Point p1 = new Point(Wizard.randomInt(0, 30), Wizard.randomInt(0, 30));
            ctr++;
            if (!(world.isOccupied(p1)) && world.withinBounds(p1)) {
                return p1;
            }
        } return this.position;
    }

    public void executeHeartActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if(this.newLocation){
            this.nextLocation = nextHeartLocation(world);
            this.newLocation = false;
        }

        if (this.moveToHeart(world, this.nextLocation, scheduler)) {
            // create a new tree
            Sapling sapling = new Sapling(SAPLING_KEY, this.nextLocation, getImageList(imageStore, SAPLING_KEY), SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, 0, SAPLING_HEALTH_LIMIT, 0);
            world.addEntity(sapling);
            sapling.scheduleActions(scheduler, world, imageStore);

            // split into two hearts if it reaches 10 planted trees
            if(this.newHeartCtr % 10 == 0){
                Heart heart = new Heart(HEART_KEY, this.position, getImageList(imageStore,HEART_KEY),0.2,0.20,0,0);
                world.addEntity(heart);
                heart.scheduleActions(scheduler, world, imageStore);
            }
            this.newHeartCtr++;
            this.newLocation = true;
        }
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
    }

    public boolean moveToHeart(WorldModel world, Point nextPosition, EventScheduler scheduler) {
        if (adjacent(this.position, nextPosition)) {
            return true;
        } else {
            Point nextPos = this.nextPositionHeart(world, nextPosition);
            if (!this.position.equals(nextPos)) {
                moveEntity(world, scheduler, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionHeart(WorldModel world, Point destPos) {
        PathingStrategy ps = new AStarPathingStrategy();
        //PathingStrategy ps = new SingleStepPathingStrategy();

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
}
