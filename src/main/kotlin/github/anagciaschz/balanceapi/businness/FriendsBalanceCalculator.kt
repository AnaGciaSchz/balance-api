package github.anagciaschz.balanceapi.businness

import github.anagciaschz.balanceapi.model.Friend
import kotlin.math.absoluteValue

class FriendsBalanceCalculator {
    companion object {

    fun recalculate(friends:List<Friend>) {
        var totalBalance = calculateTotalBalance(friends)
        var totalDebtFromFriend = truncateResult(totalBalance / friends.size.toDouble())

        friends.forEach{ friend ->
              friend.negativeBalance = -totalDebtFromFriend
              friend.balanceDebt =friend.positiveBalance + friend.negativeBalance
        }

    }

    fun calculateWhoToPay(friends:List<Friend>) : List<String>{
        var listOfMovements = mutableListOf<String>()

        val friendsWithPositiveBalance = calculateSortedFriendsWithPositiveBalance(friends)
        val friendsWithNegativeBalance = calculateSortedFriendsWithNegativeBalance(friends)

        friendsWithNegativeBalance.forEach{ negativeFriend ->
                friendsWithPositiveBalance.forEach { positiveFriend ->
                    if (positiveFriend.balanceDebt > 0.0 && negativeFriend.balanceDebt<0) {
                        if (positiveFriend.balanceDebt >= negativeFriend.balanceDebt) {
                            var originalPositiveBalance = positiveFriend.balanceDebt

                            calculateNewBalances(positiveFriend, negativeFriend, originalPositiveBalance)

                            val difference = originalPositiveBalance-positiveFriend.balanceDebt

                            listOfMovements.add(negativeFriend.name+" -> "+positiveFriend.name+" ("+difference+"€)")
                        }
                    }
                }
        }

    return listOfMovements

    }

        private fun calculateSortedFriendsWithNegativeBalance (friends: List<Friend>) : List<Friend>{
            var friendsWithNegativeBalance = mutableListOf<Friend>()

            friends.forEach{ friend ->
                if(friend.balanceDebt<=0){
                    friendsWithNegativeBalance.add(friend.copy())
                }
            }
            return friendsWithNegativeBalance.sortedByDescending { friend -> friend.balanceDebt.absoluteValue }
        }

        private fun calculateSortedFriendsWithPositiveBalance (friends: List<Friend>) : List<Friend>{
            var friendsWithPositiveBalance = mutableListOf<Friend>()

            friends.forEach{ friend ->
                if(friend.balanceDebt>0){
                    friendsWithPositiveBalance.add(friend.copy())
                }
            }
            return friendsWithPositiveBalance.sortedByDescending { friend -> friend.balanceDebt }
        }

        private fun calculateNewBalances(positiveFriend : Friend, negativeFriend: Friend, originalPositiveBalance: Double){
            val originalNegativeBalance = negativeFriend.balanceDebt

            negativeFriend.balanceDebt = calculateNegativeFriendBalance(originalPositiveBalance, originalNegativeBalance)
            positiveFriend.balanceDebt = calculatePositiveFriendBalance(originalPositiveBalance, originalNegativeBalance)
        }

        private fun calculatePositiveFriendBalance(positiveBalance: Double, negativeBalance: Double): Double{
            val balance = positiveBalance + negativeBalance
            if(balance<=0){
                return 0.0
            }
            return balance;
        }

        private fun calculateNegativeFriendBalance(positiveBalance: Double, negativeBalance: Double): Double{
            if(positiveBalance>negativeBalance.absoluteValue){
                return 0.0
            }
                return negativeBalance + positiveBalance
        }

        private fun calculateTotalBalance(friends:List<Friend>): Double {
            var balance = 0.0
             friends.forEach{ friend ->
                 balance += friend.positiveBalance
            }
            return balance;
        }

        private fun truncateResult(result:Double): Double {
            return (result * 100).toInt() / 100.0
        }
    }
}