import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class QuadraticInvalidCombinatorialTest2 {

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
     * 测试 b 参数非法输入：
     * 初次输入：a = "1"（合法），b = invalidB（非法），程序检测到 b 错误后重新从 a 开始输入。
     * 第二轮输入：a = "1"，b = "2"，c = "3"；
     * 最后输入 "n" 退出（额外追加一行 "n" 确保退出）。
     */
    @ParameterizedTest
    @ValueSource(strings = {"@", "1E309", "1E-325"})
    void testInvalidB(String invalidB) {
        // 构造输入脚本：
        // 第一轮：a = "1", b = invalidB
        // 第二轮：重新输入 a = "1", b = "2", c = "3"
        // 最后退出："n"（再追加一行 "n"）
        String simulatedInput = "1\n" + invalidB + "\n" +
                "1\n" + "2\n" + "3\n" +
                "n\n" + "n\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // 限制整个执行过程在 5 秒内完成
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Quadratic.main(new String[]{});
        });

        String output = outContent.toString();
        System.out.println("Simulated Input for B:\n" + simulatedInput);
        System.out.println("Captured Output for B:\n" + output);

        // 检查输出中是否包含针对 b 的非法输入的提示信息
        Assertions.assertTrue(
                output.contains("not allowed") || output.contains("too large or too small"),
                "对于 b 的非法输入 " + invalidB + " 应提示错误信息。"
        );
        // 检查最终输出中包含求解结果提示，例如 "x1 ="
        Assertions.assertTrue(
                output.contains("x1 ="),
                "在重新输入合法 b 后，最终输出应包含求解结果 'x1 ='."
        );
    }

    /**
     * 测试 c 参数非法输入：
     * 初次输入：a = "1"，b = "2"，c = invalidC（非法），程序检测到 c 错误后重新从 a 开始输入。
     * 第二轮输入：a = "1"，b = "2"，c = "3"；
     * 最后输入 "n" 退出（再追加一行 "n"）。
     */
    @ParameterizedTest
    @ValueSource(strings = {"@", "1E309", "1E-325"})
    void testInvalidC(String invalidC) {
        // 构造输入脚本：
        // 第一轮：a = "1", b = "2", c = invalidC
        // 第二轮：重新输入 a = "1", b = "2", c = "3"
        // 最后退出："n"（再追加一行 "n"）
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

        // 检查输出中是否包含针对 c 的非法输入的提示信息
        Assertions.assertTrue(
                output.contains("not allowed") || output.contains("too large or too small"),
                "对于 c 的非法输入 " + invalidC + " 应提示错误信息。"
        );
        // 检查最终输出中包含求解结果提示，例如 "x1 ="
        Assertions.assertTrue(
                output.contains("x1 ="),
                "在重新输入合法 c 后，最终输出应包含求解结果 'x1 ='."
        );
    }
}
