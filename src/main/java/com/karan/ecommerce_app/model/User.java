package com.karan.ecommerce_app.model;

import com.karan.ecommerce_app.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isActive=true;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    private byte[] profileImage;

    private String imageType;

                                    // JPA LifeCycle Methods
    @PrePersist
    public void onCreate(){
        this.createdAt=LocalDateTime.now();
        this.updatedAt= LocalDateTime.now();
        if(this.role==null){
            this.role= Role.USER;
        }
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt=LocalDateTime.now();
    }


}
