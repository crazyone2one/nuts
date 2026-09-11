package cn.master.nuts.dto.system;

import cn.master.nuts.handler.result.Views;
import cn.master.nuts.module.system.entity.User;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/9/3, 星期四
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends User {

    @JsonView(Views.Internal.class)
    private String accessToken;
}
