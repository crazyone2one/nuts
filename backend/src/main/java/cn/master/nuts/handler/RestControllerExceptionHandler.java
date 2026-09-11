package cn.master.nuts.handler;

import cn.master.nuts.handler.exception.IResultCode;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.handler.result.NsHttpResultCode;
import cn.master.nuts.handler.result.ResultHolder;
import cn.master.nuts.util.ServiceUtils;
import cn.master.nuts.util.Translator;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/9/7, 星期一
 **/
@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultHolder handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResultHolder.error(NsHttpResultCode.VALIDATE_FAILED.getCode(), NsHttpResultCode.VALIDATE_FAILED.getMessage(), errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultHolder handleHttpRequestMethodNotSupportedException(HttpServletResponse response, Exception exception) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        return ResultHolder.error(HttpStatus.METHOD_NOT_ALLOWED.value(), exception.getMessage());
    }

    @ExceptionHandler(NSException.class)
    public ResponseEntity<ResultHolder> handlerNSException(NSException e) {
        IResultCode errorCode = e.getErrorCode();
        if (errorCode == null) {
            // 如果抛出异常没有设置状态码，则返回错误 message
            return ResponseEntity.internalServerError()
                    .body(ResultHolder.error(NsHttpResultCode.FAILED.getCode(), e.getMessage()));
        }
        int code = errorCode.getCode();
        String message = errorCode.getMessage();
        message = Translator.get(message, message);

        if (errorCode instanceof NsHttpResultCode) {
            // 如果是 NsHttpResultCode，则设置响应的状态码，取状态码的后三位
            if (errorCode.equals(NsHttpResultCode.NOT_FOUND)) {
                message = getNotFoundMessage(message);
            }
            return ResponseEntity.status(code % 1000)
                    .body(ResultHolder.error(code, message, e.getMessage()));
        } else {
            // 响应码返回 500，设置业务状态码
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultHolder.error(code, Translator.get(message, message), e.getMessage()));
        }
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResultHolder> handleException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(ResultHolder.error(NsHttpResultCode.FAILED.getCode(),
                        e.getMessage(), getStackTraceAsString(e)));
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    private String getNotFoundMessage(String message) {
        String resourceName = ServiceUtils.getResourceName();
        if (StringUtils.isNotBlank(resourceName)) {
            message = String.format(message, Translator.get(resourceName, resourceName));
        } else {
            message = String.format(message, Translator.get("resource.name"));
        }
        ServiceUtils.clearResourceName();
        return message;
    }
}
