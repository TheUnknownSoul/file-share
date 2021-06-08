package com.repository;

import com.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    File findByFileId(Integer id);

    File findByName(String name);

}
