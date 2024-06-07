package control;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class SecurityHeadersFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        // Inizializzazione se necessaria
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Aggiungi l'header X-Frame-Options
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // Aggiungi l'header Content-Security-Policy con la direttiva frame-ancestors
        httpResponse.setHeader("Content-Security-Policy", "frame-ancestors 'none'");
        
        chain.doFilter(request, response);
    }

    public void destroy() {
        // Cleanup se necessaria
    }
}
