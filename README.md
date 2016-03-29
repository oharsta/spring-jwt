# spring-jwt

[![Build Status](https://travis-ci.org/oharsta/spring-jwt.svg)](https://travis-ci.org/oharsta/spring-jwt)
[![codecov.io](https://codecov.io/github/oharsta/spring-jwt/coverage.svg)](https://codecov.io/github/oharsta/spring-jwt)

Spring JWT

## [Rationale](#rationale)

This demo application shows how very simple it is with Spring Security to secure an endpoint using JSON Web Token (JWT).

There is a BASIC AUTH secured endpoint for obtaining a JWT. With the JWT the client can call the other secured endpoints.

## [Requirements](#requirements)

* JDK8
* MongoDB
* Maven3(+)

## [cUrl](#curl)

The secured endpoint for obtaining a token:

```bash
TOKEN=$(curl -X POST -H "Content-Type: application/json" --user "John Doe:secret" http://localhost:8080/token)
```

You can validate the token on https://jwt.io/ using the base64 encoded secret from [application.properties](src/main/resources/application.properties).

To access the admin endpoint using the token:

```bash
curl -H "X-AUTH-TOKEN: $TOKEN" -H "Content-Type: application/json" http://localhost:8080/admin/user
```

If you would get a token for an ordinary user:

```bash
TOKEN=$(curl -X POST -H "Content-Type: application/json" --user "Mary Doe:secret" http://localhost:8080/token)
```
You will get a 403 for the admin endpoint;:

```bash
curl -ik -H "X-AUTH-TOKEN: $TOKEN" -H "Content-Type: application/json" http://localhost:8080/admin/user
```
But you can access the user page:

```bash
curl -H "X-AUTH-TOKEN: $TOKEN" -H "Content-Type: application/json" http://localhost:8080/user
```

