package org.geektimes.dependency.listener;

import org.geektimes.dependency.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ComponentContextInitializerListener implements ServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ComponentContext context = new ComponentContext();
        context.init(servletContext);
//        registerMBean();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext context = ComponentContext.getInstance();
        context.destroy();
    }

    /**
     * http://localhost:8080/jolokia/list -- 获取列表
     * http://localhost:8080/jolokia/read/org.geektimes.web.user.management:type=User/Name -- 访问属性
     * http://localhost:8080/jolokia/write/org.geektimes.web.user.management:type=User/Name/testName -- 修改属性
     * http://localhost:8080/jolokia/exec/org.geektimes.web.user.management:type=User/toString -- 调用操作方法
     */
//    private void registerMBean() {
//        try {
//            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
//            // 为 UserMBean 定义ObjectName
//            ObjectName objectName = new ObjectName("org.geektimes.web.user.management:type=User");
//            // 注册
//            mBeanServer.registerMBean(new UserManager(new User()), objectName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
