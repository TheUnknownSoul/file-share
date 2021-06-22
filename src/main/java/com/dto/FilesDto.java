package com.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FilesDto {
    public List<FileDto> shared;
    public List<FileDto> hidden;
}