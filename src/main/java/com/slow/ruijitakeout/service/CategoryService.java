package com.slow.ruijitakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slow.ruijitakeout.domain.Category;

public interface CategoryService extends IService<Category> {
    public void removeById(Long ids);
}
