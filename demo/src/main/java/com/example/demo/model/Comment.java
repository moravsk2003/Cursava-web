package com.example.demo.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {

}
