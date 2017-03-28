package stockTicker;

import java.io.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"/stream"})
public final class PriceUpdate extends HttpServlet {

    final Stock stock = new Stock();

    @Override
    public void init(final ServletConfig config) {
        stock.start();
        Logger.getGlobal().log(Level.INFO, "Started stock price updates");
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/event-stream");

        try (final PrintWriter out = response.getWriter()) {

            while (!Thread.interrupted()) {
                synchronized (stock) {
                    stock.wait();
                }
                
                out.print("data: ");
                out.println(stock);
                out.println();
                out.flush();
            }
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            stock.interrupt();
            stock.join();
            Logger.getGlobal().log(Level.INFO, "Stopped stock price updates");

        } catch (InterruptedException e) {
        }
    }
    
   
}
