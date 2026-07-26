/// This file handles the Tomcat/Apache webserver
/// It creates the endpoints for API's
/// It also creates webSocket
import java.io.File;
import java.util.logging.Logger;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;

import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;

public class ServletMain {
    private static final Logger LOGGER = Logger.getLogger(ServletMain.class.getName());
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8080);
        tomcat.setConnector(connector);

        /// Create a File Handler pointing to the ./Frontend folder and convert to absolute path
        String webappDir = new File("Frontend").getAbsolutePath();
        Context ctx = tomcat.addWebapp("/", webappDir);

        /// Add servlet container starter for websocket
        ctx.addServletContainerInitializer(new WsSci(), null);

        /// Add servlet to serve an UI for http://localhost/chat
        Tomcat.addServlet(ctx, "ChatUI", "ChatUI");
        ctx.addServletMappingDecoded("/chat/*", "ChatUI");

        /// Add servlet ChatServlet API for POST Requests at http://localhost/chat/api
        Tomcat.addServlet(ctx, "ChatAPI", "ChatAPI");
        ctx.addServletMappingDecoded("/chat/api", "ChatAPI");

        tomcat.start();

        /// Start WebSocket
        ServerContainer wsContainer = (ServerContainer) ctx.getServletContext().getAttribute(ServerContainer.class.getName()); /// Create a container at context
        wsContainer.addEndpoint(ServerEndpointConfig.Builder.create(ChatWebSocket.class, "/chat").build()); /// Add endpoint to that container

        LOGGER.info("Tomcat server started on port 8080");
        tomcat.getServer().await();
    }
}
