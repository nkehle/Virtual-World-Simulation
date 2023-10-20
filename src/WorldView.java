import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView {
    private PApplet screen;
    private WorldModel world;
    private int tileWidth;
    private int tileHeight;
    private Viewport viewport;

    public Viewport viewport() {
        return viewport;
    }

    public WorldView(int numRows, int numCols, PApplet screen, WorldModel world, int tileWidth, int tileHeight) {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }

    public void drawViewport() {
        drawBackground();
        drawEntities();
    }

//    private PImage getCurrentImage(Object object) {
//        if (object instanceof Background background) {
//            return background.images.get(background.imageIndex);
//        } else if (object instanceof Entity entity) {
//            return entity.images.get(entity.imageIndex % entity.images.size());
//        } else {
//            throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", object));
//        }
//    }
    private Optional<PImage> getBackgroundImage(WorldModel world, Point pos) {
        if (world.withinBounds(pos)) {
            return Optional.of(Background.getCurrentImage(getBackgroundCell(world, pos)));
        } else {
            return Optional.empty();
        }
    }

    private void drawBackground() {
        for (int row = 0; row < this.viewport.numRows(); row++) {
            for (int col = 0; col < this.viewport.numCols(); col++) {
                Point worldPoint = this.viewport.viewportToWorld(col, row);
                Optional<PImage> image = getBackgroundImage(this.world, worldPoint);
                if (image.isPresent()) {
                    this.screen.image(image.get(), col * this.tileWidth, row * this.tileHeight);
                }
            }
        }
    }

    private Background getBackgroundCell(WorldModel world, Point pos) {
        return world.background()[pos.y()][pos.x()];
    }

    protected void setBackgroundCell(WorldModel world, Point pos, Background background) {
        world.background()[pos.y()][pos.x()] = background;
    }

//    public Point viewportToWorld(Viewport viewport, int col, int row) {
//        return new Point(col + viewport.col, row + viewport.row);
//    }
//
//    public Point worldToViewport(Viewport viewport, int col, int row) {
//        return new Point(col - viewport.col, row - viewport.row);
//    }

    private int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public void shiftView(int colDelta, int rowDelta) {
        int newCol = clamp(this.viewport.col() + colDelta, 0, this.world.numCols() - this.viewport.numCols());
        int newRow = clamp(this.viewport.row() + rowDelta, 0, this.world.numRows() - this.viewport.numRows());

        this.viewport.shift(newCol, newRow);
    }
    private void drawEntities() {
        for (Ent entity : this.world.entities()) {
            Point pos = entity.position();

            if (this.viewport.contains(pos)) {
                Point viewPoint = this.viewport.worldToViewport(pos.x(), pos.y());
                this.screen.image(Ent.getCurrentImage(entity), viewPoint.x() * this.tileWidth, viewPoint.y() * this.tileHeight);
            }
        }
    }

}