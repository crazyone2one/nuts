package cn.master.nuts.handler.exception;

import cn.master.nuts.util.Translator;

/**
 * @author : 11's papa
 * @since : 2026/9/7, 星期一
 **/
public interface IResultCode {
    /**
     * 返回状态码
     */
    int getCode();

    /**
     * 返回状态码信息
     */
    String getMessage();

    /**
     * 返回国际化后的状态码信息
     * 如果没有匹配则返回原文
     */
    default String getTranslationMessage(String message) {
        return Translator.get(message, message);
    }
}
