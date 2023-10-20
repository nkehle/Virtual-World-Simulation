import processing.core.PImage;

import java.util.List;

abstract public class Dude extends Movable{
    public Dude(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
    }


    public Point nextPositionDude(WorldModel world, Point destPos){
        PathingStrategy ps = new SingleStepPathingStrategy();
        //PathingStrategy ps = new AStarPathingStrategy();

        Point newPos = new Point(this.position.x(), this.position.y());

        // has the destination been reached?
        if (!adjacent(this.position, destPos)) {
            List<Point> path = ps.computePath(
                    this.position,
                    destPos,
                    p1 -> world.withinBounds(p1) && (world.isOccupied(p1) && (world.getOccupancyCell(p1) instanceof Stump)) || !(world.isOccupied(p1)),
                    this::adjacent, //doesnt call
                    //ps.CARDINAL_NEIGHBORS
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
