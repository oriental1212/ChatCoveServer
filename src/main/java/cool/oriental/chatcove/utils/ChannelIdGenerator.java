package cool.oriental.chatcove.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @Author: Oriental
 * @Date: 2023-07-15-22:55
 * @Description:
 */
public class ChannelIdGenerator {
    private static final int ID_LENGTH = 11;
    private static final String CHARACTERS = "1234567890";
    private static Set<String> usedIds = new HashSet<>();

    public static String generateUniqueId() {
        String id;
        do {
            id = generateRandomId();
        } while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }

    private static String generateRandomId() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
