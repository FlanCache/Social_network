package com.example.social.repository;

import com.example.social.common.RelationStatus;
import com.example.social.entity.FriendShip;
import com.example.social.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip,Integer> {
    Optional<FriendShip> findFriendShipByUserSenderIdAndAndUserReceiverId(int idUserReceiver, int idUserSender);
    FriendShip findFriendShipByUserReceiverIdAndAndUserSenderIdAndRelationShip(int idUserReceiver, int idUserSender, RelationStatus relationship);
    Optional<FriendShip> findByUserSenderIdOrUserReceiverId(int userId,int userReceiver);
    boolean existsFriendShipByUserSenderId(int userId);
    boolean existsFriendShipByUserReceiverId(int userId);

    List<FriendShip> findByUserSenderIdOrUserReceiverIdAndRelationShip(int idUserReceiver, int idUserSender, RelationStatus relationship);
    @Query("SELECT p FROM FriendShip p WHERE ((p.userSenderId = :UserId) OR (p.userReceiverId = :UserId)) AND (p.relationShip = :relationStatus)")
    List<FriendShip> getAllFriendListByFriendShipStatus(@Param("UserId") int userId , @Param("relationStatus") RelationStatus relationStatus);
List<FriendShip> findAllByUserSenderIdOrUserReceiverIdAndRelationShip(int userSenderId, int userReceiverId, RelationStatus relationStatus);

    @Query("SELECT COUNT(p) FROM FriendShip p WHERE (p.relationCreateTime >= :oneWeekAgo) AND ((p.userSenderId = :UserId) OR (p.userReceiverId = :UserId)) AND (p.relationShip = :relationStatus)")
    int countFriendsInLastWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo, @Param("UserId") int userId , @Param("relationStatus") RelationStatus relationStatus);
}
