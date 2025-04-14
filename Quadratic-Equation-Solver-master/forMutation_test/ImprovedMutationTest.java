import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ImprovedMutationTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testZeroDiscriminant() throws Exception {
        // a=1, b=2, c=1 => discriminant = 0 => one real root
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Quadratic.solveQuadratic(1, 2, 1);
        });
        String output = outContent.toString();
        assertTrue(output.contains("x1 =") && !output.contains("x2 ="));
    }

    @Test
    void testNegativeDiscriminantComplexOutput() throws Exception {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Quadratic.solveQuadratic(1, 2, 5);
        });
        String output = outContent.toString();
        assertTrue(output.contains("i"));
        assertTrue(output.contains("x1 =") && output.contains("x2 ="));
    }

    @Test
    void testSameRootsNotPrintedTwice() throws Exception {
        // a = 1, b = 2, c = 1 -> root is -1, should only print once
        Quadratic.solveQuadratic(1, 2, 1);
        String output = outContent.toString();
        long count = output.lines().filter(line -> line.contains("x1 =")).count();
        assertEquals(1, count);
    }

    @Test
    void testSignFunctionPositive() {
        assertEquals(1, invokeSign(5.0));
    }

    @Test
    void testSignFunctionNegative() {
        assertEquals(-1, invokeSign(-3.2));
    }

    @Test
    void testNewtonSqrt() throws Exception {
        double sqrt = invokeSqrtByNewton(9.0);
        assertTrue(Math.abs(sqrt - 3.0) < 0.0001);
    }

    @Test
    void testFormatIntegerDouble() throws Exception {
        String formatted = invokeFormatDouble(4.0);
        assertEquals("4", formatted);
    }

    @Test
    void testFormatDecimalDouble() throws Exception {
        String formatted = invokeFormatDouble(4.75);
        assertEquals("4.75", formatted);
    }

    private int invokeSign(double b) {
        try {
            var method = Quadratic.class.getDeclaredMethod("sign", double.class);
            method.setAccessible(true);
            return (int) method.invoke(null, b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double invokeSqrtByNewton(double val) {
        try {
            var method = Quadratic.class.getDeclaredMethod("sqrtByNewton", double.class);
            method.setAccessible(true);
            return (double) method.invoke(null, val);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeFormatDouble(double val) {
        try {
            var method = Quadratic.class.getDeclaredMethod("formatDouble", double.class);
            method.setAccessible(true);
            return (String) method.invoke(null, val);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
