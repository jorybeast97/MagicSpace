package workQueue;

import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Date;

public class WorkQueueConsumer {

    public static void main(String[] args) throws IOException {
        receiveMessage();
    }


    public final static String QUEUE_NAME = "MSG_PRODUCER";


    public static void receiveMessage() throws IOException {
        //获取连接
        Connection connection = RabbitMQUtils.
                getRabbitMQConnection("localhost", 5672,
                        "/magnolia", "guest", "guest");

        final Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);

        //事件模型
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("接收端收到信息 : " + msg + "  " + new Date() + Thread.currentThread());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }

            }
        };
        //监听队列
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck , defaultConsumer);

    }
}
