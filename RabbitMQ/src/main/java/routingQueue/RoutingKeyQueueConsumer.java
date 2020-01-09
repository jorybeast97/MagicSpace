package routingQueue;

import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Date;

public class RoutingKeyQueueConsumer {

    public static void main(String[] args) throws Exception {
        getMsg();
    }

    private static final String QUEUE_NAME = "ROUTINGKEY_QUEUE";

    public static void getMsg() throws Exception {
        //同之前
        Connection connection = RabbitMQUtils.
                getRabbitMQConnection("localhost", 5672,
                        "/magnolia", "guest", "guest");
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //将队列绑定在交换机上
        channel.queueBind(QUEUE_NAME, RoutingKeyQueueProducer.EXCHANGE_NAME, "routingKey");

        //事件模型
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println(" 1接收端收到信息: "+msg + " " + new Date());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        //监听队列
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);



    }
}
