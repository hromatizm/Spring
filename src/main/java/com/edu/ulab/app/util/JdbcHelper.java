package com.edu.ulab.app.util;

import com.edu.ulab.app.exception.JDBCConnectionException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * For use with JDBC
 */
@Slf4j
@UtilityClass
public class JdbcHelper {

    public void activateDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException exc) {
            log.info(exc.getMessage());
        }
    }

    public Connection activateConnection() {
        try {
            return DriverManager.getConnection("jdbc:h2:mem:userbook", "test", "test");
        } catch (SQLException exc) {
            handleSqlException(exc);
        }
        throw new JDBCConnectionException("Can't establish database connection");
    }

    public void handleSqlException(SQLException exc){
        while(exc != null) {
            log.info("SQLException message: {}", exc.getMessage());
            log.info("Error code: {}", exc.getErrorCode());
            log.info("SQL state: {}", exc.getSQLState());
            exc = exc.getNextException();
        }
    }

    public void closeResource(AutoCloseable ac) {
        try {
            if (ac != null) {
                ac.close();
            }
        } catch (Exception exc) {
            log.info(exc.getMessage());
        }
        log.info("Resource is closed: {}", ac);
    }

    public void closeAll(AutoCloseable ac1, AutoCloseable ac2) {
        closeResource(ac1);
        closeResource(ac2);
    }
    public void closeAll(AutoCloseable ac1, AutoCloseable ac2, AutoCloseable ac3) {
        closeResource(ac1);
        closeResource(ac2);
        closeResource(ac3);
    }
}
