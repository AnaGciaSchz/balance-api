package github.anagciaschz.balanceapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import github.anagciaschz.balanceapi.model.Friend
import github.anagciaschz.balanceapi.model.json.FriendsJson
import github.anagciaschz.balanceapi.service.FriendService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.server.ResponseStatusException
import java.util.*


@ExtendWith(MockitoExtension::class)
class FriendControllerUnitTest {

    private val objectMapper = ObjectMapper()

    @Mock
    private lateinit var friendService: FriendService

    @InjectMocks
    private lateinit var friendController: FriendController

    private val mockMvc: MockMvc by lazy{
        MockMvcBuilders.standaloneSetup(friendController).build()
    }

    private val testFriend = Friend(1, "Friend 1", 0.0, 0.0,0.0)

    @Test
    fun whenIDoAGetRequestOfTheBaseUrl_ThenItReturnsAllFriendsItHas() {
        val friends = listOf(
            testFriend,
            testFriend.copy(2)
        )

        `when`(friendService.getAll()).thenReturn(friends)

        mockMvc.perform(get("/api/v1/friends"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(friends)))
    }

    @Test
    fun whenIDoAGetRequestOfTheBaseUrlWithAConcreteIdThatExists_ThenItReturnsTheFriendWithThatId() {
        `when`(friendService.getById(testFriend.id)).thenReturn(testFriend)

        mockMvc.perform(get("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testFriend)))
    }

    @Test
    fun whenIDoAGetRequestOfTheBaseUrlWithAConcreteIdThatDoesntExist_ThenItReturnsANotFoundMessage() {
        `when`(friendService.getById(testFriend.id)).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc.perform(get("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun whenIDoAPostRequestOfTheBaseUrlToCreateANewFriend_TheItReturnsTheSavedFriend() {
        val json = FriendsJson(testFriend.name)

        `when`(friendService.create(testFriend.copy(0))).thenReturn(testFriend)

        mockMvc.perform(
            post("/api/v1/friends")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json))
        )
            .andExpect(status().isCreated)
            .andExpect(content().json(objectMapper.writeValueAsString(testFriend)))
    }

    @Test
    fun whenIDoADeleteRequestOfTheBaseUrlWithAnExistingId_ThenItDeletesTheFriend() {
        mockMvc.perform(delete("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun whenIDoADeleteRequestOfTheBaseUrlWithANonExistingId_ThenItReturnsANotFoundMessage() {
        `when`(friendService.remove(anyInt())).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc.perform(delete("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun whenIDoAPutRequestOfTheBaseUrlToUpdateAnExistingFriend_ThenItReturnsTheUpdatedFriend() {
        `when`(friendService.update(testFriend.id, testFriend)).thenReturn(testFriend)

        mockMvc.perform(
            put("/api/v1/friends/${testFriend.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFriend))
        )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(testFriend)))
    }

    @Test
    fun whenIDoAPutRequestOfTheBaseUrlToUpdateANonExistingFriend_ThenItReturnsNotFoundMessage() {
        `when`(friendService.update(testFriend.id, testFriend)).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc.perform(
            put("/api/v1/friends/${testFriend.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFriend))
        )
        .andExpect(status().isNotFound)
    }


}