package uk.me.eastmans.domain;

import javax.persistence.*;

/**
 * Created by markeastman on 23/08/2016.
 */
@Table(indexes={@Index(columnList="NAME")})
@Entity
public class Counter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private int value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
