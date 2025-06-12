# Replication demo using MySQL docker containers
Source - https://dev.to/siddhantkcode/how-to-set-up-a-mysql-master-slave-replication-in-docker-4n0a

## Steps
1. Execute - `docker-compose up -d`
2. Execute the following commands in the order specified.
    - This is configuration for the master node, keep the terminal alive, we will use it in step 4.
    - **1** - `docker exec -it mysql-master mysql -uroot -proot`
    - **2** - `CREATE USER 'replication_user'@'%' IDENTIFIED WITH 'mysql_native_password' BY 'replication_password';`
    - **3** - `GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%';`
    - **4** - `FLUSH PRIVILEGES;`
    - **5** - `SHOW MASTER STATUS;`
    - **Important**: Note the values of `File` and `Position`. This will be required to configure the slave node.
3. Execute the following commands in the order specified.
    - This is configuration for the slave node, keep the terminal alive, we will use it in step 5.
    - **1** - `docker exec -it mysql-slave mysql -uroot -proot`
    - **2** - 
        ```SQL
            CHANGE MASTER TO
                MASTER_HOST='mysql-master',
                MASTER_USER='replication_user',
                MASTER_PASSWORD='replication_password',
                MASTER_LOG_FILE=<REPLACE_WITH_MASTER_FILE>,
                MASTER_LOG_POS=<REPLACE_WITH_MASTER_POSITION>,
                MASTER_PORT=3306;
        ```
        - Example - 
        ```SQL
            CHANGE MASTER TO
                MASTER_HOST='mysql-master',
                MASTER_USER='replication_user',
                MASTER_PASSWORD='replication_password',
                MASTER_LOG_FILE='mysql-bin.000003',
                MASTER_LOG_POS=851,
                MASTER_PORT=3306;
        ```
    - **3** - `START SLAVE;`
    - **4** - `SHOW SLAVE STATUS\G`
        - There should be no error messages.
4. Update data on master node -
    - Use the same terminal as step 2, or create a new terminal using the following command -
        - `docker exec -it mysql-master mysql -uroot -proot`
    - **1** - `CREATE DATABASE shard1;`
    - **2** - `USE shard1;`
    - **3** - `CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50));`
    - **4** - `INSERT INTO users VALUES (1, 'Alice');`
    - **5** - `SELECT * from users;`
    - With this, the master table contains data.
5. Verify the data to be present in the slave node as well.
    - Use the same terminal as step 3, or create a new terminal using the following command -
        - `docker exec -it mysql-slave mysql -uroot -proot`
    - **1** - `USE shard1;`
    - **2** - `SELECT * from users;`
    - Data should be displayed now.
