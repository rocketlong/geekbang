package org.geektimes.web.user.repository;

import org.geektimes.web.user.domain.User;
import org.geektimes.web.user.repository.sql.DBConnectionManager;

import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUserRepository implements UserRepository {

    private static final Logger logger = Logger.getLogger(DatabaseUserRepository.class.getName());

    static Map<Class<?>, String> resultSetMethodMappings = new HashMap<>();

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(String.class, "getString");
        resultSetMethodMappings.put(Integer.class, "getInt");
        resultSetMethodMappings.put(Boolean.class, "getBoolean");
        resultSetMethodMappings.put(Byte.class, "getByte");
        resultSetMethodMappings.put(Short.class, "getShort");
        resultSetMethodMappings.put(Float.class, "getFloat");
        resultSetMethodMappings.put(Double.class, "getDouble");
    }

    @Resource(name = "bean/DBConnectionManager")
    private DBConnectionManager dbConnectionManager;

    private static final Consumer<Throwable> COMMON_EXCEPTION_HANDLER = e -> logger.log(Level.SEVERE, e.getMessage());

    public static final String INSERT_USER_DML_SQL = "INSERT INTO users(name,password,email,phoneNumber) VALUES (?,?,?,?)";

    public static final String UPDATE_USER_DML_SQL = "UPDATE users SET name=?,password=?,email=?,phoneNumber=? WHERE id=?";

    public static final String DELETE_USER_DML_SQL = "DELETE FROM users WHERE id=?";

    public static final String SELECT_USER_BY_ID_DML_SQL = "SELECT id,name,password,email,phoneNumber FROM users WHERE id=?";

    public static final String SELECT_USER_BY_CONDITION_DML_SQL = "SELECT id,name,password,email,phoneNumber FROM users WHERE name=? and password=?";

    public static final String SELECT_USER_BY_EMAIL = "SELECT id,name,password,email,phoneNumber FROM users WHERE email=?";

    public static final String SELECT_USER_ALL = "SELECT id,name,password,email,phoneNumber FROM users";

    @Override
    public int save(User user) {
        return dbConnectionManager.execute(INSERT_USER_DML_SQL, COMMON_EXCEPTION_HANDLER,
                user.getName(), user.getPassword(), user.getEmail(), user.getPhoneNumber());
    }

    @Override
    public int deleteById(Long userId) {
        return dbConnectionManager.execute(DELETE_USER_DML_SQL, COMMON_EXCEPTION_HANDLER, userId);
    }

    @Override
    public int update(User user) {
        return dbConnectionManager.execute(UPDATE_USER_DML_SQL, COMMON_EXCEPTION_HANDLER,
                user.getName(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getId());
    }

    @Override
    public User getById(Long userId) {
        return dbConnectionManager.executeQuery(SELECT_USER_BY_ID_DML_SQL, this::handlerResultSet, COMMON_EXCEPTION_HANDLER, userId);
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return dbConnectionManager.executeQuery(SELECT_USER_BY_CONDITION_DML_SQL, this::handlerResultSet, COMMON_EXCEPTION_HANDLER, userName, password);
    }

    @Override
    public Collection<User> getAll() {
        return dbConnectionManager.executeQuery(SELECT_USER_ALL, this::handlerResultSetList, COMMON_EXCEPTION_HANDLER);
    }

    @Override
    public User getByEmail(String email) {
        return dbConnectionManager.executeQuery(SELECT_USER_BY_EMAIL, this::handlerResultSet, COMMON_EXCEPTION_HANDLER, email);
    }

    private User handlerResultSet(ResultSet resultSet) throws Exception {
        User user = new User();
        if (resultSet.next()) {
            BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
            for (PropertyDescriptor propertyDescriptor : userBeanInfo.getPropertyDescriptors()) {
                String fileName = propertyDescriptor.getName();
                Class<?> fileType = propertyDescriptor.getPropertyType();
                Method resultSetMethod = ResultSet.class.getMethod(resultSetMethodMappings.get(fileType), String.class);
                Object resultValue = resultSetMethod.invoke(resultSet, fileName);
                Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
                setterMethodFromUser.invoke(user, resultValue);
            }
        }
        return user;
    }

    private List<User> handlerResultSetList(ResultSet resultSet) throws Exception {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            users.add(handlerResultSet(resultSet));
        }
        return users;
    }

}
