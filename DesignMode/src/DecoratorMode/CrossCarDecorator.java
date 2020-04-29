package DecoratorMode;

public class CrossCarDecorator extends CarDecorator {
    public CrossCarDecorator(Car car) {
        super(car);
    }

    @Override
    public void run() {
        super.run();
        System.out.println("开启低速四驱模式");
    }

    @Override
    public void stop() {
        super.stop();
        System.out.println("开启陡坡缓降模式");
    }



}
