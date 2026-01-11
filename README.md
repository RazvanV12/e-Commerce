# e-Commerce – Cloud Native Microservices Application

Acest proiect reprezinta o aplicatie cloud-native bazata pe microservicii.

## Arhitectura

Aplicatia este compusa din urmatoarele microservicii:

- auth-service – autentificare si autorizare utilizatori
- api-service – logica de business (produse, comenzi, coș)
- db – MySQL (stocare persistenta)
- adminer – interfata de administrare DB
- portainer – interfata de administrare cluster/containerelor

---

## Cerințe

- Docker
- Kubernetes ( Kind)
- Helm

## Creare cluster local folosind kind

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
### Pas 4: Cream imaginile de docker ale aplicatiilor noastre ( api & auth )

- Build docker images
```bash 
docker build -t ecommerce-auth:local services/auth-service
docker build -t ecommerce-api:local  services/api-service
```

## Pas 5: Incarcarea imaginilor in cluster-ul kind

- Load docker images in kind

```bash
kind load docker-image ecommerce-auth:local --name ecommerce
kind load docker-image ecommerce-api:local  --name ecommerce
```

- Putem verifica imaginile in cluster folosind comanda:
```bash
docker exec -it ecommerce-control-plane crictl images
```

## Pas 6. Adaugam repo helm pentru ingress-nginx

- Ingress-nginx va fi folosit pentru a expune serviciile in afara cluster-ului
- 
```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
```


## Pas 7. Deploy ingress-nginx in cluster

```bash
helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  -n ingress-nginx \
  --create-namespace
```

## Pas 8. Verificare ingress-nginx

```bash
kubectl get pods -n ingress-nginx
```

- Ar trebui sa arate astfel:
```aiignore
NAME                                       READY   STATUS    RESTARTS   AGE
ingress-nginx-controller-c98c9b6d4-v4lx8   1/1     Running   0          158m
```


## Pas 9: Adaugam repo helm pentru prometheus-community

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

## Pas 10: Deploy Prometheus si Grafana folosind kube-prometheus-stack ( adaugam monitoring-values.yaml pentru configurare custom pentru a persista datele)
```bash 
helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  -n monitoring \
  --create-namespace \
  -f kubernetes/monitoring/monitoring-values.yaml
```

- Verificare Prometheus si Grafana Pods
```bash 
kubectl get pods -n monitoring
```

- Ar trebui sa arate astfel:
```aiignore
alertmanager-monitoring-kube-prometheus-alertmanager-0   2/2     Running   0          159m
monitoring-grafana-6db8cb98bb-mdbg2                      3/3     Running   0          118m
monitoring-kube-prometheus-operator-65c847d757-gp7cw     1/1     Running   0          160m
monitoring-kube-state-metrics-69b6c7f8c-rfvvb            1/1     Running   0          160m
monitoring-prometheus-node-exporter-8cqm2                1/1     Running   0          160m
prometheus-monitoring-kube-prometheus-prometheus-0       2/2     Running   0          159m
```

## Pas 11. Deploy aplicatia e-commerce folosind helm chart-ul creat de noi

```bash 
helm upgrade --install ecommerce kubernetes/ecommerce-chart \
  -n ecommerce \
  --create-namespace
```

- Verificare pods in namespace ecommerce
```bash
kubectl get pods -n ecommerce
```

- Ar trebui sa arate astfel:
```aiignore
NAME                            READY   STATUS    RESTARTS   AGE
adminer-5648678756-2q82w        1/1     Running   0          161m
api-service-5c7cf697c9-bhmpj    1/1     Running   0          102m
api-service-5c7cf697c9-lq6kx    1/1     Running   0          102m
auth-service-5bdb7669d5-mfzzz   1/1     Running   0          161m
auth-service-5bdb7669d5-q8sc4   1/1     Running   0          161m
mysql-6dffc75df4-n4gw4          1/1     Running   0          161m
portainer-67dfdf6f67-nmjt6      1/1     Running   0          161m
```

## Pas 12: Pentru a accesa Prometheus din browser, rulam comanda de port-forwarding
```bash
kubectl port-forward svc/monitoring-kube-prometheus-prometheus 9090:9090 -n monitoring
```

- Accesam Prometheus in browser la adresa:
```aiignore
http://localhost:9090
```
## Pas 13: Pentru a accesa Grafana din browser, rulam comanda de port-forwarding

```bash
kubectl port-forward svc/monitoring-grafana 3000:80 -n monitoring
```

- Accesam Grafana in browser la adresa:

```aiignore
http://localhost:3000
```

- Pentru a obtine parola de admin la Grafana, rulam comanda ( username-ul este admin ) :
```bash
kubectl get secret --namespace monitoring -l app.kubernetes.io/component=admin-secret -o jsonpath="{.items[0].data.admin-password}" | base64 --decode ; echo
```

- Puteti importa dashboard-ul realizat de noi pentru monitorizarea aplicatiei e-commerce din grafana/dashboards/Dashboard.json

- Putem accesa portainer la adresa: 

```aiignore
http://localhost:30090
```

- Creem un cont nou cu username admin si orice parola dorim si rulam urmatoarea comanda pentru a avea acces la namespace-ul ecommerce ( apoi refreshuim pagina ):
```bash
kubectl create clusterrolebinding portainer-admin \
--clusterrole=cluster-admin \
--serviceaccount=ecommerce:default
```

- Pute accesa adminer la adresa:

```aiignore
http://localhost:30083
```

- Credentialele sunt: 

```aiignore
System: MySQL
Server: mysql
Username: ecommerce_user
Password: ecommerce_pass
Database: ecommerce_auth ( sau ecommerce_api )
```

## Pas 14: Accesare servicii din Postman

- Datorita faptului ca am folosit Ingress-Nginx pentru a expune serviciile in afara cluster-ului,
  putem accesa serviciile folosind urmatoarele URL-uri ( in functie de prefix api/auth se va face routing-ul catre
  serviciul corespunzator ):

- URL API service:

```aiignore
http://localhost:8088/api/
```

- URL Auth Service:

```aiignore
http://localhost:8088/auth/
```

- Au fost adaugate colectii postman in modulele api-service, respectiv auth-service pentru a folosi endpoint-urile noastre 


## Linkuri utile

- Verificare pods si servicii in namespace ecommerce

```bash
kubectl -n ecommerce get pods -o wide
kubectl -n ecommerce get svc
```

- Pod-urile ar trebui sa arate astfel ( avem 2 replici pentru api-service si auth-service ):
```aiignore
NAME                            READY   STATUS    RESTARTS   AGE    IP            NODE                      NOMINATED NODE   READINESS GATES
adminer-5648678756-2q82w        1/1     Running   0          179m   10.244.0.14   ecommerce-control-plane   <none>           <none>
api-service-5c7cf697c9-bhmpj    1/1     Running   0          120m   10.244.0.30   ecommerce-control-plane   <none>           <none>
api-service-5c7cf697c9-lq6kx    1/1     Running   0          120m   10.244.0.31   ecommerce-control-plane   <none>           <none>
auth-service-5bdb7669d5-mfzzz   1/1     Running   0          179m   10.244.0.20   ecommerce-control-plane   <none>           <none>
auth-service-5bdb7669d5-q8sc4   1/1     Running   0          179m   10.244.0.16   ecommerce-control-plane   <none>           <none>
mysql-6dffc75df4-n4gw4          1/1     Running   0          179m   10.244.0.15   ecommerce-control-plane   <none>           <none>
portainer-7c846db4c-fg2t4       1/1     Running   0          12m    10.244.0.32   ecommerce-control-plane   <none>           <none>
```
- Serviciile ar trebui sa arate astfel:
```aiignore
$ kubectl -n ecommerce get svc
NAME           TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
adminer        NodePort    10.96.203.236   <none>        8080:30083/TCP   179m
api-service    ClusterIP   10.96.76.199    <none>        8080/TCP         179m
auth-service   ClusterIP   10.96.38.122    <none>        8080/TCP         179m
mysql          ClusterIP   10.96.154.149   <none>        3306/TCP         179m
portainer      NodePort    10.96.238.51    <none>        9000:30090/TCP   179m
```

- Vezi toate resursele din namespace

```bash
kubectl -n ecommerce get all
```

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

- Restart deployment

```bash
kubectl rollout restart deployment/auth-service -n ecommerce
```

### Verificare spatiu de stocare disponibil la nivel de cluster

```bash
kubectl get pv
kubectl get pvc 
```


- Verificare helm releases in namespace ecommerce
```bash
helm list -n ecommerce
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

- Stergere e-commerce helm release

```bash
helm uninstall ecommerce -n ecommerce
```
