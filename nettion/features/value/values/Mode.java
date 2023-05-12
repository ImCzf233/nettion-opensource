package nettion.features.value.values;

import nettion.features.value.Value;

import java.util.function.Supplier;

public class Mode<V extends Enum>
extends Value<V> {
    public final V[] modes;
    public boolean state = false;
    public float Anim = 0;

    public Mode(String name, V[] modes, V value) {
        super(name, value, () -> true, () -> true, () -> true);
        this.modes = modes;
        for (int i = 0; i < this.getModes().length; ++i) {
            this.mode.add(String.valueOf(this.getModes()[i]));
        }
    }

    @Override
    public String getModeAt(int index) {
        return this.mode.get(index);
    }

    public String getModeAsString() {
        return this.getValue().name();
    }

    public V[] getModes() {
        return this.modes;
    }

    public void setMode(String mode) {
        V[] arrV = this.modes;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            V e = arrV[n2];
            if (e.name().equalsIgnoreCase(mode)) {
                this.setValue(e);
            }
            ++n2;
        }
    }

    public void setCurrentMode(int current) {
        if (current > mode.size() - 1) {
            System.out.println("Value is to big! Set to 0. (" + mode.size() + ")");
            return;
        }
        this.current = current;
        setValue(modes[current]);
    }

    public void setMode(int mode) {
        this.setValue(this.modes[mode]);
    }

    public boolean isValid(String name) {
        V[] arrV = this.modes;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            V e = arrV[n2];
            if (e.name().equalsIgnoreCase(name)) {
                return true;
            }
            ++n2;
        }
        return false;
    }
}

