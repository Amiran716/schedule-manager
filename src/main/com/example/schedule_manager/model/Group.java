package main.com.example.schedule_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "groups", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "course"}))
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Integer course;

    @Column(name = "is_subgroup")
    private boolean subgroup;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCourse() { return course; }
    public void setCourse(Integer course) { this.course = course; }

    public boolean isSubgroup() { return subgroup; }
    public void setSubgroup(boolean subgroup) { this.subgroup = subgroup; }
}