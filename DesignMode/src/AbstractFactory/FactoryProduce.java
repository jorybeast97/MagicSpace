package AbstractFactory;

public class FactoryProduce {
    /**
     * 通过工厂创造器来创造工厂
     * @param factoryName
     * @return
     */
    public AbstractFactory createFactory(String factoryName) {
        AbstractFactory abstractFactory = null;
        if (factoryName.equals("CarFactory")) {
            abstractFactory = new CarFactory();
        }
        if (factoryName.equals("HouseFactory")) {
            abstractFactory = new HouseFactory();
        }
        return abstractFactory;
    }
}
