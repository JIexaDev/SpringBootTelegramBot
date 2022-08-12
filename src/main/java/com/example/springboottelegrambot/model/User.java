package com.example.springboottelegrambot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@Entity(name = "usersDataTable")
public class User {
    @Id
    private Long chatId;

    @Column
    private String firstName;

    @Column
    private String userName;
}
