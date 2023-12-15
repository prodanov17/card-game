import java.security.SecureRandom;
import java.math.BigInteger;

public class GameIdGenerator {

    private static final int ID_LENGTH = 8;

    public static String generateUniqueId() {
        SecureRandom secureRandom = new SecureRandom();
        return new BigInteger(ID_LENGTH * 5, secureRandom).toString(32).substring(0, ID_LENGTH);
    }

}