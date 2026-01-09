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
- Kubernetes ( Kind)
- Helm

## Creare cluster local folosind kind 

- Folosim un fisier de configurare pentru mapa nodurile NodePort din container catre localhost pentru a putea accesa serviciile din afara cluster-ului 
- Porturile pe care raspunde fiecare serviciu sunt definite in fisierul kubernetes/kind-config.yaml )

### Pas 1: Stergem cluster-ul daca exista

```bash
kind delete cluster --name ecommerce
```

### Pas 2: Creare cluster local folosind Kind

```bash
kind create cluster --name ecommerce --image kindest/node:v1.34.0 --config kubernetes/kind-config.yaml
```

### Pas 3: Verificare

```bash
kubectl get nodes
```
```aiignore
NAME                      STATUS   ROLES           AGE   VERSION
ecommerce-control-plane   Ready    control-plane   19m   v1.34.0
```

## Pas 4: Crearea namespace-ului ecommerce

```bash
kubectl create namespace ecommerce
```
```aiignore
NAME                 STATUS   AGE
default              Active   21m
ecommerce            Active   21m
kube-node-lease      Active   21m
kube-public          Active   21m
kube-system          Active   21m
local-path-storage   Active   21m
```
- Putem vedea aici mai multe namespace-uri implicite create de Kubernetes, printre care si cel definit de noi, ecommerce.

## Pas 5: Deploy MySQL

- MySQL necesita credentiale, configurare si storage, deci resursele sunt aplicate
  intr-o ordine clara.

```bash
kubectl -n ecommerce apply -f kubernetes/mysql-secret.yaml
kubectl -n ecommerce apply -f kubernetes/mysql-configmap.yaml
kubectl -n ecommerce apply -f kubernetes/mysql-init-configmap.yaml
kubectl -n ecommerce apply -f kubernetes/mysql-pv.yaml
kubectl -n ecommerce apply -f kubernetes/mysql-pvc.yaml
kubectl -n ecommerce apply -f kubernetes/mysql-deployment.yaml
kubectl -n ecommerce apply -f kubernetes/mysql-service.yaml
```

- Ce se creeaza: 
    - Secret - credentiale MySQL
    - ConfigMap - configurare MySQL
    - init ConfigMap - scripturi de initializare DB
    - PV/PVC - stocare persistenta
    - Deployment - pod MySQL
    - Service(ClusterIP) - expunere MySQL in cluster

## Pas 6: Verificare MySQL Pod

```bash
kubectl -n ecommerce get pods
kubectl -n ecommerce get svc
```
- Statusul pod-ului ar trebui sa fie Running
```aiignore
READY 1/1
STATUS Running
```

## Pas 7: Deploy auth-service si api-service

- Aplicatiile backend ruleaza ca Deployments si sunt expuse prin NodePort.
```bash
kubectl -n ecommerce apply -f kubernetes/auth-deployment.yaml
kubectl -n ecommerce apply -f kubernetes/api-deployment.yaml
```

## Pas 8: Verificare Pods si Services

```bash
kubectl -n ecommerce get pods -o wide
kubectl -n ecommerce get svc
```

- Pod-urile ar trebui sa arate astfel:
```aiignore
NAME                            READY   STATUS    RESTARTS   AGE   IP           NODE                      NOMINATED NODE   READINESS GATES
api-service-5c8ccc9498-gm9jf    1/1     Running   0          31m   10.244.0.5   ecommerce-control-plane   <none>           <none>
auth-service-545f4df4d9-6zdf6   1/1     Running   0          31m   10.244.0.6   ecommerce-control-plane   <none>           <none>
mysql-6dffc75df4-wmn65          1/1     Running   0          31m   10.244.0.7   ecommerce-control-plane   <none>           <none>
```
- Serviciile ar trebui sa arate astfel:
```aiignore
NAME           TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
api-service    NodePort    10.96.113.14   <none>        8080:30080/TCP   32m
auth-service   NodePort    10.96.165.78   <none>        8081:30081/TCP   32m
mysql          ClusterIP   10.96.213.75   <none>        3306/TCP         32m
```

## Pas 9: Accesare servicii din Postman
- Datorita extraPortMappings din configuratia kind, serviciile sunt accesibile
  direct din host

- URL API service:
```aiignore
http://localhost:30080
```

- URL Auth Service:
```aiignore
http://localhost:30081
```

## Pas 10: Adminer 
- La fel ca la api-serice si auth-service, am pus in fisierul kind-config extraPortMappings
  pentru a putea accesa Adminer din host la portul 30082


- Deploy Adminer
```bash
kubectl apply -f kubernetes/adminer-deployment.yaml
kubectl apply -f kubernetes/adminer-service.yaml
```


## Verificare Adminer Pod si Service
```bash
kubectl get pods -n ecommerce
kubectl get svc  -n ecommerce
```


## Pas 11: Portainer
- Portainer este un tool de management pentru Docker si Kubernetes.
- La fel ca la Adminer, am pus in fisierul kind-config extraPortMappings
  pentru a putea accesa Portainer din host la portul 9000

- Deploy Portainer
```bash
kubectl apply -f kubernetes/portainer-deployment.yaml
kubectl apply -f kubernetes/portainer-service.yaml
```

## Verificare Portainer Pod si Service
```bash
kubectl get pods -n ecommerce
kubectl get svc  -n ecommerce
```

- URL Portainer:
```aiignore
http://localhost:9000
```

- Dupa ce creem un cont nou, pentru a avea access la namespace-ul ecommerce,
  cream un clusterrolebinding si refreshuim pagina.
```bash
kubectl create clusterrolebinding portainer-admin --clusterrole=cluster-admin --serviceaccount=ecommerce:default
```



## Comenzi utile pentru debug: 

- Vezi toate resursele din namespace
```bash
kubectl -n ecommerce get all
```

- Logs
```bash
kubectl -n ecommerce logs deploy/api-service
kubectl -n ecommerce logs deploy/auth-service
kubectl -n ecommerce logs deploy/mysql
```

- Endpoints ( vedem adresele IP si porturile pod-urilor din spatele serviciilor )
```bash
kubectl -n ecommerce get endpoints api-service auth-service mysql
```

- Describe pod
```bash
kubectl -n ecommerce describe pod <pod-name>
```

## Cleanup

- Stergere cluster kind
```bash
kind delete cluster --name ecommerce
```

- Stergere namespace 
```bash
kubectl delete namespace ecommerce
```

