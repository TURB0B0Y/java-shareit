package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@ToString
public class Item {

    private Integer id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;

}
