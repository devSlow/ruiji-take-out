package com.slow.ruijitakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.slow.ruijitakeout.domain.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AddressBookMapper extends BaseMapper<AddressBook> {

}
