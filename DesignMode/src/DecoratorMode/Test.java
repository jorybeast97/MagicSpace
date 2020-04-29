package DecoratorMode;

public class Test {
    public static void main(String[] args) {
        CarImp carImp = new CarImp();
        carImp.run();
        carImp.stop();

        CrossCarDecorator crossCarDecorator = new CrossCarDecorator(carImp);
        crossCarDecorator.run();
        crossCarDecorator.stop();
    }
}
