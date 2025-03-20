#!/bin/bash

echo "Waiting for Schema Registry to be ready..."
until curl -s http://localhost:8081/subjects; do
  sleep 1
done

curl -X DELETE http://localhost:8081/subjects/sensor-data-value?permanent=true

echo "Registering Avro schema..."
curl -X POST http://localhost:8081/subjects/sensor-data-value/versions \
  -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d '{
         "type": "record",
         "name": "IoTSensorEventKafka",
         "namespace": "com.relay42.iot.sensor.data.avro",
         "fields": [
           {
             "name": "eventType",
             "type": {
               "type": "enum",
               "name": "IoTSensorEventTypeKafka",
               "namespace": "com.relay42.iot.sensor.data.avro",
               "symbols": [
                 "SENSOR_READINGS"
               ]
             }
           },
           {
             "name": "iotSensorReadings",
             "type": [
               {
                 "type": "record",
                 "name": "IoTSensorReadings",
                 "namespace": "com.relay42.iot.sensor.data.avro",
                 "fields": [
                   {
                     "name": "sensorId",
                     "type": "string"
                   },
                   {
                     "name": "sensorType",
                     "type": {
                       "type": "enum",
                       "name": "SensorType",
                       "symbols": [
                         "THERMOSTAT",
                         "HEART_RATE",
                         "CAR_FUEL_LEVEL"
                       ]
                     }
                   },
                   {
                     "name": "readingValue",
                     "type": {
                       "type": "bytes",
                       "logicalType": "decimal",
                       "precision": 10,
                       "scale": 4
                     }
                   },
                   {
                     "name": "unit",
                     "type": "string"
                   },
                   {
                     "name": "readingAt",
                     "type": {
                       "type": "long"
                     }
                   }
                 ]
               }
             ]
           }
         ]
       }'
echo "Schema registered successfully."
