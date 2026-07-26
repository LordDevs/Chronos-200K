package com.chatbot;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet
public class ChatUI extends HttpServlet{

    /// GET Method
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
    
        /// Resolve path info
        if (pathInfo == null || pathInfo.equals("/")) {
            serveFile(response, "index.html");
        } else {
            String fileName = pathInfo.substring(1);
            serveFile(response, fileName);
        }
    }

    /// Serve file method
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

    // Method to read static file content from Frontend/
    private String getStaticFileContent(String fileName) throws IOException {
        ServletContext context = getServletContext();
        String realPath = context.getRealPath("/Frontend/" + fileName);

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

    // Method to determine content type based on file extension
    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "text/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else {
            return "application/octet-stream"; // Default for unknown types
        }
    }
}

