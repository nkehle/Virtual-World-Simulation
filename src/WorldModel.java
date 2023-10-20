import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Ent[][] occupancy; //ent
    private Set<Ent> entities; //ent

    public int numRows() {
        return numRows;
    }

    public void setBackground(Background[][] background) {
        this.background = background;
    }

    public void setEntities(Set<Ent> entities) {
        this.entities = entities;
    } //ent

    public void setOccupancy(Ent[][] occupancy) {
        this.occupancy = occupancy;
    } //ent

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int numCols() {
        return numCols;
    }

    public Background[][] background() {
        return background;
    }

    public Ent[][] occupancy() {
        return occupancy;
    } //ent
    public Set<Ent> entities(){return this.entities;} //ent

    public WorldModel() {

    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Ent entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

    public boolean withinBounds(Point pos) {
        return pos.y() >= 0 && pos.y() < this.numRows && pos.x() >= 0 && pos.x() < this.numCols;
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    public Ent getOccupancyCell(Point pos) {
        return this.occupancy[pos.y()][pos.x()];
    }

    public void setOccupancyCell(Point pos, Ent entity) {
        this.occupancy[pos.y()][pos.x()] = entity;
    }

    public Optional<Ent> getOccupant(Point pos) {
        if (isOccupied(pos)) {
            return Optional.of(getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public  void addEntity(Ent entity) {
        if (this.withinBounds(entity.position())) {
            this.setOccupancyCell(entity.position(), entity);
            this.entities.add(entity);
        }
    }
    public void tryAddEntity(Ent entity) {
        if (isOccupied(entity.position())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        addEntity(entity);
    }
    public void removeEntity(EventScheduler scheduler, Ent entity) {
        scheduler.unscheduleAllEvents(entity);
        removeEntityAt(entity.position());
    }

    public void removeEntityAt(Point pos) {
        if (withinBounds(pos) && getOccupancyCell(pos) != null) {
            Ent entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            setOccupancyCell(pos, null);
        }
    }
    public int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x() - p2.x();
        int deltaY = p1.y() - p2.y();

        return deltaX * deltaX + deltaY * deltaY;
    }


    public Optional<Ent> findNearest(Point pos, List<Ent> ents) {
        List<Ent> ofType = new LinkedList<>();
        for (Ent ent : ents) {
            for (Ent entity : this.entities) {
                if (entity.getClass() == ent.getClass()) {
                    ofType.add(entity);
                }
            }
        }
        return nearestEntity(ofType, pos);
    }

    //Breton
    public Optional<Ent> nearestEntity(List<Ent> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Ent nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.position(), pos);

            for (Ent other : entities) {
                int otherDistance = distanceSquared(other.position(), pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

//    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
//        Point oldPos = entity.position;
//        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
//            this.setOccupancyCell(oldPos, null);
//            Optional<Entity> occupant = this.getOccupant(pos);
//            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
//            this.setOccupancyCell( pos, entity);
//            entity.position = pos;
//        }
//    }
}