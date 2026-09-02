package com.crashdata.back.dto;

public record VehicleDto(
        Long id,
        Short vehicleNumber,
        Short vehicleTypeCode,
        String make,
        String model,
        Short modelYear,
        Integer engineCc,
        Short specialFunctionCode,
        Short manoeuvreCode) {
}
