package com.gifu.coreservice;

import com.gifu.coreservice.config.xenditmerchant.BriConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class GifuCoreServiceApplicationTests {

    @Autowired
    private BriConfiguration briConfiguration;
    @Test
    void contextLoads() {
        assertThat(briConfiguration).isNotNull();
    }

}
