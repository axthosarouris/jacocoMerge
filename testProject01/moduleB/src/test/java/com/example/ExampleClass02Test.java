package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

public class ExampleClass02Test {
    @Test
    public void exampleClassExists(){
        assertDoesNotThrow(ExampleClass02::new);
    }

    @Test
    public void exampleMethod02Returns3() {
        int actual = new ExampleClass02().exampleMethod02();
        assertThat(actual).isEqualTo(3);
    }


}