package com.crashdata.back.dto;

import jakarta.validation.constraints.NotNull;

public record VehicleRequest(
        @NotNull Short vehicleNumber,
        @NotNull Short vehicleTypeCode,
        String make,
        String model,
        Short modelYear,
        Integer engineCc,
        @NotNull Short specialFunctionCode,
        @NotNull Short manoeuvreCode) {
}
