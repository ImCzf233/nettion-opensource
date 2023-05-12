package nettion.utils;

public enum Direction {
    FORWARDS,
    BACKWARDS;

    public boolean forwards() {
        return this == Direction.FORWARDS;
    }
}
