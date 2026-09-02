package com.crashdata.back.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CrashDetailDto {

    @JsonUnwrapped
    private final CrashDto crash;

    private final List<VehicleDto> vehicles;

    private final List<PersonDto> persons;
}
