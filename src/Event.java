/**
 * An event is made up of an Entity that is taking an
 * Action a specified time.
 */
public final class Event {
    private Action action;
    private double time;
    private Ent entity;

    public Action action() {
        return action;
    }
    public double time() {
        return time;
    }
    public Ent entity() {
        return entity;
    }

    public Event(Action action, double time, Ent entity) {
        this.action = action;
        this.time = time;
        this.entity = entity;
    }
}
