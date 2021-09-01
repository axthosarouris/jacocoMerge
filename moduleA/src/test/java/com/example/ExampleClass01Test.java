package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ExampleClass01Test {

    @Test
    public void exampleMethod01Returns3() {
        int actual = new ExampleClass01().exampleMethod01();
        assertThat(actual).isEqualTo(3);
    }


}