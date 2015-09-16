/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.mtlab.listeners;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 *
 * @author Milan
 */
public class AppListener implements ServletContextListener {

    private static String databaseDirectory;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        databaseDirectory = servletContext.getRealPath("/WEB-INF") + File.separator;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public static String getDatabaseDirectory() {
        return databaseDirectory;
    }
    
    
}
