import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. Prefix function (LPS array).
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        int k = 0;
        for (int i = 1; i < m; i++) {
            while (k > 0 && p.charAt(k) != p.charAt(i)) {
                k = pi[k - 1];
            }
            if (p.charAt(k) == p.charAt(i)) {
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP search (supports overlapping occurrences).
     */
    public static int[] findAll(String text, String p) {
        if (p.isEmpty() || text.length() < p.length()) {
            return new int[0];
        }
        int[] pi = prefixFunction(p);
        List<Integer> matches = new ArrayList<>();
        int k = 0;

        for (int i = 0; i < text.length(); i++) {
            while (k > 0 && (k == p.length() || p.charAt(k) != text.charAt(i))) {
                k = pi[k - 1];
            }
            if (p.charAt(k) == text.charAt(i)) {
                k++;
            }
            if (k == p.length()) {
                matches.add(i - p.length() + 1);
            }
        }

        int[] result = new int[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            result[i] = matches.get(i);
        }
        return result;
    }

    /**
     * Mission 4. Rabin-Karp with rolling hash.
     * Counts how many starting positions i in string A have A[i..i+k-1] present in B.
     */
    public static int sharedWords(String a, String b, int k) {
        if (a.length() < k || b.length() < k) {
            return 0;
        }

        long BASE = 31;
        long MOD = 1_000_000_007L;

        // BASE^(k-1) % MOD
        long pPow = 1;
        for (int i = 0; i < k - 1; i++) {
            pPow = (pPow * BASE) % MOD;
        }

        // Хэшируем все окна длины k из строки B
        Map<Long, List<Integer>> bHashes = new HashMap<>();
        long hB = 0;
        for (int i = 0; i < k; i++) {
            hB = (hB * BASE + b.charAt(i)) % MOD;
        }
        bHashes.computeIfAbsent(hB, x -> new ArrayList<>()).add(0);

        for (int i = 1; i <= b.length() - k; i++) {
            hB = (hB - b.charAt(i - 1) * pPow) % MOD;
            hB = (hB * BASE + b.charAt(i + k - 1)) % MOD;
            hB = (hB + MOD) % MOD;
            bHashes.computeIfAbsent(hB, x -> new ArrayList<>()).add(i);
        }

        // Проверяем каждое окно строки A
        int count = 0;
        long hA = 0;
        for (int i = 0; i < k; i++) {
            hA = (hA * BASE + a.charAt(i)) % MOD;
        }
        if (bHashes.containsKey(hA) && matches(a, 0, b, bHashes.get(hA), k)) {
            count++;
        }

        for (int i = 1; i <= a.length() - k; i++) {
            hA = (hA - a.charAt(i - 1) * pPow) % MOD;
            hA = (hA * BASE + a.charAt(i + k - 1)) % MOD;
            hA = (hA + MOD) % MOD;

            if (bHashes.containsKey(hA) && matches(a, i, b, bHashes.get(hA), k)) {
                count++;
            }
        }
        return count;
    }

    // Проверка коллизий посимвольно
    private static boolean matches(String a, int iA, String b, List<Integer> bPositions, int k) {
        for (int iB : bPositions) {
            boolean same = true;
            for (int t = 0; t < k; t++) {
                if (a.charAt(iA + t) != b.charAt(iB + t)) {
                    same = false;
                    break;
                }
            }
            if (same) return true;
        }
        return false;
    }
}