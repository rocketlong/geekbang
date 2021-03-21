package org.geektimes.configuration.microprofile.config.source.def;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Map;

public class OperationSystemEnvironmentVariablesConfigSource extends MapBasedConfigSource {

    public OperationSystemEnvironmentVariablesConfigSource() {
        super("Operation System Environment Variables", 300, null);
    }

    @Override
    protected void prepareConfigData(Map configData, ServletContext servletContext) throws Throwable {
        configData.putAll(System.getenv());
    }
}
