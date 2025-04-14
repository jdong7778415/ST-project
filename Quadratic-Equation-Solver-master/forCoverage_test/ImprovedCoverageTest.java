import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ImprovedCoverageTest {

    @Test
    void testImaginaryEqualsOneBranchTrue() throws Exception {
        double a = 1;
        double b = 0;
        double c = 1; // discriminant = -4, sqrt = 2 -> sqrt / (2a) = 1, so imaginary = "1"

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        Quadratic.solveQuadratic(a, b, c);

        System.setOut(originalOut);
        String output = out.toString();

        // “1i” 不应该出现，而是 “i” 或 “-i”
        assertTrue(output.contains("i") && !output.contains("1i"),
                "Should omit '1' before 'i' when imaginary == 1");
    }

    @Test
    void testPromptEqualsYBranchFalse() {
        String simulatedInput = "1\n2\n1\ny\n1\n2\n1\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setIn(in);
        System.setOut(new PrintStream(out));

        Quadratic.main(new String[]{});

        System.setOut(originalOut);
        System.setIn(System.in);
        String output = out.toString();

        assertTrue(output.contains("x1 ="),
                "Expected output to contain root calculation message");
    }
}
