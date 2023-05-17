package github.anagciaschz.balanceapi.repository

import github.anagciaschz.balanceapi.model.Expense
import github.anagciaschz.balanceapi.model.Friend
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExpenseRepositoryTest {

    @Autowired lateinit var expenseRepository: ExpenseRepository

    private val testFriend = Friend(1, "John Doe", 0.0, 0.0,0.0)
    private val testExpense = Expense(1, 10.0, "Description", getTestDate(), testFriend)

    @BeforeEach
    fun setUp() {
        expenseRepository.save(testExpense)
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIGetAllExpensesWhenThereIsNoOneInserted_TheListOfExpensesIsEmpty() {
        expenseRepository.deleteAll()
        val savedExpenses = expenseRepository.findAll();
        assertThat(savedExpenses.toList(), hasSize(0))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIGetAllExpensesWhenThereIsSomeInserted_TheListOfExpensesHasSomething() {
        val savedExpenses = expenseRepository.findAll();
        assertThat(savedExpenses.toList(), hasSize(1))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenAnExpenseIsSaved_ItIsInTheExpensesTable() {
        val friend = Friend(2,"John Doe", 0.0)
        val expense = Expense(2, 10.0, "Descripcion", getTestDate(), friend)
        expenseRepository.save(expense)
        val savedExpense = expenseRepository.findAll().last()

        assertThat(savedExpense.id, equalTo(expense.id))
        assertThat(savedExpense.amount, equalTo(expense.amount))
        assertThat(savedExpense.description, equalTo(expense.description))
        assertThat(savedExpense.friend.id, equalTo(expense.friend.id))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIAskForAnExpenseThatExistInTheDB_ItIsReturned() {
        val expenseOpt = expenseRepository.findById(1)
        assertTrue(expenseOpt.isPresent)
        val expense = expenseOpt.get()

        assertThat(testExpense.id, equalTo(expense.id))
        assertThat(testExpense.amount, equalTo(expense.amount))
        assertThat(testExpense.description, equalTo(expense.description))
        assertThat(testExpense.friend.id, equalTo(expense.friend.id))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenIAskForAnExpenseThatDoesntExistInTheDB_ItReturnsOptionalObjectWithNull() {
        val expenseOpt = expenseRepository.findById(100)
        assertFalse(expenseOpt.isPresent)
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenITryToRemoveExpenseThatExistInTheDB_ItIsRemoved() {
        assertTrue(expenseRepository.existsById(1))
        expenseRepository.deleteById(1)
        assertFalse(expenseRepository.existsById(1))
    }

    @Test
    @Transactional
    @DirtiesContext
    fun whenITryToRemoveAnExpenseThatDoesntExistInTheDB_NothingIsRemoved() {
        val initialSize = expenseRepository.findAll().toList().size
        expenseRepository.deleteById(100)
        assertThat(expenseRepository.findAll().toList(), hasSize(initialSize))
    }

    private fun getTestDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2023)
        calendar.set(Calendar.MONTH, Calendar.MAY)
        calendar.set(Calendar.DAY_OF_MONTH, 7)
        calendar.set(Calendar.HOUR,10)
        calendar.set(Calendar.MINUTE,50)
        calendar.set(Calendar.SECOND,10)
        calendar.set(Calendar.MILLISECOND,111)

        return calendar.time
    }

}
