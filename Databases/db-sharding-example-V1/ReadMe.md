# Spring Boot Sharding Example

> 📖 Based on: [Sharding with Spring Boot](https://dev.to/rajkundalia/sharding-with-springboot-5h6l)

---

## 📘 Introduction

This project demonstrates how to implement **manual database sharding** using Spring Boot. The accompanying source code provides a working example of how requests are routed to different database shards based on simple logic.

> ⚠️ **Note**: This is a *manual sharding* approach — the application contains logic to decide which shard to route each request to.

---

## 🚀 Getting Started

### 1. Start MySQL Shards

Run the following command to start the MySQL containers:

```bash
docker compose up
```

### 2. Launch the Spring Boot Application

Use your IDE or the command line:

```bash
./mvnw spring-boot:run
```

### 3. Test the API

Send a GET request using `curl`:

```bash
curl --location 'http://localhost:8080/api/customers/2'
```

---

## ⚙️ Sharding Logic

- Requests for **even-numbered customer IDs** are routed to **Shard 1**
- Requests for **odd-numbered customer IDs** are routed to **Shard 2**

---

## 📁 Project Structure

- `src/main/java/` - Spring Boot source code
- `docker-compose.yml` - Sets up MySQL database shards
- `README.md` - Project documentation

---

## 📝 License

This project is intended for learning and experimentation. Feel free to modify and reuse it.