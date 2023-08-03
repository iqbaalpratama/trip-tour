# User API Spec

## Register User

Endpoint : POST /api/v1/users

Request Body :

```json
{
    "email": "employer1@gmail.com",
    "firstName": "Putri",
    "lastName": "Ramadhan",
    "gender": "P",
    "address": "Jalan Anggur 312 Jakarta",
    "phone": "089876543210",
    "password": "rahasia"
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
  "errors" : "Phone must not blank"
}
```

## Login User

Endpoint : POST /api/auth/login

Request Body :

```json
{
    "email": "employer@email.com",
    "password": "rahasia"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "token" : "TOKEN",
    "expiresIn" : 1800 // seconds or 30 minutes
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors" : "Username or password wrong"
}
```

## Get All Users 

Endpoint : GET /api/users/get-all

Response Body (Success) :

```json
{
  "data" : [
                   {
                       "email": "employer@email.com",
                       "role": "employer",
                       "firstName": "Iqbaal",
                       "lastName": "Putra",
                       "gender": "Laki-laki",
                       "phone": "081234567890",
                       "address": "Jalan Anggur 3 Jakarta"
                   },
                   {
                       "email": "employer1@gmail.com",
                       "role": "employer",
                       "firstName": "Putri",
                       "lastName": "Ramadhan",
                       "gender": "Perempuan",
                       "phone": "089876543210",
                       "address": "Jalan Anggur 312 Jakarta"
                   }
               ]
}
```

## Get User By Id

Endpoint : GET /api/users/get

Request Param :

- id: String userId (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
        "email": "employer@email.com",
        "role": "employer",
        "firstName": "Iqbaal",
        "lastName": "Putra",
        "gender": "Laki-laki",
        "phone": "081234567890",
        "address": "Jalan Anggur 3 Jakarta"
  }
}
```

Response Body (Failed, 404) :

```json
{
  "errors" : "User with that Id is not found"
}
```

## Get User Current Login

Endpoint : GET /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory) 

Response Body (Success) :

```json
{
  "data" : {
        "email": "employer@email.com",
        "role": "employer",
        "firstName": "Iqbaal",
        "lastName": "Putra",
        "gender": "Laki-laki",
        "phone": "081234567890",
        "address": "Jalan Anggur 3 Jakarta"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors" : "Unauthorized"
}
```

## Update User

Endpoint : PATCH /api/users/update

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body : 

```json
{
    "email": "employer1@gmail.com", //optional
    "firstName": "Putri", //optional
    "lastName": "Ramadhan", //optional
    "gender": "P", //optional
    "address": "Jalan Anggur 312 Jakarta", //optional
    "phone": "089876543210", //optional
    "password": "rahasia" //optional
}
```

Response Body (Success) :

```json
{
  "data" : {
        "email": "employer@email.com",
        "role": "employer",
        "firstName": "Iqbaal",
        "lastName": "Putra",
        "gender": "Laki-laki",
        "phone": "081234567890",
        "address": "Jalan Anggur 3 Jakarta"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors" : "Unauthorized"
}
```

## Logout User

Endpoint : DELETE /api/auth/logout

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```