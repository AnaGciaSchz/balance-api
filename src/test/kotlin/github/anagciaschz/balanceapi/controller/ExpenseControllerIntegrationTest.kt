package github.anagciaschz.balanceapi.controller
import com.fasterxml.jackson.databind.ObjectMapper
import github.anagciaschz.balanceapi.model.Expense
import github.anagciaschz.balanceapi.model.Friend
import github.anagciaschz.balanceapi.model.json.ExpensesJson
import github.anagciaschz.balanceapi.service.ExpenseService
import github.anagciaschz.balanceapi.service.FriendService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ExpenseControllerIntegrationTest (
    @Autowired val mockMvc: MockMvc,
    @Autowired val friendService: FriendService,
    @Autowired val expenseService: ExpenseService
){

    private val objectMapper = ObjectMapper()

    private val testFriend = Friend(1, "Friend 1", 0.0, 0.0, 0.0)
    private val testExpense = Expense(1, 10.0, "Test expense", Date(), testFriend)

    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAGetRequestOfTheBaseUrl_ThenItReturnsAllExpensesItHas() {
        expenseService.create(testExpense)

        mockMvc.perform(get("/api/v1/expenses"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].id", equalTo(testExpense.id)))
            .andExpect(jsonPath("$[0].amount", equalTo(testExpense.amount)))
            .andExpect(jsonPath("$[0].description", equalTo(testExpense.description)))
            .andExpect(jsonPath("$[0].friend.id", equalTo(testExpense.friend.id)))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAGetRequestOfTheBaseUrlWithAConcreteIdThatExists_ThenItReturnsTheExpenseWithThatId() {
        expenseService.create(testExpense)

        mockMvc.perform(get("/api/v1/expenses/${testExpense.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(testExpense.id)))
            .andExpect(jsonPath("$.amount", equalTo(testExpense.amount)))
            .andExpect(jsonPath("$.description", equalTo(testExpense.description)))
            .andExpect(jsonPath("$.friend.id", equalTo(testExpense.friend.id)))
    }



    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAGetRequestOfTheBaseUrlWithAConcreteIdThatDoesntExist_ThenItReturnsANotFoundMessage() {
        mockMvc.perform(get("/api/v1/expenses/${testExpense.id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAPostRequestOfTheBaseUrlToCreateANewExpenseWithAnExistingFriend_TheItReturnsTheSavedExpenseAndTheFriendIsUpdated() {
        friendService.create(testFriend)

        mockMvc.perform(post("/api/v1/expenses")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ExpensesJson(testExpense.amount, testExpense.description, testExpense.timestamp,testFriend.id)))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(testExpense.id)))
            .andExpect(jsonPath("$.amount", equalTo(testExpense.amount)))
            .andExpect(jsonPath("$.description", equalTo(testExpense.description)))
            .andExpect(jsonPath("$.friend.id", equalTo(testExpense.friend.id)))

        mockMvc.perform(get("/api/v1/friends/${testFriend.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(testFriend.id)))
            .andExpect(jsonPath("$.name", equalTo(testFriend.name)))
            .andExpect(jsonPath("$.positiveBalance", equalTo(testExpense.amount)))
            .andExpect(jsonPath("$.negativeBalance", equalTo(testFriend.negativeBalance)))
            .andExpect(jsonPath("$.balanceDebt", equalTo(testExpense.amount)))


    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIDoAPostRequestOfTheBaseUrlToCreateANewExpenseWithANonExistingFriend_ThenItReturnsANotFoundMessage() {
        mockMvc.perform(post("/api/v1/expenses")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ExpensesJson(testExpense.amount, testExpense.description, testExpense.timestamp,testFriend.id)))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun whenIDoADeleteRequestOfTheBaseUrlWithAnExistingId_ThenItDeletesTheExpenseAndNotTheFriend() {
        expenseService.create(testExpense)

        mockMvc.perform(delete("/api/v1/expenses/${testExpense.id}"))
            .andExpect(status().isNoContent)

        assertThrows<ResponseStatusException> {
            expenseService.getById(testExpense.id)
        }
        assertThat(friendService.getById(testExpense.friend.id), notNullValue())
    }

    @Test
    fun whenIDoADeleteRequestOfTheBaseUrlWithANonExistingId_ThenItReturnsANotFoundMessage() {
        mockMvc.perform(delete("/api/v1/expenses/${testFriend.id}"))
            .andExpect(status().isNotFound)
    }


    @Test
    fun whenIDoAPutRequestOfTheBaseUrlToUpdateAnExistingExpense_ThenItReturnsTheUpdatedExpense() {
        expenseService.create(testExpense)
        val updatedExpense = testExpense.copy(amount = 20.0)

        mockMvc.perform(put("/api/v1/expenses/${testExpense.id}")
            .content(objectMapper.writeValueAsString(updatedExpense))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(testExpense.id)))
            .andExpect(jsonPath("$.amount", equalTo(updatedExpense.amount)))
            .andExpect(jsonPath("$.description", equalTo(testExpense.description)))
            .andExpect(jsonPath("$.friend.id", equalTo(testExpense.friend.id)))
    }

    @Test
    fun whenIDoAPutRequestOfTheBaseUrlToUpdateANonExistingExpense_ThenItReturnsNotFoundMessage() {
        val updatedExpense = testExpense.copy(amount = 20.0)

        mockMvc.perform(put("/api/v1/expenses/${testExpense.id}")
            .content(objectMapper.writeValueAsString(updatedExpense))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

}