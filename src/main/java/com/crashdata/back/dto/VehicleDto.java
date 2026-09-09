package com.crashdata.back.dto;

import com.crashdata.back.code.Manoeuvre;
import com.crashdata.back.code.SpecialFunction;
import com.crashdata.back.code.VehicleType;

public record VehicleDto(
        Long id,
        Short vehicleNumber,
        VehicleType vehicleType,
        String make,
        String model,
        Short modelYear,
        Integer engineCc,
        SpecialFunction specialFunction,
        Manoeuvre manoeuvre) {
}
