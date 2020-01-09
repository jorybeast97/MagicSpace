package simpleQueue;

import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Date;

public class RabbitMQConsumer {

    public static void main(String[] args) throws IOException {
        receiveMessage();
    }

    public final static String QUEUE_NAME = "MyProductQueue";


    public static void receiveMessage() throws IOException {
        //获取连接
        Connection connection = RabbitMQUtils.
                getRabbitMQConnection("localhost", 5672,
                        "/magnolia", "guest", "guest");

        Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //事件模型
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("接收端收到信息 : "+msg + " " + new Date());
            }
        };
        //监听队列
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);

    }

}
