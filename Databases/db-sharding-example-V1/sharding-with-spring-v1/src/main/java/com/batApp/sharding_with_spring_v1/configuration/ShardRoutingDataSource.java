package com.batApp.sharding_with_spring_v1.configuration;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ShardRoutingDataSource extends AbstractRoutingDataSource {
    // ThreadLocal variable to store current shard for each thread/request
    private static final ThreadLocal<String> currentShard = new ThreadLocal<>();

    // Set which shard to use
    public static void setCurrentShard(String shardId) {
        currentShard.set(shardId);
    }

    // Get the current shard to use
    public static String getCurrentShard() {
        return currentShard.get();
    }

    // Clean up after we're done - important to prevent memory leaks!
    public static void clearCurrentShard() {
        currentShard.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        // This method asks our ThreadLocal which shard to use right now
        return getCurrentShard();
    }
}
