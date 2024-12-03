package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByDateRequestCreatedDesc(long requestorId);

    @Query(value = "select i from ItemRequest i where i.requestor.id <> :userId")
    Page<ItemRequest> findOtherUsersRequests(@Param("userId")long userId, Pageable pageable);
}
