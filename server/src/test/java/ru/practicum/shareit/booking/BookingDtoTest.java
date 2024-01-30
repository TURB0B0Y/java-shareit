package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        UserDto user = new UserDto(1, "test", "test@example.com");
        user.setName("testNameUser");
        user.setEmail("test@mail.com");
        user.setId(1);

        ItemDto item = ItemDto.builder()
                .id(1)
                .name("testItem")
                .description("test")
                .available(true)
                .build();

        item.setName("testItem");
        item.setDescription("5");
        item.setAvailable(true);
        item.setId(1);

        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .item(item)
                .start(LocalDateTime.of(2022, 1, 15, 10, 11))
                .end(LocalDateTime.of(2022, 3, 15, 10, 11))
                .build();
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(bookingDto.getId()));
        assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo(bookingDto.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().toString() + ":00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().toString() + ":00");
    }
}