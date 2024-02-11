package Model.Key_Controller;

public class KeyGenerator {
    public static String generateKey(String password, String salt) {
        final int keyLength = 32;
        int saltLength = salt.length();
        int passwordLength = password.length();

        // Ensure the password and salt are the correct lengths
        if (passwordLength < 1 || passwordLength > keyLength) {
            throw new IllegalArgumentException("Password must be between 1 and 32 characters long");
        }
        if (saltLength < 1 || saltLength > keyLength) {
            throw new IllegalArgumentException("Salt must be between 1 and 32 characters long");
        }

        // Convert password and salt to byte arrays
        byte[] passwordBytes = password.getBytes();
        byte[] saltBytes = salt.getBytes();

        // Initialize the key as a byte array
        byte[] keyBytes = new byte[keyLength];

        // Perform XOR operation and shift for each byte
        for (int i = 0; i < keyLength; i++) {
            keyBytes[i] = (byte) ((passwordBytes[i % passwordLength] ^ saltBytes[i % saltLength]) + i % 32);
        }

        // Convert the byte array to a hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : keyBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
