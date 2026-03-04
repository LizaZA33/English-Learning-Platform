package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User{
}
