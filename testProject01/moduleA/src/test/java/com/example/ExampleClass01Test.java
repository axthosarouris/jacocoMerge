package com.example;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class ExampleClass01Test {

    @Test
    public void exampleClassExists(){
        assertDoesNotThrow(ExampleClass01::new);
    }

    @Test
    public void exampleMethod01Returns3() {
        int actual = new ExampleClass01().exampleMethod01();
        assertThat(actual).isEqualTo(3);
    }


}