package fullGC;

import javax.management.*;

public class JMXExample {

    public void create() throws InterruptedException {
        while (true) {
            byte[] bytes = new byte[1024 * 1024 * 15];
            Thread.sleep(100);
        }
    }

    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, InterruptedException {
        new Thread(new JMXMonitor()).start();
        new JMXExample().create();

    }
}
