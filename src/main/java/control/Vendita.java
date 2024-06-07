package control;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import model.ProductBean;
import model.ProductModel;

/**
 * Servlet implementation class Vendita
 */
@WebServlet("/Vendita")
public class Vendita extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Vendita() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductBean product = new ProductBean();
        product.setEmail((String) request.getSession().getAttribute("email"));

        String UPLOAD_DIRECTORY = request.getServletContext().getRealPath("/") + "img/productIMG/";
        // Process only if it's multipart content
        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(new ServletRequestContext(request));

                for (FileItem item : multiparts) {
                    if (!item.isFormField()) {
                        String name = new File(item.getName()).getName();
                        item.write(new File(UPLOAD_DIRECTORY + File.separator + name));
                        product.setImmagine(name);
                    } else {
                        String sanitizedInput = sanitizeInput(item.getString());
                        if (item.getFieldName().compareTo("nome") == 0) {
                            product.setNome(sanitizedInput);
                        } else if (item.getFieldName().compareTo("prezzo") == 0) {
                            product.setPrezzo(Double.parseDouble(sanitizedInput));
                        } else if (item.getFieldName().compareTo("spedizione") == 0) {
                            product.setSpedizione(Double.parseDouble(sanitizedInput));
                        } else if (item.getFieldName().compareTo("tipologia") == 0) {
                            product.setTipologia(sanitizedInput);
                        } else if (item.getFieldName().compareTo("tag") == 0) {
                            product.setTag(sanitizedInput);
                        } else if (item.getFieldName().compareTo("descrizione") == 0) {
                            product.setDescrizione(sanitizedInput);
                        }
                    }
                }

                // File uploaded successfully
                request.setAttribute("message", "File Uploaded Successfully");

            } catch (Exception ex) {
                request.setAttribute("message", "File Upload Failed due to " + ex);
            }

        } else {
            request.setAttribute("message", "Sorry this Servlet only handles file upload request");
        }

        ProductModel model = new ProductModel();
        try {
            model.doSave(product);
        } catch (SQLException e) {
            // Log the error instead of printing stack trace
            e.printStackTrace();
        }
        request.getSession().setAttribute("refreshProduct", true);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

    /**
     * Sanitize input to prevent XSS.
     * 
     * @param input the input string to be sanitized
     * @return the sanitized input
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Replace all <, >, ", ', &, /, and \ characters with their HTML entities
        return input.replaceAll("&", "&amp;")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("'", "&#x27;")
                    .replaceAll("/", "&#x2F;")
                    .replaceAll("\\\\", "&#x5C;");
    }
}
