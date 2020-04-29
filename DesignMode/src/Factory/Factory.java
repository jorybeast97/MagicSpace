package Factory;

/**
 * 一般工厂模式,通过工厂来创建对象
 */
public class Factory {

    private static volatile Factory factory = null;

    /**
     * 工厂一般都是通过单例来创建
     * @return
     */
    public static Factory getFactory() {
        if (factory == null) {
            synchronized (Factory.class) {
                if (factory == null) factory = new Factory();
            }
        }
        return factory;
    }

    /**
     * 通过名字来创建一个对象
     * @param className
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public CommonImp createObj(String className) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        Class commonImpClass = Class.forName(className);
        CommonImp result = (CommonImp) commonImpClass.newInstance();
        return result;
    }

}
