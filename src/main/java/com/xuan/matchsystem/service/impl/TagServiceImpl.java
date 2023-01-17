package com.xuan.matchsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.matchsystem.model.domain.Tag;
import com.xuan.matchsystem.service.TagService;
import com.xuan.matchsystem.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author 炫
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2023-01-03 22:06:46
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




