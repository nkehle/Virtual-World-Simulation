import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;
    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static final Random rand = new Random();

    private static final int COLOR_MASK = 0xffffff;
    private static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;

    private static final List<String> PATH_KEYS = new ArrayList<>(Arrays.asList("bridge", "dirt", "dirt_horiz", "dirt_vert_left", "dirt_vert_right", "dirt_bot_left_corner", "dirt_bot_right_up", "dirt_vert_left_bot"));

    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;

    private static final int PROPERTY_KEY = 0;
    private static final int PROPERTY_ID = 1;
    private static final int PROPERTY_COL = 2;
    private static final int PROPERTY_ROW = 3;
    private static final int ENTITY_NUM_PROPERTIES = 4;

    private static final String STUMP_KEY = "stump";
    private static final int STUMP_NUM_PROPERTIES = 0;

    private static final String SAPLING_KEY = "sapling";
    private static final int SAPLING_HEALTH = 0;
    private static final int SAPLING_NUM_PROPERTIES = 1;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_ANIMATION_PERIOD = 0;
    private static final int OBSTACLE_NUM_PROPERTIES = 1;

    private static final String DUDE_KEY = "dude";
    private static final int DUDE_ACTION_PERIOD = 0;
    private static final int DUDE_ANIMATION_PERIOD = 1;
    private static final int DUDE_LIMIT = 2;
    private static final int DUDE_NUM_PROPERTIES = 3;

    private static final String HOUSE_KEY = "house";
    private static final int HOUSE_NUM_PROPERTIES = 0;

    private static final String FAIRY_KEY = "fairy";
    private static final int FAIRY_ANIMATION_PERIOD = 0;
    private static final int FAIRY_ACTION_PERIOD = 1;
    private static final int FAIRY_NUM_PROPERTIES = 2;

    private static final String TREE_KEY = "tree";
    private static final int TREE_ANIMATION_PERIOD = 0;
    private static final int TREE_ACTION_PERIOD = 1;
    private static final int TREE_HEALTH = 2;
    private static final int TREE_NUM_PROPERTIES = 3;


    private static final String WIZARD_KEY = "wizard";
    private static final int WIZARD_MAX = 1;
    private static int wizardCount = 0;
    private static final String FIRE_KEY = "fire";
    private static final String BURNT_KEY = "burnt";
    private static final String METEOR_KEY = "met";

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;
    private double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    private static List<PImage> getImageList(ImageStore imageStore, String key) {
        return imageStore.images().getOrDefault(key, imageStore.defaultImages());
    }

    //parsing methods
    private static void parseEntity(WorldModel world, String line, ImageStore imageStore) {
        String[] properties = line.split(" ", ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[PROPERTY_COL]), Integer.parseInt(properties[PROPERTY_ROW]));

            properties = properties.length == ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case OBSTACLE_KEY -> parseObstacle(world, properties, pt, id, imageStore);
                case DUDE_KEY -> parseDude(world, properties, pt, id, imageStore);
                case FAIRY_KEY -> parseFairy(world, properties, pt, id, imageStore);
                case HOUSE_KEY -> parseHouse(world, properties, pt, id, imageStore);
                case TREE_KEY -> parseTree(world, properties, pt, id, imageStore);
                case SAPLING_KEY -> parseSapling(world, properties, pt, id, imageStore);
                case STUMP_KEY -> parseStump(world, properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }
    //ent
    private static void parseSapling(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            //Entity entity = createSapling(id, pt, getImageList(imageStore, SAPLING_KEY), health);
            Sapling entity = new Sapling(id, pt, getImageList(imageStore, SAPLING_KEY), SAPLING_ACTION_ANIMATION_PERIOD,SAPLING_ACTION_ANIMATION_PERIOD,0,0,0, 0);  //chnaged to health = 0
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }

    //ent
    private static void parseDude(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            DudeNotFull entity = new DudeNotFull(id, pt, getImageList(imageStore, DUDE_KEY),Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]),0);

            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    private static void parseFairy(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Fairy entity = new Fairy(id, pt, getImageList(imageStore, FAIRY_KEY), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]),Double.parseDouble(properties[FAIRY_ACTION_PERIOD]),0,0);

            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    private static void parseTree(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Tree entity = new Tree(id, pt, getImageList(imageStore, TREE_KEY),Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Double.parseDouble(properties[TREE_ACTION_PERIOD]),0,0,0,Integer.parseInt(properties[TREE_HEALTH]));
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    private static void parseObstacle(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {

            Obstacle entity = new Obstacle(id, pt, getImageList(imageStore, OBSTACLE_KEY), Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]));

            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    private static void parseHouse(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            House entity = new House(id, pt, getImageList(imageStore, HOUSE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }
    private static void parseStump(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Stump entity = new Stump(id, pt, getImageList(imageStore, STUMP_KEY));
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.currentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    private void update(double frameTime){
        this.scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.x() + ", " + pressed.y());
        Optional<Ent> entityOptional = this.world.getOccupant(pressed);

        if (entityOptional.isPresent()) {
            Ent entity = entityOptional.get();
            if(entity instanceof Tree tree){
                System.out.println(tree.id() + ": " + tree.getClass() + " : " + tree.health()); //fix
            } else if (entity instanceof Sapling sapling){
                System.out.println(sapling.id() + ": " + sapling.getClass() + " : " + sapling.health()); //fix
            } else {
                System.out.println(entity.id() + ": " + entity.getClass() + " : " + 0); //fix
            }
        }
        makeCrater(pressed, entityOptional);
    }

    public void makeCrater(Point p, Optional<Ent> entity){
        // world changing event - meteor with a large space of black ground and a flame on the rock
        Fire flame = new Fire("f", new Point(p.x(),p.y()), getImageList(imageStore, FIRE_KEY),0.25,10,0,0);

        // if there is an ent in place of the meteor
        if (entity.isPresent()) {
            Ent e1 = entity.get();
            world.removeEntity(scheduler,e1);
        }
        addMeteor(p);
        this.world.addEntity(flame);
        flame.scheduleActions(this.scheduler, this.world, this.imageStore);
        // add the meteor
        addMeteor(p);

        // limits to only 1 wizard
        if (wizardCount == 0){
            addWizard(p);
            addHeart(p);
            wizardCount ++;
        }
    }

    private void addHeart(Point p){
        Optional<Ent> closestFairy = world.findNearest(p, new ArrayList<>(List.of(new Fairy("",new Point(0,0),
                null,1,1,1,1))));
        if(closestFairy.get() instanceof Fairy fairy){
            fairy.transformHeart(this.world, this.scheduler, this.imageStore);
        }
    }
    private void addWizard(Point p){
        //find the nearest dude
        Optional<Ent> closestDude = world.findNearest(p, new ArrayList<>(List.of(new DudeNotFull("",new Point(0,0),
                null,1,1,1,1))));
        if(closestDude.get() instanceof DudeNotFull dnf){
            dnf.transformWizard(this.world, this.scheduler, this.imageStore);
        }
    }

    private void addMeteor(Point p) {
        Meteor met = new Meteor("meteor", p, getImageList(imageStore, METEOR_KEY));
        this.world.addEntity(met);;
        List<Point> points = met.createBurntGroundPoints(p);
        changeBurnt(points); //creates new background
    }

    // change the background to a burnt image
    public void changeBurnt(List<Point> points){
        for(Point point: points){
            Background burnt = new Background("burnt",getImageList(imageStore,BURNT_KEY));
            view.setBackgroundCell(this.world, point, burnt);
        }
    }

    private void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Ent entity : world.entities()) {
            if (entity instanceof Active act){
                act.scheduleActions(scheduler, world, imageStore); // CHECK
            } else if(entity instanceof Animate ani){
                ani.scheduleActions(scheduler, world, imageStore); // CHECK
            }
        }
    }

    private Point mouseToPoint() {
        return view.viewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    private static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, getImageList(imageStore, DEFAULT_IMAGE_NAME));
    }

    private static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    private void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            load(world, in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            load(world, in, imageStore, createDefaultBackground(imageStore));
        }
    }

    private void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    private static void parseBackgroundRow(WorldModel world, String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < world.numRows()){
            int rows = Math.min(cells.length, world.numCols());
            for (int col = 0; col < rows; col++){
                world.background()[row][col] = new Background(cells[col], getImageList(imageStore, cells[col]));
            }
        }
    }

    private static void parseSaveFile(WorldModel world, Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> world.setBackground(new Background[world.numRows()][world.numCols()]);
                    case "Entities:" -> {
                        world.setOccupancy(new Ent[world.numRows()][world.numCols()]);
                        world.setEntities(new HashSet<>());
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> world.setNumRows(Integer.parseInt(line));
                    case "Cols:" -> world.setNumCols(Integer.parseInt(line));
                    case "Backgrounds:" -> parseBackgroundRow(world, line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> parseEntity(world, line, imageStore);
                }
            }
        }
    }

    private static void load(WorldModel world, Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        parseSaveFile(world, saveFile, imageStore, defaultBackground);
        if(world.background() == null){
            world.setBackground(new Background[world.numRows()][world.numCols()]);
            for (Background[] row : world.background())
                Arrays.fill(row, defaultBackground);
        }
        if(world.occupancy() == null){
            world.setOccupancy(new Ent[world.numRows()][world.numCols()]);
            world.setEntities(new HashSet<>());
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }

    private static void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }
    private static List<PImage> getImages(Map<String, List<PImage>> images, String key) {
        return images.computeIfAbsent(key, k -> new LinkedList<>());
    }

    private static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

    private static void loadImages(Scanner in, ImageStore imageStore, PApplet screen) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                processImageLine(imageStore.images(), in.nextLine(), screen);
            } catch (NumberFormatException e) {
                System.out.printf("Image format error on line %d\n", lineNumber);
            }
            lineNumber++;
        }
    }

}