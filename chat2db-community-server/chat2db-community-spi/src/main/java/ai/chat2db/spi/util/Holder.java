


package ai.chat2db.spi.util;

public class Holder<T> {
    private T value;

    public Holder() {}

    public Holder(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
