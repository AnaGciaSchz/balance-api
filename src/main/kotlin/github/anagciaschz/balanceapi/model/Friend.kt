package github.anagciaschz.balanceapi.model

import jakarta.persistence.*

@Entity
@Table(name = "friend")
data class Friend (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int = 0,
    var name: String = "John Doe",
    var positiveBalance: Double = 0.0,
    var negativeBalance: Double = 0.0,
    var balanceDebt: Double = 0.0
)