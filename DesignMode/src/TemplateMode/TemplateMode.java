package TemplateMode;

public abstract class TemplateMode {

    abstract void start();

    abstract void run();

    abstract void stop();

    //模板方法
    public void templateMethod() {
        start();
        run();
        stop();
    }
}
