import processing.core.PImage;

import java.util.List;
import java.util.Random;

public abstract class Ent {
    protected String id;
    protected Point position;
    protected List<PImage> images;
    protected int imageIndex;

    public Ent(String id, Point position, List<PImage> images){
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    String id(){
        return this.id;
    }
    Point position(){
        return this.position;
    }
    void setPosition(Point p1){
        this.position = p1;
    }
    List<PImage> images(){
        return this.images;
    }
    int imageIndex(){
        return this.imageIndex;
    }

    public boolean adjacent(Point p1, Point p2) {
        return (p1.x() == p2.x() && Math.abs(p1.y() - p2.y()) == 1) || (p1.y() == p2.y() && Math.abs(p1.x() - p2.x()) == 1);
    }

    public static PImage getCurrentImage(Object object) {
        if (object instanceof Ent entity) {
            return entity.images().get(entity.imageIndex() % entity.images().size());
        } else {
            throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", object));
        }
    }

    public int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    public double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    public List<PImage> getImageList(ImageStore imageStore, String key) {
        return imageStore.images().getOrDefault(key, imageStore.defaultImages());
    }

    public String log(){
        return this.id().isEmpty() ? null :
                String.format("%s %d %d %d", this.id(), this.position.x(), this.position.y(), this.imageIndex);
    }
}
