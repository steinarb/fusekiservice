package no.priv.bang.fuseki.fusekiservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.jena.atlas.logging.Log;
import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.jetty.JettyFuseki;
import org.apache.jena.fuseki.jetty.JettyServerConfig;
import org.apache.jena.fuseki.server.FusekiEnv;
import org.apache.jena.system.JenaSystem;

import com.alexkasko.installer.DaemonLauncher;

public class FusekiService implements DaemonLauncher {
    private String propertiesFilename = "jenafusekiservice.properties";

    public void startDaemon() {
        JenaSystem.init();
        FusekiEnv.setEnvironment();
        Fuseki.init();
        Log.info(getClass(), "FUSEKI_HOME: " + FusekiEnv.FUSEKI_HOME);
        Properties properties = findAndReadProperties();
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
        jettyConfig.port = Integer.parseInt(properties.getProperty("port", "4000"));
        jettyConfig.contextPath = properties.getProperty("contextpath", "/");
        jettyConfig.jettyConfigFile = null;
        jettyConfig.pages = Fuseki.PagesStatic;
        jettyConfig.enableCompression = true;
        return jettyConfig;
    }

}
