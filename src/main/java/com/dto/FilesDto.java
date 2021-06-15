package com.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class FilesDto {

    private List<FileDto> shared;
    private List<FileDto> hidden;

}
