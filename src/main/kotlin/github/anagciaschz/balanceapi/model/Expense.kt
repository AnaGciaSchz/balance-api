package github.anagciaschz.balanceapi.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "expense")
data class Expense (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int = 0,
    var amount: Double = 0.0,
    var description: String = "Expense",
    @Temporal(TemporalType.TIMESTAMP) var timestamp: Date = Date(),
    @ManyToOne(cascade = [CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH,CascadeType.DETACH]) var friend : Friend = Friend()
    )