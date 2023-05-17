package github.anagciaschz.balanceapi.service

import github.anagciaschz.balanceapi.model.Expense
import github.anagciaschz.balanceapi.repository.ExpenseRepository
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.math.absoluteValue

@Service
class ExpenseService (private val repository: ExpenseRepository){

    fun getAll(): Iterable<Expense> {
        var expenses = repository.findAll();
        return expenses.sortedByDescending { expense -> expense.timestamp }
    }

    fun getById(id: Int): Expense {
        val expenseOpt = repository.findById(id)
        if (expenseOpt.isPresent) {
            return expenseOpt.get()
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    fun create(expense: Expense): Expense = repository.save(expense)

    fun remove(id: Int) {
        if (repository.existsById(id)) repository.deleteById(id)
        else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    fun update(id: Int, expense: Expense): Expense {
        return if (repository.existsById(id)) {
            expense.id = id
            repository.save(expense)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}