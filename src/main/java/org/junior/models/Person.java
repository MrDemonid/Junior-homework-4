package org.junior.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personals", schema = "homework-db")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "firstname")
    private String fName;

    @Column(name = "lastname")
    private String lName;

    @Column(name = "surname")
    private String pName;

    @Column(name = "birth_of_day")
    private LocalDate bod;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Phone> phones = new ArrayList<>();
    public Person() {
//        this(null, null, null, null);
    }

    public Person(String fName, String lName, String pName, LocalDate bod) {
        this(fName, lName, pName, bod, new ArrayList<>());
    }

    public Person(String fName, String lName, String pName, LocalDate bod, List<Phone> phones) {
        this.fName = fName;
        this.lName = lName;
        this.pName = pName;
        this.bod = bod;
        this.phones = phones;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public LocalDate getBod() {
        return bod;
    }

    public void setBod(LocalDate bod) {
        this.bod = bod;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public void addPhone(Phone phone)
    {
        phone.setPerson(this);
        this.phones.add(phone);
    }


    @Override
    public String toString() {
        return String.format("[%d] %s %s.%s., %s", id, fName, lName.charAt(0), pName.charAt(0), phones);
    }
}
