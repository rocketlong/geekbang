package org.geektimes.configuration.microprofile.config.source.def;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Map;

public class JavaSystemPropertiesConfigSource extends MapBasedConfigSource {

    public JavaSystemPropertiesConfigSource() {
        super("Java System Properties", 400, null);
    }

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行时不去调整
     * -Dapplication.name=user-web
     */
    @Override
    protected void prepareConfigData(Map configData, ServletContext servletContext) throws Throwable {
        configData.putAll(System.getProperties());
    }

}
