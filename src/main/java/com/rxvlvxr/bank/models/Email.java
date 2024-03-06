package com.rxvlvxr.bank.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rxvlvxr.bank.utils.ContactInfoWrapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email")
@NoArgsConstructor
@Getter
@Setter
public class Email extends ContactInfoWrapper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "address")
    private String address;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Email(String address) {
        this.address = address;
    }

    public Email(String address, LocalDateTime createdAt, User user) {
        this.address = address;
        this.createdAt = createdAt;
        this.user = user;
    }
}
