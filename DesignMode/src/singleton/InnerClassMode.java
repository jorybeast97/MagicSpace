package singleton;

//内部类模式
public class InnerClassMode {

    private static class single{
        private static final InnerClassMode inner = new InnerClassMode();
    }

    public final InnerClassMode getInstence() {
        return single.inner;

    }

    private InnerClassMode() {

    }
}
