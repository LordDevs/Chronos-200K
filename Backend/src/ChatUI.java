import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Serves static UI under /chat/* (optional path; Tomcat also serves Frontend/ at /). */
public class ChatUI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            serveFile(response, "index.html");
        } else {
            serveFile(response, pathInfo.substring(1));
        }
    }

    private void serveFile(HttpServletResponse response, String fileName) throws IOException {
        String content = getStaticFileContent(fileName);
        if (content != null) {
            response.setContentType(getContentType(fileName));
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(content);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, fileName + " not found");
        }
    }

    private String getStaticFileContent(String fileName) throws IOException {
        // Prefer files from the webapp root (Frontend mapped at /)
        ServletContext context = getServletContext();
        String realPath = context.getRealPath("/" + fileName);
        if (realPath == null) {
            realPath = context.getRealPath("/Frontend/" + fileName);
        }
        if (realPath == null) {
            return null;
        }

        File file = new File(realPath);
        if (file.exists() && file.isFile()) {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        }
        return null;
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream";
    }
}
