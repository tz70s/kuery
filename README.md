# kuery
SQL usage comparison and competitions.

## Execution

```bash
# Dist docker image
sbt docker:pushLocal

docker run -it -p 8080:8080 tz70s/kuery:0.1

# Index route for more information
curl http://localhost:8080
```
