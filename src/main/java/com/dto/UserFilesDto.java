package com.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserFilesDto {
    Integer fileId;
    String fileName;

}
