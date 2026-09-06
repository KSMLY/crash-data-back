package com.crashdata.back.dto;

import java.time.LocalDate;

public record PersonRequest(
        Short personNumber,
        Short occupantVehicleNumber,
        Short struckByVehicleNumber,
        LocalDate dateOfBirth,
        Short sexCode,
        Short roadUserTypeCode,
        Short seatRowCode,
        Short seatPositionCode,
        Short injurySeverityCode,
        Short restraintCode,
        Short helmetCode,
        Short pedManoeuvreCode,
        Short alcoholSuspectedCode,
        Short drugUseCode,
        Short licenceStatusCode,
        LocalDate licenceIssueDate,
        AlcoholTestRequest alcoholTest) {
}
