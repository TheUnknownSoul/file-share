package com.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto {
    private Integer fileId;
    private String fileName;
    private boolean isShared;
}
