package fullGC;

public class HeapObserver {

    long fullGCNum = 0;

    long lastDumpTime = 0;

    public void checkDump(long num, long curTime ,long interval) {
        if (num > fullGCNum && (curTime - lastDumpTime) > interval) {
            System.out.println("生成Heap Dump");
            setFullGCNum(num);
            setLastDumpTime(curTime);
            return;
        } else if (num > fullGCNum) {
            System.out.println("进行了一次full GC,程序共计完成了"+num+"次FULL GC");
            setFullGCNum(num);
        } else {
            System.out.println("程序正常运行");
        }
    }

    public void setFullGCNum(long fullGCNum) {
        this.fullGCNum = fullGCNum;
    }

    public void setLastDumpTime(long lastDumpTime) {
        this.lastDumpTime = lastDumpTime;
    }
}
