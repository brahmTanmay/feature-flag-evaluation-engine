package com.featureflags.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Sha256StableHasher implements StableHasher {
    private static final ThreadLocal<MessageDigest> DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    });

    @Override
    public int bucket(String input, int modulo) {
        if (input == null) {
            throw new IllegalArgumentException("input is required");
        }
        if (modulo <= 0) {
            throw new IllegalArgumentException("modulo must be > 0");
        }

        MessageDigest messageDigest = DIGEST.get();
        messageDigest.reset();
        byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

        int value = ((hash[0] & 0xFF) << 24)
                | ((hash[1] & 0xFF) << 16)
                | ((hash[2] & 0xFF) << 8)
                | (hash[3] & 0xFF);

        return Math.floorMod(value, modulo);
    }
}
