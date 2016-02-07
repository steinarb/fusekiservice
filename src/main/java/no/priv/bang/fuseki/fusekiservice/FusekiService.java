package no.priv.bang.fuseki.fusekiservice;

import org.apache.jena.atlas.logging.Log;
import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.jetty.JettyFuseki;
import org.apache.jena.fuseki.jetty.JettyServerConfig;
import org.apache.jena.fuseki.server.FusekiEnv;
import org.apache.jena.system.JenaSystem;

import com.alexkasko.installer.DaemonLauncher;

public class FusekiService implements DaemonLauncher {

    public void startDaemon() {
        JenaSystem.init();
        FusekiEnv.setEnvironment();
        Fuseki.init();
        Log.info(getClass(), "FUSEKI_HOME: " + FusekiEnv.FUSEKI_HOME);
        JettyServerConfig jettyConfig = new JettyServerConfig();
        jettyConfig.port = 4000;
        jettyConfig.contextPath = "/";
        jettyConfig.jettyConfigFile = null;
        jettyConfig.pages = Fuseki.PagesStatic;
        jettyConfig.enableCompression = true;
        JettyFuseki.initializeServer(jettyConfig);
        JettyFuseki.instance.start();
    }

    public void stopDaemon() {
        JettyFuseki.instance.stop();
    }

}
