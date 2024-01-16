package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Integer id;

    @Column(name = "description", length = 512, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requestor;

    @CreatedDate
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

}
