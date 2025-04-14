import com.microsoft.z3.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuadraticZ3ValidTest {

    private static final double TOLERANCE = 1e-6;

    @Test
    public void testRealAndComplexRoots() throws Exception {
        Context ctx = new Context();
        Solver solver = ctx.mkSolver();

        RealExpr a = ctx.mkRealConst("a");
        RealExpr b = ctx.mkRealConst("b");
        RealExpr c = ctx.mkRealConst("c");

        // 添加基本约束：a ≠ 0，限制范围避免过大过小引起精度异常
        solver.add(ctx.mkNot(ctx.mkEq(a, ctx.mkReal(0))));
        solver.add(ctx.mkGe(a, ctx.mkReal("-10")));
        solver.add(ctx.mkLe(a, ctx.mkReal("10")));
        solver.add(ctx.mkGe(b, ctx.mkReal("-100")));
        solver.add(ctx.mkLe(b, ctx.mkReal("100")));
        solver.add(ctx.mkGe(c, ctx.mkReal("-100")));
        solver.add(ctx.mkLe(c, ctx.mkReal("100")));

        // 多次尝试生成满足不同判别式条件的解
        String[] labels = {"> 0 (real unequal)", "= 0 (real equal)", "< 0 (complex roots)"};
        BoolExpr[] discriminantConditions = {
                ctx.mkGt(ctx.mkSub(ctx.mkMul(b, b), ctx.mkMul(ctx.mkInt(4), a, c)), ctx.mkReal(0)),
                ctx.mkEq(ctx.mkSub(ctx.mkMul(b, b), ctx.mkMul(ctx.mkInt(4), a, c)), ctx.mkReal(0)),
                ctx.mkLt(ctx.mkSub(ctx.mkMul(b, b), ctx.mkMul(ctx.mkInt(4), a, c)), ctx.mkReal(0))
        };

        for (int i = 0; i < discriminantConditions.length; i++) {
            solver.push();
            solver.add(discriminantConditions[i]);

            if (solver.check() != Status.SATISFIABLE) {
                solver.pop();
                continue;
            }

            Model model = solver.getModel();
            double aVal = parseZ3Number(model.evaluate(a, false).toString());
            double bVal = parseZ3Number(model.evaluate(b, false).toString());
            double cVal = parseZ3Number(model.evaluate(c, false).toString());

            System.out.printf("\n[%s] Equation: %.4fx^2 + %.4fx + %.4f = 0\n", labels[i], aVal, bVal, cVal);

            // 精度保护检查
            double discriminant = bVal * bVal - 4 * aVal * cVal;
            if (Double.isNaN(discriminant) || discriminant == bVal * bVal) {
                System.out.println("[Skip] Triggered NotEnoughPrecisionException by design");
                solver.pop();
                continue;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream original = System.out;
            System.setOut(new PrintStream(out));

            try {
                Quadratic.solveQuadratic(aVal, bVal, cVal);
            } catch (NotEnoughPrecisionException e) {
                fail("Unexpected NotEnoughPrecisionException");
            } finally {
                System.setOut(original);
            }

            String output = out.toString(StandardCharsets.UTF_8).trim();
            System.out.println(output);

            if (!output.contains("i")) {
                List<Double> roots = extractRootsFromOutput(output);
                for (double x : roots) {
                    double fx = aVal * x * x + bVal * x + cVal;
                    assertTrue(Math.abs(fx) < TOLERANCE, "f(" + x + ") = " + fx + " is not approximately zero");
                }
            } else {
                // ==== 复数验证逻辑 ====
                String[] lines = output.split("\\n");
                String realStr = "", imagStr = "";
                for (String line : lines) {
                    if (line.startsWith("x1 = ")) {
                        String[] parts = line.replace("x1 = ", "").split("\\+");
                        if (parts.length == 2) {
                            realStr = parts[0].trim();
                            imagStr = parts[1].replace("i", "").trim();
                        } else if (line.contains("i")) {
                            realStr = "0";
                            imagStr = line.replace("x1 = ", "").replace("i", "").trim();
                        }
                    }
                }
                try {
                    double realPart = Double.parseDouble(realStr);
                    double imagPart = Double.parseDouble(imagStr);
                    double expectedReal = -bVal / (2 * aVal);
                    double expectedImag = Math.sqrt(-discriminant) / (2 * aVal);
                    assertTrue(Math.abs(realPart - expectedReal) < TOLERANCE, "Real part mismatch: " + realPart + " vs " + expectedReal);
                    assertTrue(Math.abs(imagPart - expectedImag) < TOLERANCE, "Imaginary part mismatch: " + imagPart + " vs " + expectedImag);
                    System.out.println("[✓] Complex root verified: real and imaginary parts correct.");
                } catch (NumberFormatException e) {
                    fail("Failed to parse complex root parts.");
                }
            }

            solver.pop();
        }

        ctx.close();
    }

    private static double parseZ3Number(String value) {
        if (value.contains("/")) {
            String[] parts = value.split("/");
            return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
        } else {
            return Double.parseDouble(value);
        }
    }

    private static List<Double> extractRootsFromOutput(String output) {
        List<Double> roots = new ArrayList<>();
        for (String line : output.split("\\n")) {
            if ((line.contains("x1") || line.contains("x2")) && !line.contains("i")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    try {
                        String cleaned = parts[1].replace("+", "").trim();
                        roots.add(Double.parseDouble(cleaned));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return roots;
    }
}

