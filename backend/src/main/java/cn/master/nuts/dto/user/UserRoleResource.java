package cn.master.nuts.dto.user;

import cn.master.nuts.handler.result.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/9/14, 星期一
 **/
@Data
public class UserRoleResource implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonView(Views.Internal.class)
    private String id;
    @JsonView(Views.Internal.class)
    private String name;
    private Boolean license = false;
}
