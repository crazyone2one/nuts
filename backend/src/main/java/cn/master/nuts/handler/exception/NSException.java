package cn.master.nuts.handler.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : 11's papa
 * @since : 2026/9/7, 星期一
 **/
@Getter
public class NSException extends RuntimeException {
    protected IResultCode errorCode;

    public NSException(String message) {
        super(message);
    }

    public NSException(Throwable t) {
        super(t);
    }

    public NSException(IResultCode errorCode) {
        super(StringUtils.EMPTY);
        this.errorCode = errorCode;
    }

    public NSException(IResultCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public NSException(IResultCode errorCode, Throwable t) {
        super(t);
        this.errorCode = errorCode;
    }

    public NSException(String message, Throwable t) {
        super(message, t);
    }

}
