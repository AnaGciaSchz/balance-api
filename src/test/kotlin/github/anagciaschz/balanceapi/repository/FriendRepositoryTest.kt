package github.anagciaschz.balanceapi.repository

import github.anagciaschz.balanceapi.model.Friend
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FriendRepositoryTest {

    @Autowired lateinit var friendRepository: FriendRepository

    private val testFriend = Friend(1, "John Doe", 0.0, 0.0,0.0)

    @BeforeEach
    fun setUp() {
        friendRepository.save(testFriend)
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIGetAllFriendsWhenThereIsNoOneInserted_TheListOfFriendsIsEmpty() {
        friendRepository.deleteAll()
        val savedFriends = friendRepository.findAll();
        assertThat(savedFriends.toList(), hasSize(0))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIGetAllFriendsWhenThereIsSomeInserted_TheListOfFriendsHasSomething() {
        val savedFriends = friendRepository.findAll();
        assertThat(savedFriends.toList(), hasSize(1))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenAFriendIsSaved_ItIsInTheFriendsTable() {
        val friend = Friend(2,"John Doe", 0.0)
        friendRepository.save(friend)
        val savedFriend = friendRepository.findAll().last()

        assertThat(savedFriend, equalTo(friend))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIAskForAFriendThatExistInTheDB_ItIsReturned() {
        val friendOpt = friendRepository.findById(1)
        assertTrue(friendOpt.isPresent)
        val friend = friendOpt.get()

        assertThat(friend, equalTo(testFriend))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIAskForAFriendThatDoesntExistInTheDB_ItReturnsOptionalObjectWithNull() {
        val friendOpt = friendRepository.findById(100)
        assertFalse(friendOpt.isPresent)
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenITryToRemoveFriendThatExistInTheDB_ItIsRemoved() {
        assertTrue(friendRepository.existsById(1))
        friendRepository.deleteById(1)
        assertFalse(friendRepository.existsById(1))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenITryToRemoveAFriendThatDoesntExistInTheDB_NothingIsRemoved() {
        val initialSize = friendRepository.findAll().toList().size
        friendRepository.deleteById(100)
        assertThat(friendRepository.findAll().toList(), hasSize(initialSize))
    }

}
