package com.crashdata.back.entity;

public record PersonSubmission(
        Person person,
        Short occupantVehicleNumber,
        Short struckByVehicleNumber,
        AlcoholTest alcoholTest) {
}
