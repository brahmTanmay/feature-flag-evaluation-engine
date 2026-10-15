package com.featureflags.hash;

public interface StableHasher {
    int bucket(String input, int modulo);
}
