package com.gifu.coreservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(value = "classpath:application-test.properties")
class GifuCoreServiceApplicationTests {

  @Test
  void contextLoads() {
  }

}
