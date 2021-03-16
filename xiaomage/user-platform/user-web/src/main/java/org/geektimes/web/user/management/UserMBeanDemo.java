package org.geektimes.web.user.management;

import org.geektimes.web.user.domain.User;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class UserMBeanDemo {

    public static void main(String[] args) throws Exception {
        // 获取平台 MBean Server
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        // 为 UserMBean 定义ObjectName
        ObjectName objectName = new ObjectName("org.geektimes.web.user.management:type=User");
        // 注册
        User user = new User();
        mBeanServer.registerMBean(new UserManager(user), objectName);
        while (true) {
            Thread.sleep(2000);
            System.out.println(user);
        }
    }

}
