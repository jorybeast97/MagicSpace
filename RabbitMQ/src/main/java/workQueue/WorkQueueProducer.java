package workQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import sun.reflect.misc.ConstructorUtil;
import utils.RabbitMQUtils;

import java.io.IOException;

/**
 * @author fanhao
 * 工作队列消息发送者
 */
public class WorkQueueProducer {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            createMsg(i);
            System.out.println("第"+i+"消息发送完成");
            Thread.sleep(500);
        }
    }


    public static final String QUEUE_NAME = "MSG_PRODUCER";

    public static void createMsg(Integer temp) throws Exception {
        Connection connection = RabbitMQUtils.
                getRabbitMQConnection("localhost", 5672,
                        "/magnolia", "guest", "guest");
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //确认响应前只发送一个消息
        channel.basicQos(1);

        String msg = "消息生产者MSG_PRDUCER第" + temp + "次发送消息";

        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());

        channel.close();
        connection.close();

    }

}
