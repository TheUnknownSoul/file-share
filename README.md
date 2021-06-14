# File-share

This microservice allows sharing text files.

### How to use

Clone this project to your local machine. Start it via programming environment or call `mvn package` to build executable
jar file. When application receives file it will create new directory in src folder called `files`.

#### Endpoints

`POST /register`

This endpoint accepts json object with two string properties: email and password. On successful create it will return
status 201. This is the only public endpoint.

`GET /api/file`

This endpoint returns all shared files of requested user.

`GET /api/file/{id}`

This endpoint is used to download file associated with given ID in uri.  
Files should be plaintext.

`POST /api/file`

Use this endpoint to upload the file and on success it will return file ID that can be used with GET endpoint to
download file.

`POST /api/share`

This endpoint will be used by file owner to share access to the file with other users. Only the file owner can give the
access to his file.

#### Security

All endpoint secured by basic auth. User password encrypted with `BCryptPasswordEncoder` .