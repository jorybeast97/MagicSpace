package CAS;

import lock.MagnoliaLock;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 通过Unsafe类来实现自己的CAS操作
 */
public class CompareAndSweepUtils {

    public static final Unsafe unsafe = CompareAndSweepUtils.reflectGetUnsafe();

    /**
     * 用于储存锁状态state的偏移量(内存地址)
     */
    private static long stateOffSet ;

    /**
     * 通过CAS的方式来修改锁的状态值,防止并发问题
     */
    public final boolean compareAndSweepState(int exceptOldNum, int update , Object object) {
        return unsafe.compareAndSwapInt(object, stateOffSet, exceptOldNum, update);
    }

    /**
     * 通过反射的方式获取到Unsafe类
     * @return
     */
    public static Unsafe reflectGetUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对偏移量进行初始值设置
     */
    static {
        try {
            stateOffSet = unsafe.objectFieldOffset(MagnoliaLock.class.getDeclaredField("state"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
