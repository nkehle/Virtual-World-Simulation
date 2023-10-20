import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Movable extends Active{

    protected static final String WIZARD_KEY = "wizard";

    public Movable(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount){
        super(id, position, images, animationPeriod, actionPeriod,resourceLimit,resourceCount);
    }

    public void moveEntity(WorldModel world, EventScheduler scheduler, Point pos) {
        Point oldPos = this.position;
        if (world.withinBounds(pos) && !pos.equals(oldPos)) {
            world.setOccupancyCell(oldPos, null);
            Optional<Ent> occupant = world.getOccupant(pos);
            occupant.ifPresent(target -> world.removeEntity(scheduler, target));
            world.setOccupancyCell(pos, this);
            this.position = pos;
        }
    }

}
