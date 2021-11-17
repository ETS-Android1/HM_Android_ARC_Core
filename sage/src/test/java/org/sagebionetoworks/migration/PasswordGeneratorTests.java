/*
 * BSD 3-Clause License
 *
 * Copyright 2021  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sagebionetoworks.migration;

import org.junit.Test;
import org.sagebionetworks.migration.PasswordGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PasswordGeneratorTests {
    @Test
    public void test_createBridgePassword() throws IOException {
        // Test 10000 bridge passwords for validity
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            counts.put(i, 0);
        }

        for (int i = 0; i < 10000; i++) {
            String password = PasswordGenerator.INSTANCE.nextPassword();
            assertNotNull(password);
            assertEquals(9, password.length());
            assertTrue(isValidBridgePassword(password));

            // Add to the counts of where each symbol is
            for (int j = 0; j < PasswordGenerator.SYMBOLIC.length(); j++) {
                int symbolIdx =  password.indexOf(PasswordGenerator.SYMBOLIC.charAt(j));
                if (symbolIdx >= 0) {
                    counts.put(symbolIdx, counts.get(symbolIdx) + 1);
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            // Make sure that the distribution has at least 1% of the distribution
            assertTrue(counts.get(i) > 10);
        }
    }

    public boolean isValidBridgePassword(String password) {
        boolean containsUppercase = false;
        boolean containsLowercase = false;
        boolean containsNumeric = false;
        boolean containsSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            String character = Character.toString(password.charAt(i));
            containsUppercase = containsUppercase || PasswordGenerator.UPPERCASE.contains(character);
            containsLowercase = containsLowercase || PasswordGenerator.LOWERCASE.contains(character);
            containsNumeric = containsNumeric || PasswordGenerator.NUMERIC.contains(character);
            containsSpecial = containsSpecial || PasswordGenerator.SYMBOLIC.contains(character);
        }

        return containsUppercase && containsLowercase && containsNumeric && containsSpecial;
    }

}
