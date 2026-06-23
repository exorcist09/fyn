# FYN – Backend Repository
### Fyn - Frontend Respository: [Frontend_Github_Link](https://github.com/exorcist09/fyn-frontend.git)

FYN is a full-stack Money Manager application that helps users track their income, expenses, and overall financial health in a secure and intuitive interface.

---

## Technologies Used

**Backend :** Spring Boot, Spring Security, JWT, bcrypt, JPA, Hibernate, MySQL (Development), PostgreSQL (Production), Docker  


## Local Setup 

### Prerequisites
- Docker & Docker Compose installed

### Steps

0. **Combination**

-  The applicaiton is in two differnt repository you need to clone both or Zip download both and then merge them and proceed


1. **Clone the repository**
```bash
git clone https://github.com/exorcist09/fyn.git
cd fyn
```
2. **Run Docker Compose**
```bash
docker-compose up --build
This will spin up both backend and frontend containers along with the database.
```

3. **Access the app**
```bash
Frontend: http://localhost:3000
Backend API: http://localhost:8080
```
