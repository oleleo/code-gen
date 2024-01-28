package com.gitee.gen.util;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author thc
 */
public class BeanUtil {
    private static final Log logger = LogFactory.getLog(BeanUtil.class);

    private static final String PREFIX_GET = "get";
    private static final String PREFIX_SET = "set";
    private static final String PREFIX_IS = "is";
    private static final String GET_CLASS_NAME = "getClass";

    /**
     * 属性拷贝,第一个参数中的属性值拷贝到第二个参数中<br>
     * 注意:当第一个参数中的属性有null值时,不会拷贝进去
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        copyProperties(source, target, true);
    }

    /**
     * 属性拷贝,第一个参数中的属性值拷贝到第二个参数中<br>
     * 注意:当第一个参数中的属性有null值时,不会拷贝进去
     *
     * @param source          源对象
     * @param target          目标对象
     * @param ignoreNullValue 是否忽略null值
     */
    private static void copyPropertiesForMap(Map<String, Object> source, Object target, boolean ignoreNullValue) {
        Objects.requireNonNull(source, "Source must not be null");
        Objects.requireNonNull(target, "Target must not be null");
        Method[] targetMethods = target.getClass().getMethods();
        Map<String, MethodBean> setMethodMap = buildSetMethodMap(targetMethods);
        Set<String> keys = source.keySet();
        try {
            for (String column : keys) {
                Object sourceFieldValue = source.get(column);
                if (ignoreNullValue && sourceFieldValue == null) {
                    continue;
                }
                MethodBean methodBean = setMethodMap.get(column);
                // 如果为null，转成驼峰再获取一次
                if (methodBean == null) {
                    String fieldName = FieldUtil.underlineFilter(column);
                    methodBean = setMethodMap.get(fieldName);
                }
                if (methodBean == null) {
                    continue;
                }
                Class<?> paramType = methodBean.getParamType();
                Object targetVal = parseValue(sourceFieldValue, paramType);
                Method targetMethod = methodBean.getMethod();
                setValue(targetMethod, target, targetVal);
            }
        } catch (Exception e) {
            logger.error("copyPropertiesForMap error, source=" + source, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 属性拷贝,第一个参数中的属性值拷贝到第二个参数中<br>
     * 注意:当第一个参数中的属性有null值时,不会拷贝进去
     *
     * @param source          源对象
     * @param target          目标对象
     * @param ignoreNullValue 是否忽略null值
     */
    private static void copyProperties(Object source, Object target, boolean ignoreNullValue) {
        Objects.requireNonNull(source, "Source must not be null");
        Objects.requireNonNull(target, "Target must not be null");
        if (source instanceof Map) {
            copyPropertiesForMap((Map<String, Object>) source, target, ignoreNullValue);
            return;
        }
        Method[] sourceMethods = source.getClass().getMethods();
        Method[] targetMethods = target.getClass().getMethods();
        Map<String, MethodBean> setMethodMap = buildSetMethodMap(targetMethods);
        try {
            for (Method method : sourceMethods) {
                if (isGetMethod(method)) {
                    String methodName = method.getName();
                    Class<?> returnType = method.getReturnType();
                    String sourceFieldName = buildFieldName(methodName);
                    Object sourceFieldValue = method.invoke(source);
                    if (ignoreNullValue && sourceFieldValue == null) {
                        continue;
                    }
                    MethodBean methodBean = setMethodMap.get(sourceFieldName);
                    if (methodBean == null) {
                        continue;
                    }
                    Class<?> paramType = methodBean.getParamType();
                    Method targetMethod = methodBean.getMethod();
                    if (paramType == returnType) {
                        setValue(targetMethod, target, sourceFieldValue);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("copyProperties error", e);
        }
    }

    /**
     * 返回所有的setXX方法，key为对应的字段名称
     *
     * @param methods 方法列表
     * @return 返回setXx方法对应关系，key为对应的字段名称
     */
    private static Map<String, MethodBean> buildSetMethodMap(Method[] methods) {
        return Arrays.stream(methods)
                .filter(BeanUtil::isSetMethod)
                .collect(Collectors.toMap(method -> buildFieldName(method.getName()), method -> {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    return new MethodBean(method, parameterType);
                }));
    }

    private static void setValue(Method method, Object obj, Object value) throws InvocationTargetException, IllegalAccessException {
        method.invoke(obj, value);
    }

    /**
     * 属性拷贝,第一个参数中的属性值拷贝到第二个参数中<br>
     * 注意:当第一个参数中的属性有null值时,不会拷贝进去
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        Objects.requireNonNull(source, "Source must not be null");
        Objects.requireNonNull(target, "Target must not be null");
        copyProperties(source, target, false);
    }


    /**
     * 深层次拷贝
     *
     * @param from    待转换的集合类
     * @param toClass 目标类class
     * @param <T>     目标类
     * @return 返回目标类
     */
    public static <T> List<T> copyBean(List<?> from, Class<T> toClass) {
        if (from == null || from.isEmpty()) {
            return new ArrayList<>();
        }
        return from.stream()
                .filter(Objects::nonNull)
                .map(source -> copyBean(source, toClass))
                .collect(Collectors.toList());
    }


    /**
     * 深层次拷贝，通过json转换的方式实现
     *
     * @param from    待转换的类
     * @param toClass 目标类class
     * @param <T>     目标类
     * @return 返回目标类
     */
    public static <T> T copyBean(Object from, Class<T> toClass) {
        if (from == null) {
            return null;
        }
        T target = newInstance(toClass);
        copyProperties(from, target);
        return target;
    }

    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("create instance error", e);
        }
    }

    /**
     * 将实体对象转换成Map, key:实体类中的字段名称
     *
     * @param pojo 实体类
     * @return 返回map, key:实体类中的字段名称， value：实体类中的字段值
     */
    public static Map<String, Object> pojoToMap(Object pojo) {
        if (pojo == null) {
            return Collections.emptyMap();
        }
        Method[] methods = pojo.getClass().getMethods();
        Map<String, Object> map = new HashMap<>(methods.length * 2);
        try {
            for (Method method : methods) {
                if (isGetMethod(method)) {
                    String methodName = method.getName();
                    String fieldName = buildFieldName(methodName);
                    Object value = method.invoke(pojo);
                    map.put(fieldName, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("pojoToMap失败", e);
        }
        return map;
    }

    // 构建列名
    private static String buildFieldName(String methodName) {
        if (methodName.startsWith(PREFIX_IS)) {
            return methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
        }
        return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
    }

    /**
     * 是否是get方法
     */
    private static boolean isGetMethod(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType == Void.TYPE) {
            return false;
        }
        String methodName = method.getName();
        if (GET_CLASS_NAME.equals(methodName)) {
            return false;
        }
        boolean isBooleanType = returnType == boolean.class || returnType == Boolean.class;
        return methodName.startsWith(PREFIX_GET) || (isBooleanType && methodName.startsWith(PREFIX_IS));
    }

    /**
     * 是否是set方法
     *
     * @param method
     * @return
     */
    private static boolean isSetMethod(Method method) {
        return method.getName().startsWith(PREFIX_SET) && method.getParameterTypes().length == 1;
    }

    /**
     * 单值转换
     * @param value 单值
     * @param valClass 转换的class类型
     * @param <T> 类型
     * @return 转换后的值
     */
    public static <T> T parseValue(Object value, Class<T> valClass) {
        if (value == null) {
            return null;
        }
        if (valClass == Object.class) {
            return (T) value;
        }
        if (valClass == String.class) {
            return (T) String.valueOf(value);
        }
        // 处理时间类型
        if (value.getClass() == Timestamp.class) {
            Timestamp timestamp = (Timestamp) value;
            if (valClass == Date.class) {
                return (T) timestamp;
            } else if (valClass == java.sql.Date.class) {
                return (T) new java.sql.Date(timestamp.getTime());
            } else if (valClass == LocalDateTime.class) {
                return (T) timestamp.toLocalDateTime();
            }
        }
        if ( valClass == Date.class && value.getClass() == LocalDateTime.class) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            return (T) Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        T t = null;
        // 转换单值：byte（位）、short（短整数）、int（整数）、long（长整数）、float（单精度）、double（双精度）、char（字符）和boolean（布尔值）。
        String strValue = String.valueOf(value);

        if (valClass == Byte.class || valClass == byte.class) {
            t = (T) Byte.valueOf(strValue);
        }
        if (valClass == Short.class || valClass == short.class) {
            t = (T) Short.valueOf(strValue);
        }
        if (valClass == Integer.class || valClass == int.class) {
            t = (T) Integer.valueOf(strValue);
        }
        if (valClass == Long.class || valClass == long.class) {
            t = (T) Long.valueOf(strValue);
        }
        if (valClass == Float.class || valClass == float.class) {
            t = (T) Float.valueOf(strValue);
        }
        if (valClass == Double.class || valClass == double.class) {
            t = (T) Double.valueOf(strValue);
        }
        if (valClass == Character.class || valClass == char.class) {
            t = (T) Character.valueOf(strValue.charAt(0));
        }
        if (valClass == BigDecimal.class) {
            t = (T) (value.getClass() == BigDecimal.class ? value : new BigDecimal(strValue));
        }
        if (valClass == BigInteger.class) {
            t = (T) (value.getClass() == BigInteger.class ? value : new BigInteger(strValue));
        }
        if (valClass == AtomicInteger.class) {
            t = (T) (value.getClass() == AtomicInteger.class ? value : new AtomicInteger(Integer.valueOf(strValue)));
        }
        if (valClass == AtomicLong.class) {
            t = (T) (value.getClass() == AtomicLong.class ? value : new AtomicLong(Long.valueOf(strValue)));
        }
        if (valClass == Boolean.class || valClass == boolean.class) {
            String temp = strValue;
            if ("0".equals(strValue)) {
                temp = "false";
            } else if ("1".equals(strValue)) {
                temp = "true";
            }
            t = (T) Boolean.valueOf(temp);
        }
        if (t != null) {
            return t;
        }
        // 转换其它单值
        return (T) value;
    }

    static class MethodBean {
        private final Method method;
        private final Class<?> paramType;

        public MethodBean(Method method, Class<?> paramType) {
            this.method = method;
            this.paramType = paramType;
        }

        public Method getMethod() {
            return method;
        }

        public Class<?> getParamType() {
            return paramType;
        }
    }
}
