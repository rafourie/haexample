package uk.me.eastmans.domain;

import javax.persistence.*;

/**
 * Created by markeastman on 23/08/2016.
 */
@Entity
@Table(name="counters")
public class Counter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private int value;

    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="current_value")
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
