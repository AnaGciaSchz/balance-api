package github.anagciaschz.balanceapi.service

import github.anagciaschz.balanceapi.businness.FriendsBalanceCalculator
import github.anagciaschz.balanceapi.model.Friend
import github.anagciaschz.balanceapi.repository.FriendRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FriendService (private val repository: FriendRepository){

    fun getAll(): Iterable<Friend> = repository.findAll()

    fun getById(id: Int): Friend {
        val friendOpt = repository.findById(id)
        if (friendOpt.isPresent) {
            return friendOpt.get()
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    fun create(friend: Friend): Friend {
        val savedFriend = repository.save(friend)
        val friends = repository.findAll()
        FriendsBalanceCalculator.recalculate(friends.toList())
        friends.forEach{friend ->
            repository.save(friend)
        }
        return savedFriend;
    }

    fun remove(id: Int) {
        if (repository.existsById(id)) repository.deleteById(id)
        else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    fun update(id: Int, friend: Friend): Friend {
         if (repository.existsById(id)) {
            friend.id = id
             val updatedFriend = repository.save(friend)
             val friends = repository.findAll()
             FriendsBalanceCalculator.recalculate(friends.toList())
             friends.forEach{friend ->
                 repository.save(friend)
             }
             return updatedFriend;
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}