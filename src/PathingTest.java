import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PathingTest {

    @Test
    public void testSingleStepNoObstacles() {
        boolean[][] grid = {
                //start
                {true, true, true},
                {true, true, true},
                {true, true, true}
                       //end ^^^^
        };

        List<Point> actual = new SingleStepPathingStrategy().computePath(
                new Point(0, 0), new Point(2, 2),
                (Point p) -> withinBounds(p,3,3) && grid[p.y()][p.x()] ,
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        List<Point> l1 = new ArrayList<>();
        l1.add(new Point(1,0));

        assertEquals(l1, actual);
    }

    @Test
    public void testAStarNoObstacles() {
        boolean[][] grid = {
                //start
                {true, true, true},
                {true, true, true},
                {true, true, true}
                        //end ^^^^
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(0, 0), new Point(2, 2),
                (Point p) -> withinBounds(p,3,3) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        List<Point> l1 = new ArrayList<>();
        l1.add(new Point(1,0));

        assertEquals(l1.get(0), actual.get(actual.size()-1));
    }

    @Test
    public void testAStarNoObstacles2() {
        boolean[][] grid = {
                //start
                {true, true, true,},
                {true, true, true},
                {true, true, true}
                //end ^^^^
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(0, 0), new Point(2, 2),
                (Point p) -> withinBounds(p,3,3) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertEquals(3, actual.size());
    }

    @Test
    public void testAStarObstacles1() {
        boolean[][] grid = {
                //start
                {true, false, false, true, true, true},
                {true, false, false, true, false, true},
                {true, false, false, true, false, true},
                {true, false, false, true, false, true},
                {true, false, false, true, false, true},
                {true, true, true, true, false, true}
                                         //end ^^^^
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(0, 0), new Point(5, 5),
                (Point p) -> withinBounds(p,6,6) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertEquals(19, actual.size());
    }

    @Test
    public void testAStarObstacles2() {
        boolean[][] grid = {
                //start
                {true, true, true, false, true, true},
                {true, true, true, false, true, true},
                {true, true, true, false, true, true},
                {true, false, false, false, true, true},
                {true, true, true, true, true, true},
                {true, true, true, true, true, true}
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(1, 1), new Point(5, 5),
                (Point p) -> withinBounds(p,6,6) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertEquals(9, actual.size());
    }

    @Test
    public void testAStarObstacles3() {
        boolean[][] grid = {
                //start
                {true,  true,  true,  true,  true,  true},
                {false, false, false, false, false, true},
                {true,  true,  true,  false,  false, true},
                {true,  false, true,  false,  false, true},
                {true,  false, false, false, false, true},
                {true,  true,  true,  true,  true,  true}
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(0, 0), new Point(2, 3),
                (Point p) -> withinBounds(p,6,6) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertEquals(20, actual.size());
    }

    @Test
    public void testAStarSmallObstacles1() {
        boolean[][] grid = {
                //start
                {true, true,  true},
                {true, false, true},
                {true, false, true}
                        //end ^^^^
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(0, 0), new Point(2, 2),
                (Point p) -> withinBounds(p,3,3) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        List<Point> l1 = new ArrayList<>();
        l1.add(new Point(1,0));
        l1.add(new Point(2,0));
        l1.add(new Point(2,1));
        Collections.reverse(l1);

        assertEquals(l1, actual);
    }

    @Test
    public void testAStarMediumObstacles2() {
        boolean[][] grid = {
                //start
                {true, true, true,  true, true},
                {true, true, true, false, true},
                {true, true, true, false, true},
                {true, false,false, false,false},
                {true, true, true,  true, true}
                                     //end ^^^^
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(0, 0), new Point(4, 4),
                (Point p) -> withinBounds(p,5,5) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        List<Point> l1 = new ArrayList<>();
        l1.add(new Point(0,1));
        l1.add(new Point(0,2));
        l1.add(new Point(0,3));
        l1.add(new Point(0,4));
        l1.add(new Point(1,4));
        l1.add(new Point(2,4));
        l1.add(new Point(3,4));

        Collections.reverse(l1);

        assertEquals(l1, actual);
    }

    @Test
    public void testAStarLargeObstacles3() {
        boolean[][] grid = {
                      //start
                {true, true,  true,  true,  true, true, true, true},
                {true, true,  true,  false, true, true, true, true},
                {true, true,  true,  false, true, true, true, true},
                {true, true,  true,  false, true, true, true, true},
                {true, true,  true,  false, true, true, true, true},
                {true, false, false, false, true, true, true, true},
                {true, true,  true,  true,  true, true, true, true},
                {true, true,  true,  true,  true, true, true, true},
                                                     //end ^^^^
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(1, 0), new Point(7, 7),
                (Point p) -> withinBounds(p,8,8) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        List<Point> l1 = new ArrayList<>();
        l1.add(new Point(2,0)); l1.add(new Point(3,0)); l1.add(new Point(4,0));
        l1.add(new Point(4,1)); l1.add(new Point(4,2)); l1.add(new Point(4,3));
        l1.add(new Point(4,4)); l1.add(new Point(4,5)); l1.add(new Point(4,6));
        l1.add(new Point(5,6)); l1.add(new Point(5,7)); l1.add(new Point(6,7));

        Collections.reverse(l1);

        assertEquals(l1, actual);
    }

    @Test
    public void testAStarNoPath() {
        boolean[][] grid = {
                //start
                {true, false, true},
                {false, false, true},
                {true, false, true}
                //end ^^^^
        };

        List<Point> actual = new AStarPathingStrategy().computePath(
                new Point(0, 0), new Point(2, 2),
                (Point p) -> withinBounds(p,3,3) && grid[p.y()][p.x()],
                (Point p1, Point p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        List<Point> l1 = new ArrayList<>();

        assertEquals(l1, actual);
    }
    @Test
    public void testEquals1(){
        Point p1 = new Point(1,2);
        Point p2 = new Point(1,2);
        assertTrue(p1.equals(p2));
    }

    @Test
    public void testEquals2(){
        HashSet<WorldNode> openSet = new HashSet<>();
        WorldNode n1 = new WorldNode(0,0,new Point(1,2),null);
        WorldNode n2 = new WorldNode(1,3,new Point(1,2),null);
        assertTrue(n1.equals(n2));
    }

    @Test
    public void testEquals3(){
        HashSet<WorldNode> openSet = new HashSet<>();
        WorldNode n1 = new WorldNode(0,0,new Point(0,0),null);
        WorldNode n2 = new WorldNode(0,0,new Point(0,0),null);
        openSet.add(n1);
        //openSet.add(n2);
        assertTrue(openSet.contains(n2));
        //assertEquals(openSet.size(),1);
    }

    public boolean withinBounds(Point pos,int width, int height) {
        return pos.y() >= 0 && pos.y() < height && pos.x() >= 0 && pos.x() < width;
    }
}

/*
    A star will give a list of points
    Check the length of the path and checks to see if it's the length of the shortesr
        - helper isValid(List<Point>, int, start, end){
            - check if length is the same
            - check if it is actully a path from start to end
                -all adjacent to eachother and adjcaent to the target
         }
 */