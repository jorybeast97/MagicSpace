package singleton;

//双检索
public class DoubleCheckMode {

    private static volatile DoubleCheckMode result = null;

    public static DoubleCheckMode getInstance() {
        if (result == null) {
            synchronized (DoubleCheckMode.class) {
                if (result == null) {
                    result = new DoubleCheckMode();
                }
            }
        }
        return result;
    }

    private DoubleCheckMode() {

    }

}
