package com.likelion.culture_test.domain.survey.entity;

import com.likelion.culture_test.domain.survey.enums.Category;
import com.likelion.culture_test.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "properties")
public class Property extends BaseEntity {

  @Column(length = 150, nullable = false, unique = true)
  private String name;

  @Enumerated(EnumType.STRING)
  private Category category;

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Question> questions = new ArrayList<>();
}
