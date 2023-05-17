package github.anagciaschz.balanceapi.service

import github.anagciaschz.balanceapi.model.Friend
import github.anagciaschz.balanceapi.repository.FriendRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.web.server.ResponseStatusException
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FriendServiceTest {

    @Mock lateinit var repository: FriendRepository

    @InjectMocks lateinit var service: FriendService

    private val testFriend = Friend(1, "John Doe", 0.0, 0.0,0.0)
    @BeforeAll
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun whenIUseTheGetAllMethod_IGetAllIndexedFriends() {
        val friends = listOf(testFriend)
        `when`(repository.findAll()).thenReturn(friends)
        val result = service.getAll()
        assertThat(result.toList(),equalTo(friends))
    }

    @Test
    fun whenIAskForTheIdOfAFriendThatExist_IGetTheFriendObject() {
        `when`(repository.findById(1)).thenReturn(Optional.of(testFriend))
        val result = service.getById(1)
        assertThat(result,equalTo(testFriend))
    }

    @Test
    fun whenIAskForTheIdOfAFriendThatDoesntExist_ItThrowsAnException() {
        `when`(repository.findById(1)).thenReturn(Optional.empty())
        assertThrows<ResponseStatusException> {
            service.getById(1)
        }
    }

    @Test
    fun whenICreateAFriend_ItIsAddedToTheFriendsTable() {
        `when`(repository.save(testFriend)).thenReturn(testFriend)
        val result = service.create(testFriend)
        assertThat(result,equalTo(testFriend))
        verify(repository, times(1)).save(testFriend)
    }

    @Test
    fun whenIRemoveAFriendThatExists_ItDoesntThrowAnException() {
        `when`(repository.existsById(1)).thenReturn(true)
        service.remove(1)
        verify(repository, times(1)).deleteById(1)
    }

    @Test
    fun whenIRemoveAFriendThaDoesnttExists_ItThrowsAnException() {
        `when`(repository.existsById(1)).thenReturn(false)
        assertThrows<ResponseStatusException> {
            service.remove(1)
        }
    }

    @Test
    fun whenIUpdateAFriendThatExists_TheUpdatedOneIsReturned() {
        val updatedFriend = testFriend.copy(0,"New Name")
        `when`(repository.existsById(1)).thenReturn(true)
        `when`(repository.save(updatedFriend)).thenReturn(updatedFriend)

        val result = service.update(1, updatedFriend)

        assertThat(result,equalTo(updatedFriend))
        verify(repository, times(1)).save(updatedFriend)
    }

    @Test
    fun whenIUpdateAFriendThatDoesntExists_ItThrownsAnException() {
        val updatedFriend = testFriend.copy(0,"New Name")
        `when`(repository.existsById(1)).thenReturn(false)
        assertThrows<ResponseStatusException> {
            service.update(1, updatedFriend)
        }
    }
}
