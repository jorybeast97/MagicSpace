package DecoratorMode;

public class CarImp implements Car {
    @Override
    public void run() {
        System.out.println("普通轿车行驶");
    }

    @Override
    public void stop() {
        System.out.println("普通轿车刹车");
    }
}
