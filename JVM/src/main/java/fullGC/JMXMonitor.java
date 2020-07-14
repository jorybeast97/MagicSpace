package fullGC;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * 死循环扫描GC
 */
public class JMXMonitor implements Runnable {

    public void run() {
        try {
            this.monitor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    GarbageCollectorMXBean garbageCollectorMXBean = null;
    String oldCollectorName = "PS Scavenge";
    Integer scanTime = 1;
    HeapObserver observer = new HeapObserver();

    public void monitor() throws Exception {
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (bean.getName().equals(oldCollectorName)) garbageCollectorMXBean = bean;
        }
        if (garbageCollectorMXBean == null) throw new Exception(oldCollectorName + "不存在");
        while (true) {
            long curTime = System.currentTimeMillis();
            long curFullGCNum = garbageCollectorMXBean.getCollectionCount();
            observer.checkDump(curFullGCNum, curTime, 300000);
            Thread.sleep(scanTime * 1000);
        }
    }


}
