package github.anagciaschz.balanceapi.repository

import github.anagciaschz.balanceapi.model.Friend
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface FriendRepository : JpaRepository<Friend, Int>