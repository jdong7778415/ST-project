import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;


public class QuadraticValidCombinatorialTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // 重定向 System.out 到 outContent 以捕获输出
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // 恢复原始 System.in 和 System.out
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * 每个测试用例从 CSV 文件中读取一组合法的 a, b, c 输入。
     * 我们将这 3 个输入依次送入主程序，再加上 "n" 退出程序，
     * 模拟一次完整的用户交互流程。
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/valid-test-data.csv", numLinesToSkip = 1)
    void testValidInput(String a, String b, String c) {
        // 构造输入脚本：依次输入 a, b, c，再输入 "n" 退出程序
        String simulatedInput = a + "\n" + b + "\n" + c + "\n" + "n\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // 使用 assertTimeoutPreemptively 限制主程序执行时间，确保 Quadratic.main 方法在 5 秒内完成执行。
        // 如果执行时间超过 5 秒，则测试会自动中断并标记为失败，防止因无限循环或卡住而阻塞测试。
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Quadratic.main(new String[]{});
        });

        String output = outContent.toString();

        // 输出中应包含求解结果提示，如 "x1 ="，表示成功计算出根
        Assertions.assertTrue(output.contains("x1 ="),
                "对于合法输入，输出应包含求解结果，例如 'x1 ='。");
    }
}

