package com.ttu.lunchbot.spring.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.ttu.lunchbot.spring.Views;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



@Entity
@Getter
@Setter
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.NoMenuItems.class)
    private long id;

    @JsonView(Views.NoMenuItems.class)
    private String name;

    public Menu(String name, Cafe cafe) {
        this.name = name;
        this.cafe = cafe;
    }

    public Menu(String name, List<MenuItem> items) {
        this.name = name;
        this.menuItems = items;
    }

    public Menu(String name, List<MenuItem> items, Cafe cafe) {
        this.name = name;
        this.menuItems = items;
        this.cafe = cafe;
    }

    public Menu(String name, Calendar calendar, List<MenuItem> items, Cafe cafe) {
        this.name = name;
        this.menuItems = items;
        this.cafe = cafe;
        //this.date = calendarToLocalDateTime(calendar);
    }

    /**
     * Copied from https://kodejava.org/how-do-i-convert-between-old-date-and-calendar-object-with-the-new-java-8-date-time/
     * @param calendarTime
     * @return
     */
    private LocalDateTime calendarToLocalDateTime(Calendar calendarTime) {
        return LocalDateTime.ofInstant(calendarTime.toInstant(),
                ZoneId.systemDefault());
    }

    public Menu() {

    }

    //@JsonFormat(pattern = "dd.MM.yyyy")
    //private LocalDateTime date;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @OneToMany(mappedBy = "menu")
    @JsonManagedReference
    private List<MenuItem> menuItems = new ArrayList<>();


}