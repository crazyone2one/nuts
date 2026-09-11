package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.CustomFieldOption;
import cn.master.nuts.module.system.mapper.CustomFieldOptionMapper;
import cn.master.nuts.module.system.service.CustomFieldOptionService;
import org.springframework.stereotype.Service;

/**
 * 自定义字段选项 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class CustomFieldOptionServiceImpl extends ServiceImpl<CustomFieldOptionMapper, CustomFieldOption>  implements CustomFieldOptionService{

}
