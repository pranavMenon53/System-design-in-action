# MySQL Master-Slave Replication Using Docker

**Source:** [dev.to - Siddhant K Code](https://dev.to/siddhantkcode/how-to-set-up-a-mysql-master-slave-replication-in-docker-4n0a)

This guide provides step-by-step instructions to set up MySQL master-slave replication using Docker.

---

```text
Note: Steps 2,4 run in the same terminal, and steps 3,5 run in the same terminal
```

## Step 1: Start Docker Containers

```bash
docker-compose up -d
```

---

## Step 2: Configure the Master Node

Run the following commands to configure the MySQL master container:

```bash
docker exec -it mysql-master mysql -uroot -proot
```

### 1. Create Replication User

```sql
CREATE USER 'replication_user'@'%' IDENTIFIED WITH 'mysql_native_password' BY 'replication_password';
```

**Explanation:**
- Creates a MySQL user `replication_user` accessible from any host (`'%'`).
- Uses `mysql_native_password` plugin.
- Sets the password to `replication_password`.

### 2. Grant Replication Privileges

```sql
GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%';
```

**Explanation:**
- Grants `REPLICATION SLAVE` privilege on all databases and tables.
- Required for the slave to read the master's binary logs.

### 3. Apply Privilege Changes

```sql
FLUSH PRIVILEGES;
```

**Explanation:**
- Reloads MySQL’s in-memory permission cache from disk.
- Ensures that newly granted privileges take effect.

### 4. Check Master Status

```sql
SHOW MASTER STATUS;
```

**Note:** Take note of the `File` and `Position` values. These are needed for slave configuration.

---

## Step 3: Configure the Slave Node

Run the following commands to configure the MySQL slave container:

```bash
docker exec -it mysql-slave mysql -uroot -proot
```

### 1. Set Master Connection Details

Replace placeholders with values from the previous step:

```sql
CHANGE MASTER TO
    MASTER_HOST='mysql-master',
    MASTER_USER='replication_user',
    MASTER_PASSWORD='replication_password',
    MASTER_LOG_FILE='mysql-bin.000003',
    MASTER_LOG_POS=851,
    MASTER_PORT=3306;
```

**Explanation:**
- `MASTER_HOST`: Docker service name of the master container.
- `MASTER_USER`: Replication user.
- `MASTER_PASSWORD`: Password for the replication user.
- `MASTER_LOG_FILE`: Binary log file name from `SHOW MASTER STATUS`.
- `MASTER_LOG_POS`: Log position from `SHOW MASTER STATUS`.
- `MASTER_PORT`: Port number (default 3306).

### 2. Start Replication

```sql
START SLAVE;
```

### 3. Check Replication Status

```sql
SHOW SLAVE STATUS\G
```

Ensure there are no errors in the output.

---

## Step 4: Insert Data in the Master Node

Use the same terminal as step 2 or start a new terminal:

```bash
docker exec -it mysql-master mysql -uroot -proot
```

Then execute:

```sql
CREATE DATABASE shard1;
USE shard1;
CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50));
INSERT INTO users VALUES (1, 'Alice');
SELECT * FROM users;
```

---

## Step 5: Verify Data on the Slave Node

Use the same terminal as step 3 or start a new terminal:

```bash
docker exec -it mysql-slave mysql -uroot -proot
```

Then execute:

```sql
USE shard1;
SELECT * FROM users;
```

You should see the replicated data.

---

## ✅ Replication Setup Complete!
