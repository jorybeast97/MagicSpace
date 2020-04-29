package singleton;

public class LazyMode {

    private static LazyMode lazyMode = null;

    private LazyMode() {

    }

    public static LazyMode getLazyMode() {
        if (lazyMode == null) lazyMode = new LazyMode();
        return lazyMode;
    }
}
