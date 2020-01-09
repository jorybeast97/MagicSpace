package utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工具类，类似JDBC提供MQ的连接
 */
public class RabbitMQUtils {

    public static Connection getRabbitMQConnection
            (String host , Integer port , String virtualHost , String username , String password) {
        //定义一个工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置连接参数
        connectionFactory.setHost(host);
        //设置连接方式的端口，例如amqp对应5672
        connectionFactory.setPort(port);
        //设置Vhost
        connectionFactory.setVirtualHost(virtualHost);
        //设置用户名和密码
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        Connection connection = null;
        //获取连接
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
