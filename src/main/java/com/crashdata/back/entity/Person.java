package com.crashdata.back.entity;

import com.crashdata.back.code.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Person {

    private Long id;
    private Long crashId;
    private Short personNumber;
    private Long occupantVehicleId;
    private Long struckByVehicleId;
    private LocalDate dateOfBirth;
    private Sex sex;
    private RoadUserType roadUserType;
    private SeatRow seatRow;
    private SeatPosition seatPosition;
    private InjurySeverity injurySeverity;
    private Restraint restraint;
    private Helmet helmet;
    private PedManoeuvre pedManoeuvre;
    private AlcoholSuspected alcoholSuspected;
    private DrugUse drugUse;
    private LicenceStatus licenceStatus;
    private LocalDate licenceIssueDate;

}
