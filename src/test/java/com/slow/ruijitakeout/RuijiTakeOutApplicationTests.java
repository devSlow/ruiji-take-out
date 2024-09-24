package com.slow.ruijitakeout;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;


import static com.slow.ruijitakeout.constant.PasswordSaltConstant.SALT;

@SpringBootTest
class RuijiTakeOutApplicationTests {

    @Test
    void handlePassword() {
        String password = "123456";
        String handlePassword = DigestUtils.md5DigestAsHex((SALT+password).getBytes());
        System.out.println(handlePassword);
    }
// 4cd7f80b776d4dc6b873976c8262b90d
}
