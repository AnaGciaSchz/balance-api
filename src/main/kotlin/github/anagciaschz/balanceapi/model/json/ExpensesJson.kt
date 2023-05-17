package github.anagciaschz.balanceapi.model.json

import java.util.*
data class ExpensesJson (
    val amount: Double = 0.0,
    val description: String = "Expense",
    val timestamp: Date = Date(),
    val friend : Int = 0
)