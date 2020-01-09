package subscriptQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.RabbitMQUtils;

public class SubscriptQueueProducer {

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

        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        //发送消息
        String msg = "这是一条来自交换机的消息。";

        channel.basicPublish(EXCHANGE_NAME,"" , null, msg.getBytes());

//        channel.close();
//        connection.close();

    }
}
