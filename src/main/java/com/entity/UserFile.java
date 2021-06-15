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
@Table(name = "USER_FILES")
public class UserFile {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer fileId;
    @NotNull
    @Column(name = "name")
    String name;

    @Column(name = "isShared")
    boolean isShared;
}
