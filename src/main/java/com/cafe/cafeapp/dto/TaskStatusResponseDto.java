package com.cafe.cafeapp.dto;

import com.cafe.cafeapp.enums.StatusTask;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStatusResponseDto {
    private Long taskId;
    private StatusTask status;
}
