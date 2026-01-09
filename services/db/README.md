
# Ecommerce MySQL Docker Setup

Acest fișier descrie pașii pentru a construi și rula un container Docker cu MySQL pentru aplicația eCommerce.

## 1. Build imaginea Docker pentru MySQL

Rulează din folderul `db`:

```bash
docker build -f Dockerfile-mysql -t ecommerce-mysql .

```
## 2. Rulează containerul Docker
Rulează următoarea comandă din folder `db` pentru a porni un container MySQL:

```bash
docker run --name ecommerce-mysql-container -p 3307:3306 -d ecommerce-mysql
```
