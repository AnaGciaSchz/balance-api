package github.anagciaschz.balanceapi.controller

import github.anagciaschz.balanceapi.businness.FriendsBalanceCalculator
import github.anagciaschz.balanceapi.model.Friend
import github.anagciaschz.balanceapi.service.FriendService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RequestMapping("api/v1/friends")
@CrossOrigin(origins = ["http://localhost:4200"])
@RestController
class FriendController (val service: FriendService){
@GetMapping
fun getAllFriends() = service.getAll()

@GetMapping("/payMap")
fun getFriendsMapOfPays() = FriendsBalanceCalculator.calculateWhoToPay(service.getAll().toList());

@GetMapping("/{id}")
fun getFriend(@PathVariable id: Int) = service.getById(id)

@PostMapping
@ResponseStatus(HttpStatus.CREATED)
fun saveFriend(@RequestBody friend: Friend): Friend {
    return service.create(friend)
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
fun deleteFriend(@PathVariable id: Int) = service.remove(id)

@PutMapping("/{id}")
fun updateFriend(
    @PathVariable id: Int, @RequestBody friend: Friend
) = service.update(id, friend)
}