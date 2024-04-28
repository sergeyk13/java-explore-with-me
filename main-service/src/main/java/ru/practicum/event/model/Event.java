package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    private String annotation;
    @NotNull
    @ManyToOne
    private Category category;
    @Min(0)
    private long confirmedRequests;
    @NotNull
    private LocalDateTime createdOn;
    @NotBlank
    private String description;
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    @ManyToOne
    private User initiator;
    @NotNull
    @ManyToOne
    private Location location;
    @NotNull
    private boolean paid;
    @Min(0)
    private long participantLimit;
    private LocalDateTime publishedOn;
    @NotNull
    private boolean requestModeration;
    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;
    @NotBlank
    private String title;
    @Min(0)
    private long views;
    @NotNull
    private boolean available = true;
}
