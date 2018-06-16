# Kubernetes related configuration.

Contains collection of yaml files for database service deployment.

## Helm

To use the helm, preparing the helm installed in your environment.

```bash
helm install --name mysql-service -f helm/mysql/values.yml stable/mysql
```

Containing helm configurations:
1. [MySQL](https://github.com/kubernetes/charts/tree/master/stable/mysql)
2. [CouchDB](https://github.com/kubernetes/charts/tree/master/incubator/couchdb)
