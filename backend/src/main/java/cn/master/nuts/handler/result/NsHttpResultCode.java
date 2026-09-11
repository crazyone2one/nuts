package cn.master.nuts.handler.result;

import cn.master.nuts.handler.exception.IResultCode;

/**
 * @author : 11's papa
 * @since : 2026/9/7, 星期一
 **/
public enum NsHttpResultCode implements IResultCode {
    SUCCESS(100200, "http_result_success"),
    FAILED(100500, "http_result_unknown_exception"),
    VALIDATE_FAILED(100400, "http_result_validate"),
    UNAUTHORIZED(100401, "http_result_unauthorized"),
    FORBIDDEN(100403, "http_result_forbidden"),
    NOT_FOUND(100404, "http_result_not_found");

    private final int code;
    private final String message;

    NsHttpResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return getTranslationMessage(this.message);
    }
}
