import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.*;


public class SingleStepPathingStrategy implements PathingStrategy{

        public List<Point> computePath(
                Point start,
                Point end,
                Predicate<Point> canPassThrough,
                BiPredicate<Point, Point> withinReach,
                Function<Point, Stream<Point>> potentialNeighbors)
        {
            /* Does not check withinReach.  Since only a single step is taken
             * on each call, the caller will need to check if the destination
             * has been reached.
             */
            return potentialNeighbors.apply(start)
                    .filter(canPassThrough)
                    .filter(pt ->
                            !pt.equals(start)
                                    && !pt.equals(end)
                                    && Math.abs(end.x() - pt.x()) <= Math.abs(end.x() - start.x()) //gets closer in the x direction
                                    && Math.abs(end.y() - pt.y()) <= Math.abs(end.y() - start.y())) //gets closer in the y direction
                    .limit(1)
                    .collect(Collectors.toList());
        }
}
