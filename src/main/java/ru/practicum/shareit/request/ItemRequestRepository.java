package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester_IdNotLikeOrderByCreatedDesc(Long userId, Pageable pageable);

    List<ItemRequest> findAllByRequester_Id(Long userId);

    ItemRequest findFirstById(Long requestId);
}
