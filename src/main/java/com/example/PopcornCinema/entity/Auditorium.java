package com.example.PopcornCinema.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "auditoriums")
public class Auditorium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "total_columns")
    private Integer totalColumns;

    @Column(name = "seat_capacity")
    private Integer seatCapacity;

    // ====================
    // RELATION
    // ====================
    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    // ====================
    // GETTER SETTER
    // ====================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns(Integer totalColumns) {
        this.totalColumns = totalColumns;
    }

    public Integer getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(Integer seatCapacity) {
        this.seatCapacity = seatCapacity;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}