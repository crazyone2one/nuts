package cn.master.nuts.handler.annotation;

import java.lang.annotation.*;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileLimit {

    /**
     * 文件大小限制 (单位: MB)
     */
    long maxSize() default 0;
}
