# Install with docker

```sh
$ docker run --volume=/var/warp10:/data -p 8080:8080 -p 8081:8081 -d -i warp10io/warp10:1.0.16
```

Get the image id:

```sh
$ docker ps
```

Generate tokens and copy them:

```sh
$ docker exec -t -i <imageId> worf.sh

# Create the write token
> encodeToken
encodeToken/token type (read|write)> write
encodeToken/application name, default (***)> <enter>
encodeToken/data producer UUID, default (***)> <enter>
encodeToken/data owner UUID, default (***)> <enter>
encodeToken/token ttl (ms) > 31536000000
encodeToken(generate | cancel)> generate

# Create the read token
> encodeToken
encodeToken/token type (read|write)> read
encodeToken/application name, default (***)> <enter>
encodeToken/data producer UUID, default (***)> <enter>
encodeToken/data owner UUID, default (***)> <enter>
encodeToken/token ttl (ms) > 31536000000
encodeToken(generate | cancel)> generate
```

Save generated token somewhere:

```sh
Note:
* Read token: ***
* Write token: ***
```

# Use warp10-quantum

* Go to http://localhost:8081
* Load dataset from `src/main/resources/data/drones`
* Execute queries from `src/main/resources/data/queries`

# Troubleshooting

## WarpScript

* If you have the error `ERROR line *: in section '[TOP]': Invalid token.`
* Click on *Choose another backend* / *Edit*
* Backend URL: `http://127.0.0.1:8080/api/v0`
* Click on *Valid*

## Update

* If you have the error `Unkown error`
* Click on *Choose another backend* / *Edit*
* Backend URL: `http://127.0.0.1:8080/api/v0`
* HTTP header root: `X-Warp10`
* Click on *Valid*