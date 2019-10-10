package org.hackillinois.android.database.entity

import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {

    @Test
    fun fullName_withoutFirstAndLastName_shouldBeAtendee() {
        val user = buildUser(
            firstName = "",
            lastName = ""
        )

        assertEquals("Attendee", user.fullName)
    }

    @Test
    fun fullName_withOnlyFirstName_shouldBeFirstName() {
        val user = buildUser(
            lastName = ""
        )

        assertEquals("Test", user.fullName)
    }

    @Test
    fun fullName_withOnlyLastName_shouldBeLastName() {
        val user = buildUser(
            firstName = ""
        )

        assertEquals("User", user.fullName)
    }

    @Test
    fun fullName_withBothNames_shouldBeTheUnionOfBoth() {
        val user = buildUser()

        assertEquals("Test User", user.fullName)
    }

    private fun buildUser(
        firstName: String = "Test",
        lastName: String = "User"
    ) = User(
        "123",
        firstName,
        lastName,
        "test@gmail.com",
        "tester"
    )
}