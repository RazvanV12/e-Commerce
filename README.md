# e-Commerce – Cloud Native Microservices Application

Acest proiect reprezintă o aplicație cloud-native bazată pe microservicii,
dezvoltată în cadrul laboratorului de Cloud Computing.

## Arhitectură
Aplicația este compusă din următoarele microservicii:
- auth-service – autentificare și autorizare utilizatori
- api-service – logică de business (produse, comenzi, coș)
- db – MySQL (stocare persistentă)
- adminer – interfață de administrare DB
- portainer – interfață de administrare cluster/containerelor

---

## Cerințe
- Docker
- Kubernetes (Minikube / Kind)
- Helm

---

## Build imagini Docker

### Auth Service
```bash
docker build -t ecommerce-auth:local services/auth-service
```
### Auth API
```bash
docker build -t ecommerce-api:local services/api-service
```

### Verificare imagini 
```bash
docker images | grep ecommerce
```
NodePort vs ClusterIP 
MySQL: ClusterIP (intern) 
API/Auth: NodePort (ca să le testezi din Postman) 

### Creare fisiere YAML, pentru a avea datele persistente, avem nevoie de un PV și de un PV, apoi aplicarea lor

```bash
kubectl apply -f kubernetes/mysql-pv.yaml
kubectl apply -f kubernetes/mysql-pvc.yaml
```

prima comanda:
Kubernetes înregistrează un spațiu de stocare
disponibil pentru aplicații
la nivel de cluster.

### Verificare spatiu de stocare disponibil la nivel de cluster
```bash
kubectl get pv
kubectl get pvc 
```

kubectl apply spune clusterului Kubernetes să creeze sau să actualizeze resursele descrise declarativ într-un fișier YAML.
kubectl apply spune clusterului Kubernetes să creeze sau să actualizeze resursele descrise declarativ într-un fișier YAML.

```karlaniculae@Alexes-MacBook-Air e-Commerce % kubectl get pv

NAME       CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                 STORAGECLASS   VOLUMEATTRIBUTESCLASS   REASON   AGE
mysql-pv   1Gi        RWO            Retain           Bound    ecommerce/mysql-pvc   manual         <unset>                          2m5s

karlaniculae@Alexes-MacBook-Air e-Commerce % kubectl get pvc -n ecommerce

NAME        STATUS   VOLUME     CAPACITY   ACCESS MODES   STORAGECLASS   VOLUMEATTRIBUTESCLASS   AGE
mysql-pvc   Bound    mysql-pv   1Gi        RWO            manual         <unset>                 3m34s

```