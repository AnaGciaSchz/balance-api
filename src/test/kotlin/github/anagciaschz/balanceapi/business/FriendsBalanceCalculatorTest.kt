package github.anagciaschz.balanceapi.business

import github.anagciaschz.balanceapi.businness.FriendsBalanceCalculator
import github.anagciaschz.balanceapi.model.Friend
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FriendsBalanceCalculatorTest {

    @Test
    fun givenNewFriend_whenTheresOneFriendThatAlreadyPaid_TheBalanceIsCorrectlyRecalculated() {
        val friend1 = Friend(1, "Friend 1", 100.0,0.0)
        val newFriend = Friend(2, "Friend 2")
        val friends = listOf(friend1, newFriend)

        FriendsBalanceCalculator.recalculate(friends)
        assertEquals(50.00, friend1.balanceDebt)
        assertEquals(-50.00, newFriend.balanceDebt,)
    }

    @Test
    fun givenNewFriend_whenThePaidBalanceIsNotDivisibleByTheNumberOfFriends_TheBalanceIsCorrectlyTruncated() {
        val friend1 = Friend(1, "Friend 1", 10.0, 0.0)
        val newFriend1 = Friend(2, "Friend 2", )
        val newFriend2 = Friend(3, "Friend 3", )
        val friends = listOf(friend1, newFriend1, newFriend2)

        FriendsBalanceCalculator.recalculate(friends)
        assertEquals(6.67, friend1.balanceDebt)
        assertEquals(-3.33, newFriend1.balanceDebt,)
        assertEquals(-3.33, newFriend2.balanceDebt,)
    }

    @Test
    fun givenNewFriend_whenTheresFriendsWithDebts_TheBalanceIsCorrectlyRecalculates() {
        val friend1 = Friend(1, "Friend 1", 15.0,0.0)
        val friend2 = Friend(2, "Friend 2", 0.0,-7.5)
        val newFriend = Friend(3, "Friend 3")
        val friends = listOf(friend1, friend2, newFriend)

        FriendsBalanceCalculator.recalculate(friends)

        assertEquals(10.00, friend1.balanceDebt)
        assertEquals(-5.00, friend2.balanceDebt,)
        assertEquals(-5.00, newFriend.balanceDebt,)
    }

    @Test
    fun givenNewFriend_whenTheresFriendsWithDebtsButPaidSomething_TheBalanceIsCorrectlyRecalculates() {
        val friend1 = Friend(1, "Friend 1", 15.0,-10.0)
        val friend2 = Friend(2, "Friend 2", 20.0,-7.5)
        val newFriend = Friend(3, "Friend 3")
        val friends = listOf(friend1, friend2, newFriend)

        FriendsBalanceCalculator.recalculate(friends)

        assertEquals(3.34, friend1.balanceDebt)
        assertEquals(8.34, friend2.balanceDebt,)
        assertEquals(-11.66, newFriend.balanceDebt,)
    }

    @Test
    fun whenTheresOneFriendThatAlreadyPaid_WeKnowWhoItHasToPayAndTheOriginalObjectsAreNotAffected() {
        val friend1 = Friend(1, "Friend 1", 100.0,0.0, 100.0)
        val friend2 = Friend(2, "Friend 2",0.0,-50.0, -50.0)
        val friends = listOf(friend1, friend2)

        val listOfMovements = FriendsBalanceCalculator.calculateWhoToPay(friends)
        assertEquals(1,listOfMovements.size)
        assertEquals("Friend 2 -> Friend 1 (50.0€)", listOfMovements[0])
        assertEquals(friend1.positiveBalance, 100.0)
        assertEquals(friend1.negativeBalance, 0.0)
        assertEquals(friend1.balanceDebt, 100.0)
        assertEquals(friend2.positiveBalance, 0.0)
        assertEquals(friend2.negativeBalance, -50.0)
        assertEquals(friend2.balanceDebt, -50.0)


    }

    @Test
    fun whenTheresFriendsWithDebts_WeKnowWhoItHasToPay() {
        val friend1 = Friend(1, "Friend 1", 15.0,0.0, 10.0)
        val friend2 = Friend(2, "Friend 2", 0.0,-5.0, -5.0)
        val friend3 = Friend(3, "Friend 3",0.0,-5.0, -5.0 )
        val friends = listOf(friend1, friend2, friend3)

        val listOfMovements = FriendsBalanceCalculator.calculateWhoToPay(friends)
        assertEquals(2,listOfMovements.size)
        assertEquals("Friend 2 -> Friend 1 (5.0€)", listOfMovements[0])
        assertEquals("Friend 3 -> Friend 1 (5.0€)", listOfMovements[1])


    }

    @Test
    fun whenTheresFriendsWithDebtsButPaidSomething_WeKnowWhoItHasToPay() {
        val friend1 = Friend(1, "Friend 1", 12.0,-8.0, 4.0)
        val friend2 = Friend(2, "Friend 2", 20.0,-8.0, 12.0)
        val friend3 = Friend(3, "Friend 3", 0.0, -8.0, -8.00)
        val friend4 = Friend(3, "Friend 4", 0.0, -8.0, -8.00)
        val friends = listOf(friend1, friend2, friend3, friend4)

        val listOfMovements = FriendsBalanceCalculator.calculateWhoToPay(friends)
        assertEquals(3,listOfMovements.size)
        assertEquals("Friend 3 -> Friend 2 (8.0€)", listOfMovements[0])
        assertEquals("Friend 4 -> Friend 2 (4.0€)", listOfMovements[1])
        assertEquals("Friend 4 -> Friend 1 (4.0€)", listOfMovements[2])
    }
}