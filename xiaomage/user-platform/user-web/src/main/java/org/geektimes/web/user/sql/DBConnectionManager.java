package org.geektimes.web.user.sql;

import org.apache.commons.lang.ClassUtils;
import org.geektimes.web.user.function.ThrowableFunction;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnectionManager {

    private static final Logger logger = Logger.getLogger(DBConnectionManager.class.getName());

    static Map<Class<?>, String> preparedStatementMethodMappings = new HashMap<>();

    static {
        preparedStatementMethodMappings.put(Long.class, "setLong");
        preparedStatementMethodMappings.put(String.class, "setString");
        preparedStatementMethodMappings.put(Integer.class, "setInt");
        preparedStatementMethodMappings.put(Boolean.class, "setBoolean");
        preparedStatementMethodMappings.put(Byte.class, "setByte");
        preparedStatementMethodMappings.put(Short.class, "setShort");
        preparedStatementMethodMappings.put(Float.class, "setFloat");
        preparedStatementMethodMappings.put(Double.class, "setDouble");
    }

    private DataSource dataSource;

    public DBConnectionManager() {
        try {
            Context initCtx = new InitialContext();
            this.dataSource = (DataSource) initCtx.lookup("java:comp/env/jdbc/UserPlatformDB");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> function,
                                 Consumer<Throwable> exceptionHandler, Object... args) {
        try {
            PreparedStatement preparedStatement = handlerArgs(getConnection(), sql, args);
            ResultSet resultSet = preparedStatement.executeQuery();
            return function.apply(resultSet);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return null;
    }

    public <T> Integer execute(String sql, Consumer<Throwable> exceptionHandler, Object... args) {
        try {
            PreparedStatement preparedStatement = handlerArgs(getConnection(), sql, args);
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            exceptionHandler.accept(e);
        }
        return null;
    }

    private PreparedStatement handlerArgs(Connection connection, String sql, Object... args) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Class<?> argType = arg.getClass();
            Class<?> wrapperType = ClassUtils.wrapperToPrimitive(argType);
            if (wrapperType == null) {
                wrapperType = argType;
            }
            String methodName = preparedStatementMethodMappings.get(wrapperType);
            Method method = PreparedStatement.class.getMethod(methodName, int.class, wrapperType);
            method.invoke(preparedStatement, i + 1, arg);
        }
        return preparedStatement;
    }

}