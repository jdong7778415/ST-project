/*
package com.example.myapplication.helper

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 测试 "YYYY-MM-DD" 格式转换到 "DD/MM/YYYY" 格式的功能，
 * 全程仅调用 DateHelper 中的方法：
 * - 解析输入字符串使用 parseDate(input, DateFormats.D_YYYYMMDD)
 * - 格式化输出使用 getDesiredFormat(DateFormats.S_DDMMYYYY, timestamp)
 *
 * 覆盖范围包括：
 * 1. 标准日期转换
 * 2. Unix 纪元边界（1970-01-01）
 * 3. 闰年日期（2020-02-29）
 * 4. 无效输入（包含空字符串）
 * 5. 输入中带有多余空格的情况
 *
 * 注意：若解析失败，parseDate 方法返回 0，
 *   而 0 对应的 Unix 纪元 UTC 1970-01-01 00:00:00 转换为本地时间（例如渥太华 UTC-5）后，
 *   格式化输出可能为 "31/12/1969"。
 */
class DateHelperTest {

    /**
     * 测试标准日期转换：
     * "2021-12-31" 应转换为 "31/12/2021"
     */
    @Test
    fun testStandardConversion() {
        val input = "2023-12-31"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        assertEquals("31/12/2023", output)
    }

    /**
     * 测试月份为 13 的情况：例如 "2023-13-31"
     */
    @Test
    fun testInvalidMonth_AboveRange() {
        val input = "2023-13-31"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        // 解析失败返回 0，预计格式化后为 "31/12/1969"
        assertEquals("31/12/1969", output)
    }

    /**
     * 测试日份超过当月最大值：例如 "2023-12-32"
     */
    @Test
    fun testInvalidDay_AboveRange() {
        val input = "2023-12-32"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        // 解析失败返回 0，输出为 "31/12/1969"
        assertEquals("31/12/1969", output)
    }

    /**
     * 测试闰年 2 月份出现 29 号的情况：例如 "2020-02-29"
     */
    @Test
    fun testFebruary_1() {
        val input = "2020-02-29"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)

        assertEquals("29/02/2020", output)
    }

    /**
     * 测试平年 2 月份出现 29 号的情况：例如 "2019-02-29"
     */
    @Test
    fun testFebruary_2() {
        val input = "2019-02-29"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)

        assertEquals("31/12/1969", output)
    }

    /**
     * 测试年份部分为浮点数的情况：例如 "2023.5-12-31"
     */
    @Test
    fun testFloatingPointYear() {
        val input = "2023.5-12-31"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        // 解析失败，返回 0，输出 "31/12/1969"
        assertEquals("31/12/1969", output)
    }

    /**
     * 边界测试：Unix 纪元转换
     * 对于输入 "1970-01-01"，若系统将其解释为本地时间，则应转换为 "01/01/1970"，
     * 否则（解析失败返回 0）在UTC-5系统下可能转换为 "31/12/1969"。
     *
     * 此处预期值需要根据实际系统时区设定调整，这里假设输入解析成功为本地 1970-01-01。
     */
    @Test
    fun testEpochConversion() {
        val input = "1970-01-01"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        // 如果本地时间解析为 1970-01-01，期望输出应为 "01/01/1970"；
        // 若解析失败返回 0，则在 UTC-5 下输出 "31/12/1969"。
        // 请根据实际系统环境修改预期值，下例假设解析成功为本地时间 1970-01-01。
        assertEquals("01/01/1970", output)
    }

    /**
     * 异常输入：无效日期字符串
     * 解析失败时，parseDate 返回 0，进而格式化输出在 UTC-5 系统下可能为 "31/12/1969"
     */
    @Test
    fun testInvalidInput() {
        val input = "invalid-date"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        assertEquals("31/12/1969", output)
    }

    /**
     * 异常输入：空字符串输入
     * 同样会返回 0，从而导致输出为 "31/12/1969"
     */
    @Test
    fun testEmptyInput() {
        val input = ""
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        assertEquals("31/12/1969", output)
    }

    /**
     * 测试：输入中含有多余空格的情况
     * 如果 parseDate 方法内部未去除空格，建议在传入前 trim 输入。
     * 此处我们手动调用 trim() 后进行转换验证
     */
    @Test
    fun testInputWithExtraSpaces() {
        val input = " 2021-12-31 "
        val trimmedInput = input.trim()
        val timestamp = DateHelper.parseDate(trimmedInput, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        assertEquals("31/12/2021", output)
    }

    /**
     * 测试输入的月份不是两位数字时：
     */
    @Test
    fun testMonthAndDayNotTwoDigits() {
        val input = "2023-8-8"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        assertEquals("08/08/2023", output)
    }


    /**
     * 测试输入的年份不是四位数字时：
     * 例如 "23-12-31" 不是符合 "YYYY-MM-DD" 格式的标准输入，
     * 因此解析失败返回 0，格式化后输出为 "31/12/1969"。
     */
    @Test
    fun testInvalidButFormattedNumber() {
        val input = "23231-1-310000"
        val timestamp = DateHelper.parseDate(input, DateHelper.DateFormats.D_YYYYMMDD)
        val output = DateHelper.getDesiredFormat(DateHelper.DateFormats.S_DDMMYYYY, timestamp)
        assertEquals("31/12/1969", output)
    }
}

 */
package com.example.myapplication.helper

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * 参数化测试示例。
 *
 * 测试数据来源于CSV格式，包含如下字段：
 *  - dateStr：输入日期字符串
 *  - origFormatStr：用于解析输入字符串的格式名称（"D_YYYYMMDD" 或 "S_DDMMYYYY"）
 *  - targetFormatStr：用于格式化输出的格式名称（"D_YYYYMMDD" 或 "S_DDMMYYYY"）
 *  - expectedOutcome：如果为 "Unparseable" 表示预期解析失败（parseDate返回0），否则为转换后的预期结果
 *
 * 当 parseDate 返回 0 时，我们视实际输出为 "Unparseable"。
 */
@RunWith(Parameterized::class)
class DateHelperCombinatorialTest(
    private val dateStr: String,
    private val origFormatStr: String,
    private val targetFormatStr: String,
    private val expectedOutcome: String
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: date=\"{0}\", origFormat=\"{1}\", targetFormat=\"{2}\" -> expected={3}")
        fun data(): Collection<Array<String>> = listOf(

            arrayOf("2023-12-31", "D_YYYYMMDD", "S_DDMMYYYY", "31/12/2023"),
            arrayOf("2023-12-31", "S_DDMMYYYY", "D_YYYYMMDD", "Unparseable"),
            arrayOf("2023-12-31", "D_YYYYMMDD", "D_YYYYMMDD", "2023-12-31"),
            arrayOf("2023-12-31", "S_DDMMYYYY", "S_DDMMYYYY", "Unparseable"),

            arrayOf("2023-13-31", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2023-13-31", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2023-12-32", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2023-12-32", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2020-02-29", "D_YYYYMMDD", "S_DDMMYYYY", "29/02/2020"),
            arrayOf("2020-02-29", "D_YYYYMMDD", "S_DDMMYYYY", "29/02/2020"),
            arrayOf("2019-02-29", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2019-02-29", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2023.5-12-31", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2023.5-12-31", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("1970-01-01", "D_YYYYMMDD", "S_DDMMYYYY", "01/01/1970"),
            arrayOf("1970-01-01", "D_YYYYMMDD", "S_DDMMYYYY", "01/01/1970"),
            arrayOf("invalid-date", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("invalid-date", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2023-8-8", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("2023-8-8", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("23231-1-310000", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("23231-1-310000", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable"),
            arrayOf("", "D_YYYYMMDD", "S_DDMMYYYY", "Unparseable")
        )
    }

    // 简单正则检查：仅针对两种格式作简单验证
    private fun isValidInput(input: String, format: DateHelper.DateFormats): Boolean {
        return when (format) {
            DateHelper.DateFormats.D_YYYYMMDD -> input.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))
            DateHelper.DateFormats.S_DDMMYYYY -> input.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$"))
            else -> true
        }
    }

    private fun getDateFormat(formatStr: String): DateHelper.DateFormats {
        return when (formatStr.trim()) {
            "D_YYYYMMDD" -> DateHelper.DateFormats.D_YYYYMMDD
            "S_DDMMYYYY" -> DateHelper.DateFormats.S_DDMMYYYY
            else -> throw IllegalArgumentException("Unknown date format: $formatStr")
        }
    }

    @Test
    fun testDateConversion() {
        val origFormat = getDateFormat(origFormatStr)
        val targetFormat = getDateFormat(targetFormatStr)
        val valid = isValidInput(dateStr, origFormat)
        val timestamp = DateHelper.parseDate(dateStr, origFormat)
        // 如果解析返回0，则认为无法解析，实际输出视作 "Unparseable"
        val actualOutcome = if (timestamp == 0L) "Unparseable" else DateHelper.getDesiredFormat(targetFormat, timestamp)

        if (!valid) {
            // 输入格式不符合预期正则，期望解析失败（即 timestamp==0，实际输出 "Unparseable"）
            if (timestamp != 0L) {
                fail("Expected unparseable (timestamp==0) for input '$dateStr' with format '$origFormatStr', but got timestamp: $timestamp")
            }
            assertEquals("Failed for input '$dateStr', origFormat '$origFormatStr', targetFormat '$targetFormatStr'",
                expectedOutcome, actualOutcome)
        } else {
            // 输入格式符合预期正则
            if (timestamp == 0L && expectedOutcome == "Unparseable") {
                // 解析失败符合预期
                return
            }
            if (timestamp == 0L && expectedOutcome != "Unparseable") {
                fail("Unexpected failure for valid input '$dateStr' with format '$origFormatStr'")
            }
            // 正常转换
            assertEquals("Failed for input '$dateStr', origFormat '$origFormatStr', targetFormat '$targetFormatStr'",
                expectedOutcome, actualOutcome)
        }
    }
}
