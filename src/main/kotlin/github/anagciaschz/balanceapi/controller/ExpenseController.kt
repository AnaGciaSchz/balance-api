package github.anagciaschz.balanceapi.controller

import github.anagciaschz.balanceapi.model.json.ExpensesJson
import github.anagciaschz.balanceapi.model.Expense
import github.anagciaschz.balanceapi.service.ExpenseService
import github.anagciaschz.balanceapi.service.FriendService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
@RequestMapping("api/v1/expenses")
class ExpenseController (
    private val expenseService: ExpenseService,
    private val friendService: FriendService
){

@GetMapping
fun getAllExpenses() = expenseService.getAll()

@GetMapping("/{id}")
fun getExpense(@PathVariable id: Int) = expenseService.getById(id)

@PostMapping
@ResponseStatus(HttpStatus.CREATED)
fun saveExpense(@RequestBody json: ExpensesJson): Expense {
    val friend = friendService.getById(json.friend)
    friend.positiveBalance = friend.positiveBalance + json.amount
    friendService.update(friend.id, friend)
    return expenseService.create(Expense(0,json.amount, json.description, json.timestamp, friend))
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
fun deleteExpense(@PathVariable id: Int) = expenseService.remove(id)

@PutMapping("/{id}")
fun updateExpense(
    @PathVariable id: Int, @RequestBody expense: Expense
) = expenseService.update(id, expense)
}