package ru.practicum.shareit.item.dto;

public class ItemDto {

    private String name;
    private String description;
    private Boolean available;
    private Long aLong;

    public ItemDto(String name, String description, Boolean available, Long aLong) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.aLong = aLong;
    }

}