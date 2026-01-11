## Auth Service – Postman Usage Guide

- Acest document descrie modul în care pot fi utilizate endpoint-urile de autentificare expuse de Auth Service, folosind
  Postman. 

- Base URL

```aiignore
http://localhost:8088/auth
```


## Register USER 

- Full URL: http://localhost:8088/auth/registerUser ( POST method )

- Creeaza un utilizator nou în sistem.

- Request Body ( JSON ):

```json
{
  "firstName": "Razvan",
  "lastName": "Velciu",
  "email": "razvan.velciu@example.com",
  "password": "password123"
}

```


## Login USER

- Full URL: http://localhost:8088/auth/loginUser ( POST method )

- Autentifică un utilizator existent și returnează un token JWT.

- Request Body ( JSON ):

```json
{
  "email": "razvan.velciu@example.com",
  "password": "password123"
}
```