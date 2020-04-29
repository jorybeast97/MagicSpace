package singleton;

public class HungryMode {
    private HungryMode() {

    }

    private static HungryMode hungryMode = new HungryMode();

    public static HungryMode getHungryMode() {
        return hungryMode;
    }
}
