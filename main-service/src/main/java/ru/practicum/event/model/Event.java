package ru.practicum.event.model;

import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.comment.model.Comment;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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
    @ManyToOne
    private Compilation compilation;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Comment> comments;

}
