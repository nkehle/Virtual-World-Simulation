import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public class Meteor extends Ent{
    public Meteor(String id, Point position, List<PImage> images) {
        super(id, position, images);
    }

    public List<Point> createBurntGroundPoints(Point p){
        List<Point> points = new ArrayList<>();
        points.add(new Point(p.x(),p.y()));
        //change the background for crater
        points.add(new Point(p.x()+1,p.y()));
        points.add(new Point(p.x()+2,p.y()));
        points.add(new Point(p.x()+1,p.y()+1));

        points.add(new Point(p.x()-1,p.y()));
        points.add(new Point(p.x()-2,p.y()));
        points.add(new Point(p.x()-1,p.y()-1));

        points.add(new Point(p.x(),p.y()+1));
        points.add(new Point(p.x(),p.y()+2));
        points.add(new Point(p.x()-1,p.y()+1));

        points.add(new Point(p.x(),p.y()-1));
        points.add(new Point(p.x(),p.y()-2));
        points.add(new Point(p.x()+1,p.y()-1));
        return points;
    }
}
