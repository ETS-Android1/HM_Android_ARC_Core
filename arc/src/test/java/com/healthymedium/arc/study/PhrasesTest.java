package com.healthymedium.arc.study;

import com.healthymedium.arc.utilities.Phrase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PhrasesTest {

    private static final String testSpacer = "\n- - - - - - - - - -\n";

    @Test
    public void testFormatPhoneNumber() {
        String mockReceived = "+10987654321"; //number format of phone number on server
        String formatted = Phrase.formatPhoneNumber(mockReceived);

        String logMsg = String.format(
                "TEST_formatPhoneNumber\n" +
                        "\tunformatted:\t\"%s\"\n" +
                        "\tformatted:\t\t\"%s\"%s",
                mockReceived,
                formatted,
                testSpacer
        );

        System.out.print(logMsg);
    }

}