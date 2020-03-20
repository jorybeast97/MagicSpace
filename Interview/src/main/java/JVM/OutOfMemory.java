package JVM;

import java.util.ArrayList;

public class OutOfMemory {

    public static void main(String[] args) {
        new OutOfMemory().stackOverFlow();

    }

    public void heapOutOfMemory() {
        ArrayList<Object> list = new ArrayList<>();
        while (true) {
            list.add(new int[1024]);
        }
    }


    /**
     * 无限递归会导致栈溢出
     */
    public void stackOverFlow() {
        stackOverFlow();
    }
}
