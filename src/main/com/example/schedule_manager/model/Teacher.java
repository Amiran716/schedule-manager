package main.com.example.schedule_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "teachers", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name; // Формат: "Иванов Иван Иванович"

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = normalizeName(name);
    }

    // Нормализация ФИО
    private String normalizeName(String rawName) {
        if (rawName == null) return null;

        return rawName.trim()
                .replaceAll("\\s+", " ") // Удаляет двойные пробелы
                .transform(s -> {
                    if (s.isEmpty()) return s;
                    String[] parts = s.split(" ");
                    StringBuilder result = new StringBuilder();
                    for (String part : parts) {
                        if (!part.isEmpty()) {
                            result.append(part.substring(0, 1).toUpperCase())
                                    .append(part.substring(1).toLowerCase())
                                    .append(" ");
                        }
                    }
                    return result.toString().trim();
                });
    }
}