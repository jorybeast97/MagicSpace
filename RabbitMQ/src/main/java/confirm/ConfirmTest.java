package confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class ConfirmTest {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            createMsg();
            System.out.println("发送完成");
        }
    }

    //声明交换机的名称
    public static final String EXCHANGE_NAME = "SUBSCRIPT_QUEUE";

    public static void createMsg() throws Exception {
        //同之前
        Connection connection = RabbitMQUtils.
                getRabbitMQConnection("localhost", 5672,
                        "/magnolia", "guest", "guest");
        Channel channel = connection.createChannel();

        //未确认的消息集合
        final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());

        channel.addConfirmListener(new ConfirmListener() {
            //没有问题
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    confirmSet.headSet(deliveryTag + 1).clear();
                }else {
                    confirmSet.remove(deliveryTag);
                }
            }
            //消息没有确认
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    confirmSet.headSet(deliveryTag + 1).clear();
                }else {
                    confirmSet.remove(deliveryTag);
                }
            }
        });

    }
}
