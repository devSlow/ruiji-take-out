package com.slow.ruijitakeout.dto;

import com.slow.ruijitakeout.domain.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String code;
}
