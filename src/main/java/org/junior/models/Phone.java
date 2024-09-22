package org.junior.models;

import jakarta.persistence.*;

@Entity
@Table(name = "phones", schema = "homework-db")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String numName;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;


    public Phone() {
//        this(null, null);
    }

    public Phone(String numName) {
        this(numName, null);
    }

    public Phone(String numName, Person person) {
        this.numName = numName;
        this.person = person;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumName() {
        return numName;
    }

    public void setNumName(String numName) {
        this.numName = numName;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return String.format("{%s}", numName);
    }
}
