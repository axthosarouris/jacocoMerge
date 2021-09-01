package com.github.axthosarouris.jacocomerge;

import java.util.Optional;

public final class Environment {

    private Environment() {

    }

    public static String readEnv(String envVariableName) {
        return Optional.ofNullable(System.getenv().getOrDefault(envVariableName, null))
            .orElseThrow(() -> new RuntimeException("Could not find env variable: `" + envVariableName));
    }
}
