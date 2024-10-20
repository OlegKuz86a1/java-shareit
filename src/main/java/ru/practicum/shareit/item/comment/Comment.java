package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.common.BaseEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "comment.item", attributeNodes = {
        @NamedAttributeNode("item")
})
@NamedEntityGraph(name = "comment.author", attributeNodes = {
        @NamedAttributeNode("author")
})
@NamedEntityGraph(name = "comment.full", attributeNodes = {
        @NamedAttributeNode("item"), @NamedAttributeNode("author")
})
public class Comment extends BaseEntity {

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd, hh:mm:ss")
    private LocalDateTime created;
}
