package nettion.features.value.values;

import nettion.features.value.Value;

import java.util.function.Supplier;

public class Option<V> extends Value<V> {
    public float AnimOption = 0;

    public Option(String name, V enabled) {
        super(name, enabled, () -> true, () -> true, () -> true);
    }
}

