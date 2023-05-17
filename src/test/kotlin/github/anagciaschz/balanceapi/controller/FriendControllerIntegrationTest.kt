package github.anagciaschz.balanceapi.controller
import com.fasterxml.jackson.databind.ObjectMapper
import github.anagciaschz.balanceapi.model.Friend
import github.anagciaschz.balanceapi.model.json.FriendsJson
import github.anagciaschz.balanceapi.service.FriendService
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FriendControllerIntegrationTest (
    @Autowired val mockMvc: MockMvc,
    @Autowired val friendService: FriendService
){

    private val objectMapper = ObjectMapper()

    private val testFriend = Friend(1, "Friend 1", 0.0, 0.0,0.0)

    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAGetRequestOfTheBaseUrl_ThenItReturnsAllFriendsItHas() {
        friendService.create(testFriend)

        mockMvc.perform(get("/api/v1/friends"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].id", equalTo(testFriend.id)))
            .andExpect(jsonPath("$[0].name", equalTo(testFriend.name)))
            .andExpect(jsonPath("$.balanceDebt", equalTo(testFriend.balanceDebt)))
            .andExpect(jsonPath("$.positiveBalance", equalTo(testFriend.positiveBalance)))
            .andExpect(jsonPath("$.negativeBalance", equalTo(testFriend.negativeBalance)))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAGetRequestOfTheBaseUrlWithAConcreteIdThatExists_ThenItReturnsTheFriendWithThatId() {
        friendService.create(testFriend)

        mockMvc.perform(get("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(testFriend.id)))
            .andExpect(jsonPath("$.name", equalTo(testFriend.name)))
            .andExpect(jsonPath("$.balanceDebt", equalTo(testFriend.balanceDebt)))
            .andExpect(jsonPath("$.positiveBalance", equalTo(testFriend.positiveBalance)))
            .andExpect(jsonPath("$.negativeBalance", equalTo(testFriend.negativeBalance)))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAGetRequestOfTheBaseUrlWithAConcreteIdThatDoesntExist_ThenItReturnsANotFoundMessage() {
        mockMvc.perform(get("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAPostRequestOfTheBaseUrlToCreateANewFriend_TheItReturnsTheSavedFriend() {
        mockMvc.perform(post("/api/v1/friends")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(FriendsJson(testFriend.name)))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id",equalTo(testFriend.id)))
            .andExpect(jsonPath("$.name", equalTo(testFriend.name)))
            .andExpect(jsonPath("$.balanceDebt", equalTo(testFriend.balanceDebt)))
            .andExpect(jsonPath("$.positiveBalance", equalTo(testFriend.positiveBalance)))
            .andExpect(jsonPath("$.negativeBalance", equalTo(testFriend.negativeBalance)))
    }
    @Test
    fun whenIDoADeleteRequestOfTheBaseUrlWithAnExistingId_ThenItDeletesTheFriend() {
        friendService.create(testFriend)

        mockMvc.perform(delete("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isNoContent)

        assertThrows<ResponseStatusException> {
            friendService.getById(testFriend.id)
        }
    }

    @Test
    fun whenIDoADeleteRequestOfTheBaseUrlWithANonExistingId_ThenItReturnsANotFoundMessage() {
        mockMvc.perform(delete("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isNotFound)
    }


    @Test
    fun whenIDoAPutRequestOfTheBaseUrlToUpdateAnExistingFriend_ThenItReturnsTheUpdatedFriend() {
        friendService.create(testFriend)
        val updatedFriend = testFriend.copy(balanceDebt = 20.0)

        mockMvc.perform(put("/api/v1/friends/${testFriend.id}")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedFriend))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(testFriend.id)))
            .andExpect(jsonPath("$.name", equalTo(testFriend.name)))
            .andExpect(jsonPath("$.balanceDebt", equalTo(testFriend.balanceDebt)))
            .andExpect(jsonPath("$.positiveBalance", equalTo(testFriend.positiveBalance)))
            .andExpect(jsonPath("$.negativeBalance", equalTo(testFriend.negativeBalance)))
    }

    @Test
    fun whenIDoAPutRequestOfTheBaseUrlToUpdateANonExistingFriend_ThenItReturnsNotFoundMessage() {
        val updatedFriend = testFriend.copy(balanceDebt = 20.0)

        mockMvc.perform(put("/api/v1/friends/${testFriend.id}")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedFriend))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

}