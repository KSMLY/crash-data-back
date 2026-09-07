package com.crashdata.back.entity;

import com.crashdata.back.code.Manoeuvre;
import com.crashdata.back.code.SpecialFunction;
import com.crashdata.back.code.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Vehicle {
    private Long id;
    private Long crashId;
    private Short vehicleNumber;
    private VehicleType vehicleType;
    private String make;
    private String model;
    private Short modelYear;
    private Integer engineCc;
    private SpecialFunction specialFunction;
    private Manoeuvre manoeuvre;


    public Vehicle withCrashId(Long crashId) {
        return new Vehicle(id, crashId, vehicleNumber, vehicleType, make, model,
                modelYear, engineCc, specialFunction, manoeuvre);
    }
}
