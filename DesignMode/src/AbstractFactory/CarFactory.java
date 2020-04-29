package AbstractFactory;

public class CarFactory implements AbstractFactory {

    @Override
    public Car getCar(String car) {
        if (car.equals("Acar")) {
            return new Acar();
        }
        return null;
    }

    @Override
    public House getHouse(String house) {
        return null;
    }
}
