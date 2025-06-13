package com.batApp.sharding_with_spring_v1.services;

import org.springframework.stereotype.Service;

@Service
public class ShardingService {

    /**
     * Determines which shard contains data for a given customer ID
     *
     * @param customerId The customer ID to look up
     * @return The shard key (e.g., "shard1" or "shard2")
     */
    public String determineShardKey(Long customerId) {
        // This is a simple example using the modulo operator
        // If customerId is odd, use shard1; if even, use shard2
        return "shard" + (customerId % 2 + 1);

        // In real applications, you might use more sophisticated strategies:
        // - Hash-based: using a hash function on the ID
        // - Range-based: specific ID ranges go to specific shards
        // - Geography-based: users from different regions go to different shards
    }
}