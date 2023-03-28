package com.gifu.coreservice.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    public void shouldReturnPng(){
        assertThat(FileUtils.getFileExtension("girl.png")).isEqualTo("png");
    }

}