import java.util.List;

import processing.core.PImage;

/**
 * Represents a background for the 2D world.
 */
public final class Background {
    private String id;
    private List<PImage> images; //public
    private int imageIndex;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }
    public static PImage getCurrentImage(Object object) { //ask teacher
        if (object instanceof Background background) {
            return background.images.get(background.imageIndex);
        } else {
            throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", object));
        }
    }

    public List<PImage> images() {
        return images;
    }
    public int imageIndex(){
        return imageIndex;
    }
}