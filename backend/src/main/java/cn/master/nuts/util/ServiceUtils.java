package cn.master.nuts.util;

import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.handler.result.NsHttpResultCode;

/**
 * @author : 11's papa
 * @since : 2026/9/7, 星期一
 **/
public class ServiceUtils {
    private static final ThreadLocal<String> resourceName = new ThreadLocal<>();

    public static <T> T checkResourceExist(T resource, String name) {
        if (resource == null) {
            resourceName.set(name);
            throw new NSException(NsHttpResultCode.NOT_FOUND);
        }
        return resource;
    }

    public static String getResourceName() {
        return resourceName.get();
    }

    public static void clearResourceName() {
        resourceName.remove();
    }

}
