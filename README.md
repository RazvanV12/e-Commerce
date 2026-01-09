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
### Business API
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




## 3. Creare cluster local folosind Kind

```bash
kind create cluster --name ecommerce --image kindest/node:v1.34.0
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

### Verificare bază de date MySQL

Pentru a verifica faptul că MySQL rulează corect și că bazele de date au fost create automat,
am rulat următoarea comandă:

```bash
kubectl exec -n ecommerce -it deploy/mysql -- \
  mysql -uroot -p'rootpass' -e "SHOW DATABASES;"
```

### Verificare bază de date MySQL

Pentru a verifica faptul că MySQL rulează corect și că bazele de date au fost create automat,
am rulat următoarea comandă:

```bash
kubectl exec -n ecommerce -it deploy/mysql -- \
  mysql -uroot -p'rootpass' -e "SHOW DATABASES;"
```

Configurația bazei de date pentru auth-service este injectată prin variabile de mediu
definite în Kubernetes (Deployment), conform principiilor cloud-native.

Setările din application.properties sunt suprascrise la rularea în cluster.

dupa modif din application.properties
```bash
docker build -t ecommerce-auth:local services/auth-service
```

se incarca in kind
```bash
kind load docker-image ecommerce-auth:local --name ecommerce
```
restart deployment
```bash
kubectl rollout restart deployment/auth-service -n ecommerce

```


deci dupa creare yaml pt api si auth

```bash
# build
docker build -t ecommerce-auth:local services/auth-service
docker build -t ecommerce-api:local  services/api-service

# load in kind
kind load docker-image ecommerce-auth:local --name ecommerce
kind load docker-image ecommerce-api:local  --name ecommerce

# apply k8s
kubectl apply -f kubernetes/auth-deployment.yaml
kubectl apply -f kubernetes/api-deployment.yaml

# verify
kubectl get pods -n ecommerce
kubectl get svc -n ecommerce
```

OUTPUT
```bash
karlaniculae@Alexes-MacBook-Air e-Commerce % kubectl get pods -n ecommerce
NAME                            READY   STATUS             RESTARTS   AGE
api-service-59d665d94c-bmcjz    0/1     Pending            0          0s
auth-service-7b59775dc8-lstfz   0/1     ImagePullBackOff   0          32m
mysql-5d47c9d6b7-gnjlj          1/1     Running            0          169m
karlaniculae@Alexes-MacBook-Air e-Commerce % kubectl get svc -n ecommerce
NAME           TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
api-service    NodePort    10.96.55.56     <none>        8080:30080/TCP   0s
auth-service   NodePort    10.96.222.115   <none>        8081:30081/TCP   32m
mysql          ClusterIP   10.96.186.94    <none>        3306/TCP         169m
```

```bash
 e-Commerce % kubectl get pods -n ecommerce
kubectl describe pod -n ecommerce -l app=api-service | tail -n 30
```
describe îți arată detalii complete despre un pod, nu doar statusul scurt din get pods.
Include:
pe ce node rulează
ce image folosește
ce env vars are
ce ports are
ce volume are
Events (cea mai importantă parte)
→ aici vezi de ce nu pornește: ImagePull, CrashLoopBackOff, etc.