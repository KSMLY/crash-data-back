package com.crashdata.back.dto;

import com.crashdata.back.code.Manoeuvre;
import com.crashdata.back.code.SpecialFunction;
import com.crashdata.back.code.VehicleType;
import jakarta.validation.constraints.NotNull;

public record VehicleRequest(
        @NotNull Short vehicleNumber,
        @NotNull VehicleType vehicleType,
        String make,
        String model,
        Short modelYear,
        Integer engineCc,
        @NotNull SpecialFunction specialFunction,
        @NotNull Manoeuvre manoeuvre) {
}
