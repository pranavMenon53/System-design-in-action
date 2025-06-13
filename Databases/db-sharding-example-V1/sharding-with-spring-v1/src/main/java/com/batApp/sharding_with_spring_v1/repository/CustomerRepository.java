package com.batApp.sharding_with_spring_v1.repository;

import com.batApp.sharding_with_spring_v1.configuration.ShardRoutingDataSource;
import com.batApp.sharding_with_spring_v1.models.Customer;
import com.batApp.sharding_with_spring_v1.services.ShardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate; // Spring's helper for database queries

    @Autowired
    private ShardingService shardingService;

    /**
     * Find a customer by their ID
     *
     * @param customerId The customer to look up
     * @return The customer information
     */
    public Customer findById(Long customerId) {
        try {
            // Step 1: Figure out which shard has this customer
            String shardKey = shardingService.determineShardKey(customerId);

            // Step 2: Tell our system to use that shard
            ShardRoutingDataSource.setCurrentShard(shardKey);

            // Step 3: Run the query (it will automatically go to the right shard)
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM customers WHERE id = ?",
                    new Object[]{customerId},
                    (resultSet, rowNum) -> {
                        Customer customer = new Customer();
                        customer.setId(resultSet.getLong("id"));
                        customer.setName(resultSet.getString("name"));
                        customer.setEmail(resultSet.getString("email"));
                        // Set other fields...
                        return customer;
                    }
            );
        } finally {
            // Step 4: Always clean up the shard selection when done
            ShardRoutingDataSource.clearCurrentShard();
        }
    }
}