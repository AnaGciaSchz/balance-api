package github.anagciaschz.balanceapi.service

import github.anagciaschz.balanceapi.model.Expense
import github.anagciaschz.balanceapi.model.Friend
import github.anagciaschz.balanceapi.repository.ExpenseRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.web.server.ResponseStatusException
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseServiceTest {

    @Mock lateinit var repository: ExpenseRepository

    @InjectMocks lateinit var service: ExpenseService

    private val testExpense = Expense(1, 10.0, "Test expense", Date(), Friend())

    @BeforeAll
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun whenIUseTheGetAllMethod_IGetAllIndexedExpenses() {
        val expenses = listOf(testExpense)
        `when`(repository.findAll()).thenReturn(expenses)
        val result = service.getAll()
        assertThat(result.toList(), equalTo(expenses))
    }

    @Test
    fun whenIAskForTheIdOfAnExpenseThatExist_IGetTheExpenseObject() {
        `when`(repository.findById(1)).thenReturn(Optional.of(testExpense))
        val result = service.getById(1)
        assertThat(result, equalTo(testExpense))
    }

    @Test
    fun whenIAskForTheIdOfAnExpenseThatDoesntExist_ItThrowsAnException() {
        `when`(repository.findById(1)).thenReturn(Optional.empty())
        assertThrows<ResponseStatusException> {
            service.getById(1)
        }
    }

    @Test
    fun whenICreateAnExpense_ItIsAddedToTheExpensesTable() {
        `when`(repository.save(testExpense)).thenReturn(testExpense)
        val result = service.create(testExpense)
        assertThat(result, equalTo(testExpense))
        verify(repository, times(1)).save(testExpense)
    }

    @Test
    fun whenIRemoveAnExpenseThatExists_ItDoesntThrowAnException() {
        `when`(repository.existsById(1)).thenReturn(true)
        service.remove(1)
        verify(repository, times(1)).deleteById(1)
    }

    @Test
    fun whenIRemoveAnExpenseThaDoesnttExists_ItThrowsAnException() {
        `when`(repository.existsById(1)).thenReturn(false)
        assertThrows<ResponseStatusException> {
            service.remove(1)
        }
    }

    @Test
    fun whenIUpdateAnExpenseThatExists_TheUpdatedOneIsReturned() {
        val updatedExpense = testExpense.copy(amount =20.0)
        `when`(repository.existsById(1)).thenReturn(true)
        `when`(repository.save(updatedExpense)).thenReturn(updatedExpense)

        val result = service.update(1, updatedExpense)

        assertThat(result, equalTo(updatedExpense))
        verify(repository, times(1)).save(updatedExpense)
    }

    @Test
    fun whenIUpdateAnExpenseThatDoesntExists_ItThrownsAnException() {
        val updatedExpense = testExpense.copy(amount = 20.0)
        `when`(repository.existsById(1)).thenReturn(false)
        assertThrows<ResponseStatusException> {
            service.update(1, updatedExpense)
        }
    }
}
