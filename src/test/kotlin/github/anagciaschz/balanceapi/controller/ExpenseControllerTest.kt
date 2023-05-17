package github.anagciaschz.balanceapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import github.anagciaschz.balanceapi.model.Expense
import github.anagciaschz.balanceapi.model.Friend
import github.anagciaschz.balanceapi.model.json.ExpensesJson
import github.anagciaschz.balanceapi.service.ExpenseService
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
class ExpenseControllerTest {

    private val objectMapper = ObjectMapper()

    @Mock
    private lateinit var expenseService: ExpenseService

    @Mock
    private lateinit var friendService: FriendService

    @InjectMocks
    private lateinit var expenseController: ExpenseController

    private val mockMvc: MockMvc by lazy{
        MockMvcBuilders.standaloneSetup(expenseController).build()
    }

    private val testFriend = Friend(1, "Friend 1", 0.0, 0.0,0.0)
    private val testExpense = Expense(1, 10.0, "Test expense", Date(), testFriend)

    @Test
    fun whenIDoAGetRequestOfTheBaseUrl_ThenItReturnsAllExpensesItHas() {
        val expenses = listOf(
            testExpense,
            testExpense.copy(2)
        )

        `when`(expenseService.getAll()).thenReturn(expenses)

        mockMvc.perform(get("/api/v1/expenses"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(expenses)))
    }

    @Test
    fun whenIDoAGetRequestOfTheBaseUrlWithAConcreteIdThatExists_ThenItReturnsTheExpenseWithThatId() {
        `when`(expenseService.getById(testExpense.id)).thenReturn(testExpense)

        mockMvc.perform(get("/api/v1/expenses/${testExpense.id}"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(testExpense)))
    }

    @Test
    fun whenIDoAGetRequestOfTheBaseUrlWithAConcreteIdThatDoesntExist_ThenItReturnsANotFoundMessage() {
        `when`(expenseService.getById(testExpense.id)).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc.perform(get("/api/v1/expenses/${testExpense.id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun whenIDoAPostRequestOfTheBaseUrlToCreateANewExpenseWithAnExistingFriend_TheItReturnsTheSavedExpense() {
        val json = ExpensesJson(testExpense.amount, testExpense.description, testExpense.timestamp, testFriend.id)

        `when`(friendService.getById(json.friend)).thenReturn(testFriend)
        `when`(expenseService.create(testExpense.copy(0))).thenReturn(testExpense)

        mockMvc.perform(
            post("/api/v1/expenses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json))
        )
            .andExpect(status().isCreated)
            .andExpect(content().json(objectMapper.writeValueAsString(testExpense)))
    }

    @Test
    fun whenIDoAPostRequestOfTheBaseUrlToCreateANewExpenseWithANonExistingFriend_ThenItReturnsANotFoundMessage() {
        val json = ExpensesJson(testExpense.amount, testExpense.description, testExpense.timestamp, testFriend.id)

        `when`(friendService.getById(json.friend)).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc.perform(
            post("/api/v1/expenses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun whenIDoADeleteRequestOfTheBaseUrlWithAnExistingId_ThenItDeletesTheExpense() {
        mockMvc.perform(delete("/api/v1/expenses/${testExpense.id}"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun whenIDoADeleteRequestOfTheBaseUrlWithANonExistingId_ThenItReturnsANotFoundMessage() {
        `when`(expenseService.remove(anyInt())).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc.perform(delete("/api/v1/expenses/${testExpense.id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun whenIDoAPutRequestOfTheBaseUrlToUpdateAnExistingExpense_ThenItReturnsTheUpdatedExpense() {
        `when`(expenseService.update(testExpense.id, testExpense)).thenReturn(testExpense)

        mockMvc.perform(
            put("/api/v1/expenses/${testExpense.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpense))
        )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(testExpense)))
    }

    @Test
    fun whenIDoAPutRequestOfTheBaseUrlToUpdateANonExistingExpense_ThenItReturnsNotFoundMessage() {
        `when`(expenseService.update(testExpense.id, testExpense)).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        mockMvc.perform(
            put("/api/v1/expenses/${testExpense.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpense))
        )
        .andExpect(status().isNotFound)
    }


}