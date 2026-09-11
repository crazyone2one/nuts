package cn.master.nuts.handler.result;

import cn.master.nuts.util.JSON;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collections;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/9/7, 星期一
 **/
@RestControllerAdvice
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return JacksonJsonHttpMessageConverter.class.isAssignableFrom(converterType) || StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body,
                                            @NonNull MethodParameter methodParameter,
                                            @NonNull MediaType mediaType,
                                            @NonNull Class<? extends HttpMessageConverter<?>> converterType,
                                            @NonNull ServerHttpRequest request,
                                            @NonNull ServerHttpResponse response) {

        // 1) null + String 返回时，手动包装成 JSON，避免 Spring StringHttpMessageConverter 直接输出 null
        if (body == null) {
            if (StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return JSON.toJSONString(ResultHolder.success(null));
            }
            return ResultHolder.success(null);
        }
        // 2) 已经是统一返回体，直接返回（JsonView 由 determineWriteHints 下发）
        if (body instanceof ResultHolder) {
            return body;
        }

        // 3) String 类型直接按统一返回体包装
        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JSON.toJSONString(ResultHolder.success(body));
        }

        // 4) 普通对象：包装成 ResultHolder，直接返回（JsonView 由 determineWriteHints 下发）
        return ResultHolder.success(body);
    }

    /**
     * 当转换器为 Spring 7 的 {@link org.springframework.http.converter.SmartHttpMessageConverter} 时，
     * 将控制器方法上的 {@link JsonView} 转换成 write hints 传给 Jackson 3 转换器，
     * 使 {@link ResultHolder#getData()} 中实体内核能被按视图序列化。
     * <p>该机制替代了 Spring 6 时代通过 {@code MappingJacksonValue} 传递视图的方式
     * （Spring 7 中 {@code MappingJacksonValue} 已废弃，转换器不再识别）。
     */
    @Override
    public @Nullable Map<String, Object> determineWriteHints(@Nullable Object body,
                                                             @NonNull MethodParameter returnType,
                                                             @NonNull MediaType selectedContentType,
                                                             @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType) {
        JsonView jsonView = returnType.getMethodAnnotation(JsonView.class);
        if (jsonView == null && returnType.getContainingClass() != null) {
            jsonView = returnType.getContainingClass().getAnnotation(JsonView.class);
        }
        if (jsonView == null || jsonView.value() == null || jsonView.value().length == 0) {
            return null;
        }
        return Collections.singletonMap(JsonView.class.getName(), jsonView.value()[0]);
    }
}
