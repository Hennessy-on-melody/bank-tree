package com.banktree.banktree.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue
    private Long id;
    private String subject;
    private String details;
    private LocalDateTime generatedAt;

    @Enumerated(EnumType.STRING)
    private NotificationStatus confirmation;
}
