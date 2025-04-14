import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class QuadraticInvalidCombinatorialTest {

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

    /**
     * 测试 a 参数非法输入
     */
    @ParameterizedTest
    @ValueSource(strings = {"@", "1E309", "1E-325"})
    void testInvalidA(String invalidA) {
        String simulatedInput = invalidA + "\n" +
                "1\n" + "2\n" + "3\n" +
                "n\n" + "n\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Quadratic.main(new String[]{});
        });

        String output = outContent.toString();
        System.out.println("Simulated Input for A:\n" + simulatedInput);
        System.out.println("Captured Output for A:\n" + output);

        Assertions.assertTrue(
                output.contains("not allowed") || output.contains("too large or too small"),
                "对于 a 的非法输入 " + invalidA + " 应提示错误信息。"
        );
        Assertions.assertTrue(
                output.contains("x1 ="),
                "在重新输入合法 a 后，最终输出应包含求解结果 'x1 ='。"
        );
    }

    /**
     * 测试 a = 0 的边界情况（不是二次项）
     */
    @Test
    void testAEqualsZero() {
        String simulatedInput = "0\n" +
                "1\n" + "2\n" + "3\n" +
                "n\n" + "n\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Quadratic.main(new String[]{});
        });

        String output = outContent.toString();
        System.out.println("Simulated Input for A=0:\n" + simulatedInput);
        System.out.println("Captured Output for A=0:\n" + output);

        Assertions.assertTrue(
                output.toLowerCase().contains("cannot be zero"),
                "当 a = 0 时，程序应输出 'cannot be zero' 的提示信息。"
        );

        Assertions.assertTrue(
                output.contains("x1 ="),
                "在重新输入有效 a 后，程序应成功计算并输出结果。"
        );
    }

    /**
     * 测试 b 参数非法输入
     */
    @ParameterizedTest
    @ValueSource(strings = {"@", "1E309", "1E-325"})
    void testInvalidB(String invalidB) {
        String simulatedInput = "1\n" + invalidB + "\n" +
                "1\n" + "2\n" + "3\n" +
                "n\n" + "n\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Quadratic.main(new String[]{});
        });

        String output = outContent.toString();
        System.out.println("Simulated Input for B:\n" + simulatedInput);
        System.out.println("Captured Output for B:\n" + output);

        Assertions.assertTrue(
                output.contains("not allowed") || output.contains("too large or too small"),
                "对于 b 的非法输入 " + invalidB + " 应提示错误信息。"
        );
        Assertions.assertTrue(
                output.contains("x1 ="),
                "在重新输入合法 b 后，最终输出应包含求解结果 'x1 ='。"
        );
    }

    /**
     * 测试 c 参数非法输入
     */
    @ParameterizedTest
    @ValueSource(strings = {"@", "1E309", "1E-325"})
    void testInvalidC(String invalidC) {
        String simulatedInput = "1\n" + "2\n" + invalidC + "\n" +
                "1\n" + "2\n" + "3\n" +
                "n\n" + "n\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Quadratic.main(new String[]{});
        });

        String output = outContent.toString();
        System.out.println("Simulated Input for C:\n" + simulatedInput);
        System.out.println("Captured Output for C:\n" + output);

        Assertions.assertTrue(
                output.contains("not allowed") || output.contains("too large or too small"),
                "对于 c 的非法输入 " + invalidC + " 应提示错误信息。"
        );
        Assertions.assertTrue(
                output.contains("x1 ="),
                "在重新输入合法 c 后，最终输出应包含求解结果 'x1 ='。"
        );
    }
}
