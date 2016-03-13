package no.priv.bang.fuseki.fusekiservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.jetty.JettyFuseki;
import org.apache.jena.fuseki.jetty.JettyServerConfig;
import org.apache.jena.fuseki.server.FusekiEnv;
import org.apache.jena.system.JenaSystem;

import com.alexkasko.installer.DaemonLauncher;

public class FusekiService implements DaemonLauncher {
    private Properties properties;

    public FusekiService() {
        super();
        JenaSystem.init();
        properties = findAndReadProperties();
        String fusekiDataLocation = properties.getProperty("jena.fuseki.datalocation");
        if (fusekiDataLocation != null) {
            FusekiEnv.FUSEKI_BASE = Paths.get(fusekiDataLocation);
        }
        FusekiEnv.mode = FusekiEnv.INIT.STANDALONE;
        FusekiEnv.setEnvironment();
        Fuseki.init();
    }

    private String propertiesFilename = "jenafusekiservice.properties";

    public void startDaemon() {
        JettyServerConfig jettyConfig = createJettyConfig(properties);
        JettyFuseki.initializeServer(jettyConfig);
        JettyFuseki.instance.start();
    }

    public void stopDaemon() {
        JettyFuseki.instance.stop();
    }

    private Properties findAndReadProperties() {
        Properties properties = new Properties();
        InputStream propertiesInputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
        try {
            if (propertiesInputStream != null) {
                properties.load(propertiesInputStream);
            } else {
                throw new FileNotFoundException("Property file " + propertiesFilename + " not found in the classpath.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException reading " + propertiesFilename, e);
        }
        return properties;
    }

    private JettyServerConfig createJettyConfig(Properties properties) {
        JettyServerConfig jettyConfig = new JettyServerConfig();
        jettyConfig.port = Integer.parseInt(properties.getProperty("jena.fuseki.port", "3030"));
        jettyConfig.contextPath = properties.getProperty("jena.fuseki.contextpath", "/");
        jettyConfig.jettyConfigFile = null;
        jettyConfig.pages = Fuseki.PagesStatic;
        jettyConfig.enableCompression = true;
        return jettyConfig;
    }

}
