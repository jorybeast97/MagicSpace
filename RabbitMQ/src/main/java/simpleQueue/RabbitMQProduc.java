package simpleQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.RabbitMQUtils;

public class RabbitMQProduc {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            prodceMessage();
            Thread.sleep(3000);
        }
    }

    /**
     * 队列名称
     */
    public final static String QUEUE_NAME = "MyProductQueue";

    public static void prodceMessage() throws Exception {
        //获取连接
        Connection connection = RabbitMQUtils.
                getRabbitMQConnection("localhost", 5672,
                        "/magnolia", "guest", "guest");

        //获取通道
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //发送消息
        String msg = "HELLO WORLD";
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());

        System.out.println("消息已经发送!");

        //关闭
        channel.close();
        connection.close();

    }
}
