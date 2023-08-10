# Trip API Spec

## Create Trip

Endpoint : POST /api/v1/trips

Request Header :

- X-API-TOKEN : Token (Mandatory) 

Request Body : (Form-data)

```form-data
    "image": "File Image", //file image
    "video": "File Video", //file video
    "tnc": "File TNC", //file tnc
    "createTripRequest": {
        "name" : "Yogya Tour",
        "city": "Yogyakarta",
        "price": 1000000,
        "quota": 12,
        "noOfDays": 2,
        "status": "Active"
    }

```

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "errors" : "Name must not blank"
}
```


## Get All Trips

Endpoint : GET /api/trips

Response Body (Success) :

```json
{
    "data": [
        {
            "name": "Yogya Tour",
            "city": "Yogyakarta",
            "price": 1000000,
            "quota": 12,
            "noOfDays": 2,
            "status": "Active",
            "imagePath": "uploads\\Audit Trail Library.png",
            "videoPath": "uploads\\WhatsApp Video 2023-08-03 at 06.41.54.mp4",
            "tncPath": "uploads\\ACEAS module.txt"
        }
    ]
}
```

## Get Trip By Id

Endpoint : GET /api/trips/{id}

Response Body (Success) :

```json
{
  "data" : {
            "name": "Yogya Tour",
            "city": "Yogyakarta",
            "price": 1000000,
            "quota": 12,
            "noOfDays": 2,
            "status": "Active",
            "imagePath": "uploads\\Audit Trail Library.png",
            "videoPath": "uploads\\WhatsApp Video 2023-08-03 at 06.41.54.mp4",
            "tncPath": "uploads\\ACEAS module.txt"
        }
}
```

Response Body (Failed, 404) :

```json
{
  "errors" : "Trip with that Id is not found"
}
```


## Update Trip

Endpoint : PATCH /api/trips/update/{id}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body : (Form-Data)

```form-data
    "image": "File Image", //file image --> optional
    "video": "File Video", //file video --> optional
    "tnc": "File TNC", //file tnc --> optional
    "updateTripRequest": {
        "name" : "Yogya Tour", //optional 
        "city": "Yogyakarta", //optional
        "price": 1000000, //optional
        "quota": 12, //optional
        "noOfDays": 2, //optional
        "status": "Active" //optional
    }
```

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "errors" : "Name must not blank"
}
```

Response Body (Failed, 401) :

```json
{
  "errors" : "Unauthorized"
}
```

Response Body (Failed, 404) :

```json
{
  "errors" : "Trip with that Id is not found"
}
```

## Delete Trip

Endpoint : DELETE /api/trips/delete/{id}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed, 401) :

```json
{
  "errors" : "Unauthorized"
}
```

Response Body (Failed, 404) :

```json
{
  "errors" : "Trip with that Id is not found"
}
```