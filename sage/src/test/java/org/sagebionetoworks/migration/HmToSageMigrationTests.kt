package org.sagebionetoworks.migration

import com.healthymedium.arc.study.Participant
import com.healthymedium.arc.study.ParticipantState
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.sagebionetworks.migration.SecureTokenGenerator
import org.sagebionetworks.research.sagearc.SageRestApi
import java.io.IOException

@RunWith(JUnit4::class)
class HmToSageMigrationTests {

    @Test
    fun testUserNeedsToMigrate() {
        // User has not signed in yet, no migration needed
        assertFalse(SageRestApi.HmToSageMigration.userNeedsToMigrate(null, null))
        val participant = MockParticipant()
        assertFalse(SageRestApi.HmToSageMigration.userNeedsToMigrate(participant, null))
        participant.state = ParticipantState()
        assertFalse(SageRestApi.HmToSageMigration.userNeedsToMigrate(participant, null))

        // User needs to migrate when they have an Arc ID (participant ID)
        // But their external ID is null, or incorrect
        participant.state.id = "000000"
        assertTrue(SageRestApi.HmToSageMigration.userNeedsToMigrate(participant, null))
        assertTrue(SageRestApi.HmToSageMigration.userNeedsToMigrate(participant, "000001"))
        // User has already migrated
        assertFalse(SageRestApi.HmToSageMigration.userNeedsToMigrate(participant, "000000"))
    }

    @Test
    @Throws(IOException::class)
    fun test_createBridgePassword() {
        // Test 10000 bridge passwords for validity
        for (i in 0..9999) {
            val password = SecureTokenGenerator.BRIDGE_PASSWORD.nextBridgePassword()
            Assert.assertNotNull(password)
            Assert.assertEquals(9, password.length.toLong())
            assertTrue(SecureTokenGenerator.BRIDGE_PASSWORD.isValidBridgePassword(password))
        }
    }
}

open class MockParticipant: Participant() {
    override fun save() {
        // no-op, as the base class calls preferences manager which will be null in unit tests
    }
}