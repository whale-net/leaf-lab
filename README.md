# leaf-lab
Smart Planters

## running leaf-lab

### locally
using java 17. scala version is pinned in build.sbt
to run locally just do `sbt run`

if you do not have sbt, check out [sdkman](https://sdkman.io/).
then do `sdk install sbt`. additionally, you will need java. check versions with `sdk list java | grep 17`. then install with `sdk install java 17.0.X-tem`. 
set it as the default java version with `sdk default java 17.0.14-tem`. TODO setup jENV instructions. also TODO, figure out JENV

### 
alternatively, use tilt
- https://docs.tilt.dev/install.html
- requires docker kubernetes (easiest setup)
run via `tilt up`
reads values from .env file
automatically builds docker image
can access locally via `localhost:8080` thanks to port forwarding

### production
Deployed with Helm chart.
Most configuration-like stuff and required resources, such as databases, should be defined in this repository. 
This should hopefully make it easy to setup a new instance.
However, some configurations and resources might be defined externally or manually and may not be present in this repository.

### commands
`export $(cat .env | xargs)`

### test data

person

```json
{
  "id": 123,
  "name": "John Tester"
}
```

plant
```json
{
  "id": 456,
  "name": "My Precious",
  "plant_type": "Tomato",
  "owner_person_id": 123
}
```

sensor
```json
{
  "id": 789,
  "name": "Thermometer",
  "unit": "Celsius"
}
```

```json
{
  "id": -1,
  "plant_id": 456,
  "sensor_id": 789,
  "value": "100.0",
  "as_of": "2025-01-20T14:37:51.528214229-05:00"
}
```

sample call:
```bash
curl -H "Content-Type: application/json" -X POST -d '{"id": -1, "plant_id": 456, "sensor_id": 789, "value": "100.0", "as_of": "2025-01-20T14:37:51.528214229-05:00"}' localhost:8080/result
```