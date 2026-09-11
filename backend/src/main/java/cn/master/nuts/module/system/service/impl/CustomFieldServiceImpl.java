package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.CustomField;
import cn.master.nuts.module.system.mapper.CustomFieldMapper;
import cn.master.nuts.module.system.service.CustomFieldService;
import org.springframework.stereotype.Service;

/**
 * 自定义字段 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class CustomFieldServiceImpl extends ServiceImpl<CustomFieldMapper, CustomField>  implements CustomFieldService{

}
