package nettion.features.value.values;

import nettion.features.value.Value;

import java.util.function.Supplier;

public class Numbers<T extends Number> extends Value<T> {
    public T min;
    public T max;
    public T inc;
    private final boolean integer;

    public Numbers(String name, T value, T min, T max, T inc) {
        super(name, value, () -> true, () -> true, () -> true);
        this.min = min;
        this.max = max;
        this.inc = inc;
        this.integer = false;
    }

    public T getMin() {
        return this.min;
    }

    public T getMax() {
        return this.max;
    }

    public void setIncrement(T inc) {
        this.inc = inc;
    }

    public T getIncrement() {
        return this.inc;
    }

    public boolean isInteger() {
        return this.integer;
    }
}

