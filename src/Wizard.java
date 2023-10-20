import processing.core.PImage;

import java.util.*;


public class Wizard extends Movable {
    private boolean newLocation = true;
    private Point nextLocation;
    private final String SUPERDUDE_KEY= "superdude";

    public Wizard(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
        this.nextLocation = this.position;
    }

    public boolean moveToWizard(WorldModel world, Point target, EventScheduler scheduler) {
        if (adjacent(this.position, target)) {
            return true;
        } else {
            Point nextPos = nextPositionWizard(world, target);
            if (!this.position.equals(nextPos)) {
                moveEntity(world,scheduler,nextPos);
            }
            return false;
        }
    }
    protected static int randomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public Point nextWizardLocation(WorldModel world) {
        int ctr = 0;
        while (ctr<1000) {
            Point p1 = new Point(Wizard.randomInt(0, 30), Wizard.randomInt(0, 30));
            ctr++;
            if (!(world.isOccupied(p1)) && world.withinBounds(p1)) {
                return p1;
            }
        } return this.position;
    }

    public void executeWizardActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if(this.newLocation){
            this.nextLocation = nextWizardLocation(world);
            this.newLocation = false;
        }

        if (this.moveToWizard(world, this.nextLocation, scheduler)) {
                this.newLocation = true;

                // creates faster dude that's a diffrenet color
                DudeNotFull dude = new DudeNotFull("superdude", this.nextLocation, getImageList(imageStore, SUPERDUDE_KEY), 0.1, 0.4, 50, 0);
                world.addEntity(dude);  // fix
                dude.scheduleActions(scheduler, world, imageStore);

        }
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
    }

    public Point nextPositionWizard(WorldModel world, Point destPos){
        //PathingStrategy ps = new SingleStepPathingStrategy();
        PathingStrategy ps = new AStarPathingStrategy();

        // already reached the target
        if (this.position.adjacent(destPos)){
            return this.position;
        } else {
            List<Point> path = ps.computePath(
                    this.position,
                    destPos,
                    p1 -> world.withinBounds(p1) && !world.isOccupied(p1),
                    this::adjacent, //doesnt call
                    ps.CARDINAL_NEIGHBORS
            );
            if(path.size()>0) {
                return path.get(path.size() - 1);
            } return this.position;
        }
    }

}
