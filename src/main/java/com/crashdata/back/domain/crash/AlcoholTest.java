package com.crashdata.back.domain.crash;

import com.crashdata.back.domain.code.ResultStatus;
import com.crashdata.back.domain.code.TestStatus;
import com.crashdata.back.domain.code.TestType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "alcohol_test")
public class AlcoholTest {

    @Id
    @Column(name = "person_id")
    private Long personId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "test_status_code", nullable = false)
    private TestStatus testStatus;

    @Column(name = "test_type_code", nullable = false)
    private TestType testType;

    @Column(name = "result_status_code", nullable = false)
    private ResultStatus resultStatus;

    @Column(name = "result_value", precision = 5, scale = 3)
    private BigDecimal resultValue;

    protected AlcoholTest() { }

    public AlcoholTest(Person person, TestStatus testStatus, TestType testType, ResultStatus resultStatus,
                       BigDecimal resultValue) {
        this.person = person;
        this.testStatus = testStatus;
        this.testType = testType;
        this.resultStatus = resultStatus;
        this.resultValue = resultValue;
    }

    public Long getPersonId()             { return personId; }
    public Person getPerson()             { return person; }
    public TestStatus getTestStatus()     { return testStatus; }
    public TestType getTestType()         { return testType; }
    public ResultStatus getResultStatus() { return resultStatus; }
    public BigDecimal getResultValue()    { return resultValue; }
}