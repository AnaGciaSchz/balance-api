package github.anagciaschz.balanceapi.repository

import github.anagciaschz.balanceapi.model.Expense
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface ExpenseRepository : CrudRepository<Expense, Int>