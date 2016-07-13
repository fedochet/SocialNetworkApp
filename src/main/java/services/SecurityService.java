package services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by roman on 13.07.2016.
 */
public class SecurityService {
    private static final MessageDigest hashFunction;
    private static final SecureRandom random;

    // length of salt in hex string representation
    private static final int SALT_LENGTH = 128;

    static {
        try {
            hashFunction = MessageDigest.getInstance("SHA-256");
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSalt() {
        return bytesToHexString(random.generateSeed(SALT_LENGTH/2));
    }

    public String encryptPassword(String password) {
        String salt = getSalt();
        String saltedPasswordHash = hash(salt+password);

        return salt+saltedPasswordHash;
    }

    public boolean validatePassword(String password, String hash) {
        if (hash.length()<=SALT_LENGTH)
            return false;

        String salt = hash.substring(0, SALT_LENGTH);
        String saltedPasswordHash = hash.substring(SALT_LENGTH);

        return saltedPasswordHash.equals(hash(salt + password));
    }

    private String bytesToHexString(byte[] bytes) {
        final StringBuilder result = new StringBuilder();
        for (byte b: bytes) {
            String hexVal = Integer.toHexString(0xFF & b);
            if (hexVal.length() == 1)
                result.append("0");
            result.append(hexVal);
        }

        return result.toString();
    }

    private String hash(String string) {
        try {
            // MessageDigest is not thread-safe
            MessageDigest myHashFunction = (MessageDigest) hashFunction.clone();

            myHashFunction.reset();
            byte[] bytes = myHashFunction.digest(string.getBytes());
            return bytesToHexString(bytes);

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
