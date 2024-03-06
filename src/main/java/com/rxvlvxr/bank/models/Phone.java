package com.rxvlvxr.bank.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rxvlvxr.bank.utils.ContactInfoWrapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "phone")
@NoArgsConstructor
@Getter
@Setter
public class Phone extends ContactInfoWrapper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "number")
    private String number;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Phone(String number) {
        this.number = number;
    }

    public Phone(String number, LocalDateTime createdAt, User user) {
        this.number = number;
        this.createdAt = createdAt;
        this.user = user;
    }
}
