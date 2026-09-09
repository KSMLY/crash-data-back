package com.crashdata.back.dto;

import com.crashdata.back.code.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PersonRequest(
        @NotNull Short personNumber,
        Short occupantVehicleNumber,
        Short struckByVehicleNumber,
        LocalDate dateOfBirth,
        @NotNull Sex sex,
        @NotNull RoadUserType roadUserType,
        @NotNull SeatRow seatRow,
        @NotNull SeatPosition seatPosition,
        @NotNull InjurySeverity injurySeverity,
        @NotNull Restraint restraint,
        @NotNull Helmet helmet,
        PedManoeuvre pedManoeuvre,
        @NotNull AlcoholSuspected alcoholSuspected,
        @NotNull DrugUse drugUse,
        LicenceStatus licenceStatus,
        LocalDate licenceIssueDate,
        @Valid AlcoholTestRequest alcoholTest) {
}
