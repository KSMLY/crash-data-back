package com.crashdata.back.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PersonRequest(
        @NotNull Short personNumber,
        Short occupantVehicleNumber,
        Short struckByVehicleNumber,
        LocalDate dateOfBirth,
        @NotNull Short sexCode,
        @NotNull Short roadUserTypeCode,
        @NotNull Short seatRowCode,
        @NotNull Short seatPositionCode,
        @NotNull Short injurySeverityCode,
        @NotNull Short restraintCode,
        @NotNull Short helmetCode,
        Short pedManoeuvreCode,
        @NotNull Short alcoholSuspectedCode,
        @NotNull Short drugUseCode,
        Short licenceStatusCode,
        LocalDate licenceIssueDate,
        @Valid AlcoholTestRequest alcoholTest) {
}
