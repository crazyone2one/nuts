package cn.master.nuts.demo;

import cn.master.nuts.dto.system.UserDTO;
import cn.master.nuts.handler.result.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/3, 星期四
 **/
@RestController
@RequestMapping("/users")
public class DemoController {
    @GetMapping("/basic")
    @JsonView(Views.Internal.class)
    public UserDTO getBasic() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("1");
        userDTO.setName("zhangsan");
        userDTO.setEmail("zs@zs.com");
        userDTO.setPhone("123456");
        return userDTO;
    }

    @GetMapping("/detail")
    @JsonView(Views.Public.class)
    public List<UserDTO> getDetail() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("1");
        userDTO.setName("zhangsan");
        userDTO.setEmail("zs@zs.com");
        userDTO.setPhone("123456");
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId("2");
        userDTO2.setName("lisi");
        userDTO2.setEmail("ls@ls.com");
        userDTO2.setPhone("123456");
        List<UserDTO> userDTOList = new ArrayList<>();
        userDTOList.add(userDTO);
        userDTOList.add(userDTO2);
        return userDTOList;
    }
}
