
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

## 3. Creare cluster local folosind Kind

```bash
kind create cluster --name ecommerce
```
kind este un utilitar care creează un cluster Kubernetes local folosind Docker, iar kubectl este tool-ul CLI prin care administrăm resursele din cluster, precum pods, services și deployments.

## 4. Verificam daca s-a creat cluster-ul

```bash
kubectl get nodes
```
Un nod este o masina din cluster. In Kind acesta este un container Docker.
Are rol control-plane.
``` 
NAME                      STATUS   ROLES           AGE   VERSION
ecommerce-control-plane   Ready    control-plane   44s   v1.35.0
```
READY inseamna ca cluster-ul este functional.
## 5. Creare namespace

```bash
kubectl create namespace ecommerce
kubectl config set-context --current --namespace=ecommerce
```
## 5. Verificare namespace

```bash
kubectl get ns
```

set-context face ca toate comenzile kubectl sa fie directionate catre clusterul ecommerce.
Nu mai este nevoie sa punem -n ecommerce atunci cand dam comenzile, pentru ca vor pointa automat catre acest cluster.

Un namespace NU conține fișiere, ci resurse Kubernetes.
Într-un namespace pot exista:
Pods
Deployments
Services
PersistentVolumeClaims
ConfigMaps
Secrets

Dacă nu creezi namespace-ul tău:
➡ TOTUL ajunge în default

doar ca default detine si chestii care nu tin de aplicatia mea

Ce este kube-system?

Namespace INTERN Kubernetes
Conține:
DNS (CoreDNS)
controller-e
scheduler
kube-proxy

Ce este kube-public?

namespace special
conține informații publice despre cluster
foarte rar folosit de aplicații

Ce este default?

namespace-ul implicit
unde ajunge tot dacă NU specifici alt namespace

Pentru a porni un pod de MySQL, avem nevoie de a seta variabilele MYSQL_USER și MYSQL_PASSWORD, cu configMap si secret pentru parola.

## Se creeaza yaml-urile mysql-secret.yaml si mysql-configmap.yaml

Când se va crea deployment-ul, o să folosim acest ConfigMap pentru a injecta variabilele de mediu.

apoi

```bash
kubectl apply -f kubernetes/mysql-configmap.yaml
kubectl apply -f kubernetes/mysql-secret.yaml
kubectl apply -f kubernetes/mysql-init-configmap.yaml
kubectl apply -f kubernetes/mysql-deployment.yaml
kubectl apply -f kubernetes/mysql-service.yaml
```

mysql-configmap.yaml
→ definește variabile non-sensibile (user MySQL)
mysql-secret.yaml
→ definește parolele MySQL (root + user)
mysql-init-configmap.yaml
→ conține scriptul SQL care creează bazele de date ecommerce_api și ecommerce_auth
mysql-deployment.yaml
→ pornește MySQL în Kubernetes și montează volumele (PVC + init SQL)
mysql-service.yaml
→ expune MySQL intern în cluster printr-un Service de tip ClusterIP

```bash
kubectl get pods -n ecommerce
kubectl get svc -n ecommerce
kubectl logs -n ecommerce -l app=mysql --tail=50
```