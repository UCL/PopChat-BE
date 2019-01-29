package uk.ac.ucl.rits.popchat.security.salt;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A Salt generator that uses the SHA1 pseudo-random number generator.
 *
 * @author RSDG
 *
 */
public class Sha1prngSaltGenerator implements SaltGenerator {

    @Override
    public byte[] generateSalt(int length) throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[length];
        sr.nextBytes(salt);
        return salt;
    }

}
