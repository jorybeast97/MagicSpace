import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;

public class BloomTest {

    public static int total = 2000000;
    public static BloomFilter<Integer> filter = BloomFilter.create(Funnels.integerFunnel(), total,0.000000001);

    public static void main(String[] args) {
        //添加200w条数据
        for (int i = 0; i < total; i++) {
            filter.put(i);
        }
        //查看已经在filter中的值是否漏掉
        int temp = 0;
        for (int i = 0; i < total; i++) {
            if (!filter.mightContain(i)) temp++;
        }
        System.out.println("匹配失败的条目 : " + temp);
        //测试10w条不在filter中的数据，看是否有误判
        int count = 0;
        for (int i = total; i < total + 100000; i++) {
            if (filter.mightContain(i)) count++;
        }
        System.out.println("出现误判的条数 : " + count);
    }
}
