package util;

import java.util.Map;

import main.Person;

public class util {
    public static int addMapEntrys(Map<Person, Integer> map) {
        int total = 0;
        for (Map.Entry<Person, Integer> entry : map.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }
}
