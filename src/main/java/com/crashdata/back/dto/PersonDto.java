package com.crashdata.back.dto;

import com.crashdata.back.code.*;

import java.time.LocalDate;

public record PersonDto(
        Long id,
        Short personNumber,
        Long occupantVehicleId,
        Long struckByVehicleId,
        LocalDate dateOfBirth,
        Sex sex,
        RoadUserType roadUserType,
        SeatRow seatRow,
        SeatPosition seatPosition,
        InjurySeverity injurySeverity,
        Restraint restraint,
        Helmet helmet,
        PedManoeuvre pedManoeuvre,
        AlcoholSuspected alcoholSuspected,
        DrugUse drugUse,
        LicenceStatus licenceStatus,
        LocalDate licenceIssueDate,
        AlcoholTestDto alcoholTest) {
}
