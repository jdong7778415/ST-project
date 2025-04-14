package com.example.myapplication;

import com.example.myapplication.helper.DateHelper;
import com.microsoft.z3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DateHelperZ3Test {

    private final String inputDate;
    private final DateHelper.DateFormats inputFormat;
    private final DateHelper.DateFormats outputFormat;

    public DateHelperZ3Test(String inputDate, DateHelper.DateFormats inputFormat, DateHelper.DateFormats outputFormat) {
        this.inputDate = inputDate;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
    }

    @Parameterized.Parameters(name = "{index}: parse({0})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"2023-12-31", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"2023-13-31", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"2023-12-32", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"2020-02-29", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"2019-02-29", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"2023.5-12-31", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"1970-01-01", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"invalid-date", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {" 2021-12-31 ", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"2023-8-8", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
                {"23231-1-310000", DateHelper.DateFormats.D_YYYYMMDD, DateHelper.DateFormats.S_DDMMYYYY},
        });
    }

    @Test
    public void testDateParsingAgainstZ3Constraints() throws Exception {
        long timestamp = 0;
        try {
            timestamp = DateHelper.parseDate(inputDate.trim(), inputFormat);
        } catch (Exception e) {
            timestamp = 0;
        }

        boolean isExpectedValid = checkZ3Validity(inputDate);
        boolean actualValid = (timestamp != 0);

        assertEquals("Date: " + inputDate, isExpectedValid, actualValid);
    }

    private boolean checkZ3Validity(String input) {
        try (Context ctx = new Context()) {
            String[] parts = input.trim().split("-");
            if (parts.length != 3) return false;

            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            Solver solver = ctx.mkSolver();
            IntExpr y = ctx.mkIntConst("year");
            IntExpr m = ctx.mkIntConst("month");
            IntExpr d = ctx.mkIntConst("day");

            solver.add(ctx.mkEq(y, ctx.mkInt(year)));
            solver.add(ctx.mkEq(m, ctx.mkInt(month)));
            solver.add(ctx.mkEq(d, ctx.mkInt(day)));

            solver.add(ctx.mkAnd(ctx.mkGe(m, ctx.mkInt(1)), ctx.mkLe(m, ctx.mkInt(12))));
            solver.add(ctx.mkGe(d, ctx.mkInt(1)));

            BoolExpr[] monthDays = new BoolExpr[]{
                    ctx.mkImplies(ctx.mkEq(m, ctx.mkInt(2)),
                            ctx.mkOr(
                                    ctx.mkAnd(isLeapYear(ctx, y), ctx.mkLe(d, ctx.mkInt(29))),
                                    ctx.mkAnd(ctx.mkNot(isLeapYear(ctx, y)), ctx.mkLe(d, ctx.mkInt(28)))
                            )),
                    ctx.mkImplies(ctx.mkOr(ctx.mkEq(m, ctx.mkInt(4)), ctx.mkEq(m, ctx.mkInt(6)), ctx.mkEq(m, ctx.mkInt(9)), ctx.mkEq(m, ctx.mkInt(11))),
                            ctx.mkLe(d, ctx.mkInt(30))),
                    ctx.mkImplies(ctx.mkOr(ctx.mkEq(m, ctx.mkInt(1)), ctx.mkEq(m, ctx.mkInt(3)), ctx.mkEq(m, ctx.mkInt(5)), ctx.mkEq(m, ctx.mkInt(7)), ctx.mkEq(m, ctx.mkInt(8)), ctx.mkEq(m, ctx.mkInt(10)), ctx.mkEq(m, ctx.mkInt(12))),
                            ctx.mkLe(d, ctx.mkInt(31)))
            };
            solver.add(monthDays);

            return solver.check() == Status.SATISFIABLE;
        } catch (Exception e) {
            return false;
        }
    }

    private BoolExpr isLeapYear(Context ctx, IntExpr year) {
        return ctx.mkOr(
                ctx.mkAnd(ctx.mkEq(ctx.mkMod(year, ctx.mkInt(4)), ctx.mkInt(0)),
                        ctx.mkNot(ctx.mkEq(ctx.mkMod(year, ctx.mkInt(100)), ctx.mkInt(0)))),
                ctx.mkEq(ctx.mkMod(year, ctx.mkInt(400)), ctx.mkInt(0))
        );
    }
}
