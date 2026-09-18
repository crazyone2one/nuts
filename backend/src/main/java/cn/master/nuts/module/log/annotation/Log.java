package cn.master.nuts.module.log.annotation;

import cn.master.nuts.module.log.constants.OperationLogType;

import java.lang.annotation.*;

/**
 * @author : 11's papa
 * @since : 2026/9/11, 星期五
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    OperationLogType type() default OperationLogType.SELECT;

    String expression();

    Class[] msClass() default {};
}
