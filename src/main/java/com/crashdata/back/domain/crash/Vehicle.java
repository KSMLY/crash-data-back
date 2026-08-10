package com.crashdata.back.domain.crash;

import com.crashdata.back.domain.code.Manoeuvre;
import com.crashdata.back.domain.code.SpecialFunction;
import com.crashdata.back.domain.code.VehicleType;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicle",
        uniqueConstraints = @UniqueConstraint(name = "uq_vehicle_number", columnNames = {"crash_id", "vehicle_number"}))
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "crash_id", nullable = false)
    private Crash crash;

    @Column(name = "vehicle_number", nullable = false)
    private Short vehicleNumber;

    @Column(name = "vehicle_type_code", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "make", length = 50)
    private String make;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "model_year")
    private Short modelYear;

    @Column(name = "engine_cc")
    private Integer engineCc;

    @Column(name = "special_function_code", nullable = false)
    private SpecialFunction specialFunction;

    @Column(name = "manoeuvre_code", nullable = false)
    private Manoeuvre manoeuvre;

    protected Vehicle() { }

    public Vehicle(Crash crash, Short vehicleNumber, VehicleType vehicleType, String make, String model,
                   Short modelYear, Integer engineCc, SpecialFunction specialFunction, Manoeuvre manoeuvre) {
        this.crash = crash;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.engineCc = engineCc;
        this.specialFunction = specialFunction;
        this.manoeuvre = manoeuvre;
    }

    public Long getId()                         { return id; }
    public Crash getCrash()                     { return crash; }
    public Short getVehicleNumber()              { return vehicleNumber; }
    public VehicleType getVehicleType()          { return vehicleType; }
    public String getMake()                      { return make; }
    public String getModel()                     { return model; }
    public Short getModelYear()                  { return modelYear; }
    public Integer getEngineCc()                 { return engineCc; }
    public SpecialFunction getSpecialFunction()   { return specialFunction; }
    public Manoeuvre getManoeuvre()               { return manoeuvre; }
}