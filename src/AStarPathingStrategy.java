import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.*;

public class AStarPathingStrategy implements PathingStrategy {

    public List<Point> computePath(
        Point start,
        Point end,
        Predicate<Point> canPassThrough,
        BiPredicate<Point, Point> withinReach,
        Function<Point, Stream<Point>> potentialNeighbors) {

        // comparator for priority queue
        Comparator<WorldNode> lesser = new Comparator<WorldNode>() {
            @Override
            public int compare(WorldNode n1, WorldNode n2) {
                return (n1.getTd() - n2.getTd());
            }
        };

        //initialize open and closed list
        PriorityQueue<WorldNode> open = new PriorityQueue<>(lesser);  // need to give a comparator
        HashSet<WorldNode> openSet = new HashSet<WorldNode>();

        HashSet<WorldNode> closed = new HashSet<WorldNode>();

        WorldNode startNode = new WorldNode(0, manhattanDistance(start, end), start, null);
        //WorldNode endNode = new WorldNode(0, 0, end, null);

        WorldNode cur = null;

        //add start node to the open
        open.add(startNode);
        openSet.add(startNode);

        while (!(open.isEmpty())) {
            // grab the first node
            cur = open.poll();

            // is it adjacent to the target
            if (withinReach.test(cur.getPosition(), end)) {
                break;  // found the path
            }

            if (!closed.contains(cur)) {
                // add to the closed
                closed.add(cur);

                // not sure about this one
                WorldNode finalCur = cur;

                List<WorldNode> neighbors = potentialNeighbors.apply(cur.getPosition())  // Stream of <Points>
                        .filter(canPassThrough)
                        .filter(node -> !closed.contains(node)) // ignore the ones in the closed
                        .map(point -> new WorldNode(finalCur.getDfs() + 1, manhattanDistance(point, end), point, finalCur))
                        //.limit(1) //just added
                        .toList();   //switched from collecotrs.tolist....


                //add neighbors to the open list as long as they are not already in it
                for (WorldNode neighbor : neighbors) {
                    if(!openSet.contains(neighbor)){
                        open.add(neighbor);
                        openSet.add(neighbor);
                    }

                }
            }
        }
        return contructPath(cur, start, new ArrayList<>());
    }

    public ArrayList<Point> contructPath(WorldNode pathNode, Point start, ArrayList<Point> path){
        // got back to the start
        if(pathNode.getPosition().equals(start)){  //pathNode.getParent() == null
            return path;
        }
        path.add(pathNode.getPosition());
        return contructPath(pathNode.getParent(),start, path);
    }

    public int manhattanDistance(Point p1, Point p2) {
        return Math.abs(p2.x() - p1.x()) + Math.abs(p2.y()-p1.y());
    }
}
