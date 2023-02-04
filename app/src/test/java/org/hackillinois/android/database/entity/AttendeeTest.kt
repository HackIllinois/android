package org.hackillinois.android.database.entity

import org.junit.Assert.assertEquals
import org.junit.Test

class AttendeeTest {

    @Test
    fun fullName_withoutFirstAndLastName_shouldBeAtendee() {
        val attendee = buildAttendee(
            firstName = "",
            lastName = ""
        )

        assertEquals("Attendee", attendee.fullName)
    }

    @Test
    fun fullName_withOnlyFirstName_shouldBeFirstName() {
        val attendee = buildAttendee(
            lastName = ""
        )

        assertEquals("Test", attendee.fullName)
    }

    @Test
    fun fullName_withOnlyLastName_shouldBeLastName() {
        val attendee = buildAttendee(
            firstName = ""
        )

        assertEquals("User", attendee.fullName)
    }

    @Test
    fun fullName_withBothNames_shouldBeTheUnionOfBoth() {
        val attendee = buildAttendee()

        assertEquals("Test User", attendee.fullName)
    }

    private fun buildAttendee(
        firstName: String = "Test",
        lastName: String = "User"
    ) = Attendee(
        "123",
        firstName,
        lastName
    )
}
