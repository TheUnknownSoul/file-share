package com.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "FILES")
public class File {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    int fileId;
    @NotNull
    @Column(name = "name")
    String name;

    @Column(name = "userEmail")
    String userEmail;
}
