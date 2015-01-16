package org.priorityhealth.stab.pdiff.domain.service.general;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class DigestService {
    /**
     * SHA1 Digest
     * @param source Source to digest
     * @return Digest
     */
    public static String getSHA1(byte[] source) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(source);
            byte[] byteData = md.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte aByte : byteData) {
                stringBuilder.append(Integer.toString((aByte & 0xFF) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException ex) {
            // Nobody cares.
        }
        return null;
    }
}
