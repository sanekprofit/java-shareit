package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String textName,
                                                                                                 String textDescription,
                                                                                                 Pageable pageable
    );

    List<Item> findAllByOwner_Id(Long userId, Pageable pageable);

    List<Item> findAllByRequest_Id(Long requestId);

    Item findFirstByRequest_Id(Long requestId);
}