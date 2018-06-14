# kuery
SQL usage comparison and competitions.

## Execution

There's already pushed image on the docker hub.

```bash
docker run -it -p 8080:8080 tz70s/kuery

# Index route for more information
curl http://localhost:8080
```

To run locally without docker requires sbt to build.

```bash
sbt run
```

