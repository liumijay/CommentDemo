package com.example.comment;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        Pattern pattern = Pattern.compile("[0-9]*");
         boolean isMatch =pattern.matcher("7821123134564").matches();
         System.out.println(isMatch);
         assert isMatch;
    }
}