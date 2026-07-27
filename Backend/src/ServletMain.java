/// Embedded Tomcat entrypoint for Chronos-200K
import java.io.File;
import java.util.logging.Logger;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;

import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;

public class ServletMain {
    private static final Logger LOGGER = Logger.getLogger(ServletMain.class.getName());

    public static void main(String[] args) throws Exception {
        File baseDir = new File("tomcat-work");
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            LOGGER.warning("Could not create tomcat-work directory");
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8080);
        tomcat.setConnector(connector);

        // addContext + DefaultServlet: static Frontend without pulling in JSP (jasper not on classpath)
        String webappDir = new File("Frontend").getAbsolutePath();
        Context ctx = tomcat.addContext("", webappDir);
        ctx.addWelcomeFile("index.html");

        Wrapper defaultServlet = ctx.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.setLoadOnStartup(1);
        defaultServlet.addInitParameter("listings", "false");
        ctx.addChild(defaultServlet);
        ctx.addServletMappingDecoded("/", "default");
        ctx.addServletMappingDecoded("*.css", "default");
        ctx.addServletMappingDecoded("*.js", "default");
        ctx.addServletMappingDecoded("*.png", "default");
        ctx.addServletMappingDecoded("*.html", "default");
        ctx.addServletMappingDecoded("*.ico", "default");

        ctx.addServletContainerInitializer(new WsSci(), null);

        Tomcat.addServlet(ctx, "ChatAPI", "ChatAPI");
        ctx.addServletMappingDecoded("/api", "ChatAPI");
        ctx.addServletMappingDecoded("/chat/api", "ChatAPI");

        Tomcat.addServlet(ctx, "ColoniesAPI", "ColoniesAPI");
        ctx.addServletMappingDecoded("/api/colonies", "ColoniesAPI");

        tomcat.start();

        ServerContainer wsContainer = (ServerContainer) ctx.getServletContext()
                .getAttribute(ServerContainer.class.getName());
        if (wsContainer != null) {
            wsContainer.addEndpoint(
                    ServerEndpointConfig.Builder.create(ChatWebSocket.class, "/ws").build());
        } else {
            LOGGER.warning("WebSocket container unavailable; HTTP /api still works");
        }

        LOGGER.info("CHRONOS Tomcat ready — http://127.0.0.1:8080/");
        tomcat.getServer().await();
    }
}
