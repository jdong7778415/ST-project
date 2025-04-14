import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class QuadraticZ3InvalidTest {

    @Test
    public void testAEqualsZero() {
        Exception exception = assertThrows(NotEnoughPrecisionException.class, () -> {
            Quadratic.solveQuadratic(0, 2, 1);
        });
        System.out.println("a = 0: " + exception.getClass().getSimpleName());
    }

    @Test
    public void testScientificNotationInputs() {
        assertDoesNotThrow(() -> Quadratic.solveQuadratic(1e10, 3e-5, -7e3));
        assertDoesNotThrow(() -> Quadratic.solveQuadratic(1e-10, 5e-2, 2e-1));
    }

    @Test
    void testDoubleMax() {
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            assertThrows(NotEnoughPrecisionException.class, () -> {
                Quadratic.solveQuadratic(Double.MAX_VALUE, 1, 1);
            });
        });
    }

    @Test
    public void testInvalidCharacterInput() {
        assertThrows(NumberFormatException.class, () -> {
            Quadratic.validateInput("@");
        });

        assertThrows(NumberFormatException.class, () -> {
            Quadratic.validateInput("abc");
        });
    }

    @Test
    public void testEmptyInput() {
        assertThrows(NumberFormatException.class, () -> {
            Quadratic.validateInput("");
        });
    }

    @Test
    public void testValidDecimalInputs() {
        assertDoesNotThrow(() -> Quadratic.solveQuadratic(0.25, -0.5, 0.25));
        assertDoesNotThrow(() -> Quadratic.solveQuadratic(-1.5, 2.75, -0.5));
    }

    @Test
    public void testScientificNotationValidation() {
        Assertions.assertDoesNotThrow(() -> Quadratic.validateInput("1e10"));
        Assertions.assertDoesNotThrow(() -> Quadratic.validateInput("2.5E-3"));
    }

    @Test
    public void testInputCausingOverflow() {
        assertThrows(NotEnoughPrecisionException.class, () -> {
            Quadratic.validateInput("1.2345678901234567890123456789012345678901234567890");
        });
    }


}
