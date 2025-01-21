# plant-lab
Smart Planters

## running locally

using java 17. scala version is pinned in build.sbt
to run locally just do `sbt run`

alternatively, use tilt
- https://docs.tilt.dev/install.html
- requires docker kubernetes (easiest setup)
run via `tilt up`
reads values from .env file
automatically builds docker image
can access locally via `localhost:8080` thanks to port forwarding


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