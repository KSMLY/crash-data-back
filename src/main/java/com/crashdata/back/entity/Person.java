package com.crashdata.back.entity;

import com.crashdata.back.code.*;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "person",
        uniqueConstraints = @UniqueConstraint(name = "uq_person_number", columnNames = {"crash_id", "person_number"}))
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "crash_id", nullable = false)
    private Crash crash;

    @Column(name = "person_number", nullable = false)
    private Short personNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occupant_vehicle_id")
    private Vehicle occupantVehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "struck_by_vehicle_id")
    private Vehicle struckByVehicle;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "sex_code", nullable = false)
    private Sex sex;

    @Column(name = "road_user_type_code", nullable = false)
    private RoadUserType roadUserType;

    @Column(name = "seat_row_code", nullable = false)
    private SeatRow seatRow;

    @Column(name = "seat_position_code", nullable = false)
    private SeatPosition seatPosition;

    @Column(name = "injury_severity_code", nullable = false)
    private InjurySeverity injurySeverity;

    @Column(name = "restraint_code", nullable = false)
    private Restraint restraint;

    @Column(name = "helmet_code", nullable = false)
    private Helmet helmet;

    @Column(name = "ped_manoeuvre_code")
    private PedManoeuvre pedManoeuvre;

    @Column(name = "alcohol_suspected_code", nullable = false)
    private AlcoholSuspected alcoholSuspected;

    @Column(name = "drug_use_code", nullable = false)
    private DrugUse drugUse;

    @Column(name = "licence_status_code")
    private LicenceStatus licenceStatus;

    @Column(name = "licence_issue_date")
    private LocalDate licenceIssueDate;

    protected Person() { }

    public Person(Crash crash, Short personNumber, Vehicle occupantVehicle, Vehicle struckByVehicle,
                  LocalDate dateOfBirth, Sex sex, RoadUserType roadUserType, SeatRow seatRow,
                  SeatPosition seatPosition, InjurySeverity injurySeverity, Restraint restraint, Helmet helmet,
                  PedManoeuvre pedManoeuvre, AlcoholSuspected alcoholSuspected, DrugUse drugUse,
                  LicenceStatus licenceStatus, LocalDate licenceIssueDate) {
        this.crash = crash;
        this.personNumber = personNumber;
        this.occupantVehicle = occupantVehicle;
        this.struckByVehicle = struckByVehicle;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.roadUserType = roadUserType;
        this.seatRow = seatRow;
        this.seatPosition = seatPosition;
        this.injurySeverity = injurySeverity;
        this.restraint = restraint;
        this.helmet = helmet;
        this.pedManoeuvre = pedManoeuvre;
        this.alcoholSuspected = alcoholSuspected;
        this.drugUse = drugUse;
        this.licenceStatus = licenceStatus;
        this.licenceIssueDate = licenceIssueDate;
    }

    public Long getId()                               { return id; }
    public Crash getCrash()                           { return crash; }
    public Short getPersonNumber()                    { return personNumber; }
    public Vehicle getOccupantVehicle()               { return occupantVehicle; }
    public Vehicle getStruckByVehicle()               { return struckByVehicle; }
    public LocalDate getDateOfBirth()                 { return dateOfBirth; }
    public Sex getSex()                               { return sex; }
    public RoadUserType getRoadUserType()             { return roadUserType; }
    public SeatRow getSeatRow()                       { return seatRow; }
    public SeatPosition getSeatPosition()             { return seatPosition; }
    public InjurySeverity getInjurySeverity()         { return injurySeverity; }
    public Restraint getRestraint()                   { return restraint; }
    public Helmet getHelmet()                         { return helmet; }
    public PedManoeuvre getPedManoeuvre()             { return pedManoeuvre; }
    public AlcoholSuspected getAlcoholSuspected()     { return alcoholSuspected; }
    public DrugUse getDrugUse()                       { return drugUse; }
    public LicenceStatus getLicenceStatus()           { return licenceStatus; }
    public LocalDate getLicenceIssueDate()            { return licenceIssueDate; }
}