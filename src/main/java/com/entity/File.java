package com.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@Entity
public class File {
    @Id
    @NotNull
    Long id;
    @NotNull
    @Column(name = "name")
    String name;
}
