package org.hackillinois.android.database.entity

import org.junit.Assert.*
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

    @Test
    fun completeDiet_withEmptyList_shouldBeNull() {
        val attendee = buildAttendee()

        assertNull(attendee.completeDiet)
    }

    @Test
    fun completeDiet_withOneItem_shouldBuildString() {
        val attendee = buildAttendee(
            diet = listOf("Lorem")
        )

        assertEquals("Lorem", attendee.completeDiet)
    }

    @Test
    fun completeDiet_withTwoItem_shouldBuildString() {
        val attendee = buildAttendee(
            diet = listOf("Lorem", "Ipsum")
        )

        assertEquals("Lorem and Ipsum", attendee.completeDiet)
    }

    @Test
    fun completeDiet_withThreeOrMore_shouldBuildString() {
        val attendee = buildAttendee(
            diet = listOf("Lorem", "Ipsum", "Dolor", "Sit", "Amet")
        )

        assertEquals("Lorem, Ipsum, Dolor, Sit and Amet", attendee.completeDiet)
    }

    private fun buildAttendee(
        firstName: String = "Test",
        lastName: String = "User",
        diet: List<String> = emptyList()
    ) = Attendee(
        "123",
        firstName,
        lastName,
        "test@gmail.com",
        diet,
        "Some School",
        "Some Major"
    )
}
