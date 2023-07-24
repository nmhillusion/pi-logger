package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * date: 2023-07-24
 * <p>
 * created-by: nmhillusion
 */

public class ExceptionTest {
    
    void causeOfTheProblem() {
        throw new StackOverflowError("Value of column is too large");
    }
    
    void callToCauseFunc() {
        causeOfTheProblem();
    }
    
    void throwExceptionFunc() throws SQLException {
        throw new SQLException("Invalid column name");
    }
    
    @Test
    void testWithException() {
        try {
            throwExceptionFunc();
        } catch (Throwable ex) {
            PiLoggerFactory.getLog(this).error(ex);
        }
    }
    
    @Test
    void testWithChainFunctionCause() {
        try {
            callToCauseFunc();
        } catch (Throwable ex) {
            PiLoggerFactory.getLog(this).error(ex);
        }
    }
}
