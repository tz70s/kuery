# Kubernetes related configuration.

Contains collection of yaml files for database service deployment.

## Ingress

For easier observing production environment, deploying ingress object and ingress controller for kubernetes cluster. [reference](https://docs.bitnami.com/kubernetes/how-to/secure-kubernetes-services-with-ingress-tls-letsencrypt/)
```bash
# Still use the default namespace
helm install stable/nginx-ingress

# Create ingress controller, which contains the default 80 port of kuery-service
kubectl apply -f ingress/production.yml

# Debug mode, open all database services for local connection.
kubectl apply -f ingress/debug.yml
```

## Helm

To use the helm, preparing the helm installed in your environment.

```bash
helm install --name mysql-service -f helm/mysql/values.yml stable/mysql
```

Containing helm configurations:
1. [MySQL](https://github.com/kubernetes/charts/tree/master/stable/mysql)
2. [CouchDB](https://github.com/kubernetes/charts/tree/master/incubator/couchdb)
3. [Vitess](https://github.com/vitessio/vitess/tree/master/helm/vitess)
4. [TiDB](https://banzaicloud.com/blog/tidb-kubernetes/)
