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
public class File {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    @Column(name = "name")
    String name;
}
