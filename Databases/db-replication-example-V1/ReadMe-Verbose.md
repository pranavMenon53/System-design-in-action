# Replication demo using MySQL docker containers
Source - https://dev.to/siddhantkcode/how-to-set-up-a-mysql-master-slave-replication-in-docker-4n0a

## Steps
1. Execute - `docker-compose up -d`
2. Execute the following commands in the order specified.
    - This is configuration for the master node, keep the terminal alive, we will use it in step 4.
    - **1** - `docker exec -it mysql-master mysql -uroot -proot`
    - **2** - `CREATE USER 'replication_user'@'%' IDENTIFIED WITH 'mysql_native_password' BY 'replication_password';`
        - **What does this command do?**
        - Creates a new MySQL account
            - Username: `replication_user`
            - Host: `'%’` means “from any IP address” (wildcard).
        - Specifies the authentication plugin
            - `mysql_native_password` is MySQL’s traditional password-hashing plugin.
            - This ensures that when `replication_user` connects, MySQL will check the password using that plugin.
        - Sets the user’s password
            - user’s login password is set to `replication_password`.
            - On connection, MySQL will compare the supplied password (after hashing with `mysql_native_password`) against its stored hash.
    - **3** - `GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%';`
        - **What does this command do?**
        - Grants the `REPLICATION SLAVE` privilege
            - This is a special permission that allows the account to connect to the master and read its binary log stream.
            - It does not grant any rights to read or write application data—only to access the replication stream.
        - Applies “on .”
            - The `*.*` means **“for every database and every table.”**
            - Replication privileges aren’t scoped to particular tables, so you typically see ON `*.*`.
        - Targets the specific user
            - `TO 'replication_user'@'%'` means you’re granting this permission to that account for connections from any host (%).
    - **4** - `FLUSH PRIVILEGES;`
        - **What does this command do?**
        - It’s essentially a “refresh” of MySQL’s permissions cache.
        - The `FLUSH PRIVILEGES;` command tells the MySQL server to reload its in-memory copy of the grant tables (user accounts, passwords, and privileges) from the on-disk tables in the mysql system database.
        - Why do you often see it in replication setup guides?
            - In replication tutorials, after you manually create a replication user and grant it privileges, authors often include `FLUSH PRIVILEGES;` to ensure that:
                - Any changes you made by hand (especially if you edited system tables directly) are recognized immediately.
                - There’s no confusion later about whether the new user can actually authenticate with the privileges you intended.

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
        - **What does this command do?**
        - The `CHANGE MASTER TO …;` statement tells a slave (replica) server how to connect to its master, and where in the master’s binary log stream to begin replicating. 
        - Here’s what each clause does:
        - `MASTER_HOST='mysql-master'`
            - The hostname or network alias of the master MySQL instance.
            - In Docker Compose setups, this is often the service name `(mysql-master)`.
        - `MASTER_USER='replication_user'`
            - The account the slave will use to authenticate on the master.
            - That user must have been granted the `REPLICATION SLAVE` privilege.
        - `MASTER_PASSWORD='replication_password'`
            - The password for the replication user.
        - `MASTER_LOG_FILE='mysql-bin.000003'`
            - The name of the master’s binary-log file at the point where replication should start.
            - You get this filename from `SHOW MASTER STATUS` on the master.
        - `MASTER_LOG_POS=851`
            - The byte position within `mysql-bin.000003` where the slave should begin reading events.
            - Also obtained from `SHOW MASTER STATUS`.
        - `MASTER_PORT=3306`
            - The TCP port the master is listening on (3306 by default).
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
