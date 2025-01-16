import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Question1 {
    static class Point {
        BigInteger x;
        BigInteger y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws IOException {
        JSONObject testCase1 = loadJSON("testcase1.json");
        JSONObject testCase2 = loadJSON("testcase2.json");

        System.out.println("Secret for Test Case 1: " + findConstantTerm(testCase1));
        System.out.println("Secret for Test Case 2: " + findConstantTerm(testCase2));
    }

    private static JSONObject loadJSON(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));
        return new JSONObject(content);
    }

    private static BigInteger decodeValue(String value, String base) {
        return new BigInteger(value, Integer.parseInt(base));
    }

    private static BigInteger findConstantTerm(JSONObject testCase) {
        JSONObject keys = testCase.getJSONObject("keys");
        int k = keys.getInt("k");
        List<Point> points = new ArrayList<>();

        // Get first k points
        for (int i = 1; i <= k; i++) {
            String key = String.valueOf(i);
            if (!testCase.has(key)) continue;

            JSONObject root = testCase.getJSONObject(key);
            BigInteger x = BigInteger.valueOf(i);
            BigInteger y = decodeValue(root.getString("value"), root.getString("base"));
            points.add(new Point(x, y));
        }

        return lagrangeInterpolation(points);
    }

    private static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger secret = BigInteger.ZERO;
        int k = points.size();

        // We only need to calculate for x = 0 to get the constant term
        for (int i = 0; i < k; i++) {
            BigInteger numerator = points.get(i).y;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    // For x = 0, we only need the term without x
                    numerator = numerator.multiply(points.get(j).x.negate());
                    denominator = denominator.multiply(points.get(i).x.subtract(points.get(j).x));
                }
            }

            // Handle the division
            if (denominator.signum() < 0) {
                numerator = numerator.negate();
                denominator = denominator.negate();
            }
            secret = secret.add(numerator.divide(denominator));
        }

        return secret;
    }
}