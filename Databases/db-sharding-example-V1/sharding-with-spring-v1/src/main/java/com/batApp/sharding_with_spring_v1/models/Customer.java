package com.batApp.sharding_with_spring_v1.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    @Id
    private Long id;
    private String name;
    private String email;
}

//@Repository
//public interface CustomerRepository extends JpaRepository<Customer, Long> {
//    // Regular JPA methods - ShardingSphere handles the routing
//    List<Customer> findByNameContaining(String nameFragment);
//}