package DecoratorMode;

/**
 * 越野装饰器
 */
public abstract class CarDecorator implements Car {

    protected Car car;

    public CarDecorator(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        car.run();
    }

    @Override
    public void stop() {
        car.stop();
    }
}
