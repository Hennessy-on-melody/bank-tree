package com.banktree.banktree.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String name;
    private String password;
    private String phoneNumber;
    private String emailAuthKey;


    @OneToOne
    private NotificationPermit notificationPermit;
    @OneToMany(mappedBy = "customer")
    private List<Account> accounts = new ArrayList<>();
    @OneToMany(mappedBy = "customer")
    private List<Notification> notifications;
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;



}
