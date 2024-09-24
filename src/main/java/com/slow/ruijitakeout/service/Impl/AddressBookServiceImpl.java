package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.slow.ruijitakeout.domain.AddressBook;
import com.slow.ruijitakeout.mapper.AddressBookMapper;
import com.slow.ruijitakeout.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
