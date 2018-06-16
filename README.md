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

## Performance Testing

Predefined rules for adapting various behaviors:

1. Plain Akka Http Server: `GET /plain`
2. Simple SQL Query: `GET /sql/personnel?job=nurse`
3. Simple SQL Aggregation: `GET /sql/personnel?job=nurse&count=true`
4. Three table SQL Join: `GET /sql/personnel/join`

