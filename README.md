# File-share

This microservice allows sharing text files.

### How to use

Clone this project to your local machine. Start it via programming environment or call `mvn package` to build executable
jar userFile. When application receives userFile it will create new directory in src folder called `files`.

#### Endpoints

`POST /register`

This endpoint accepts json object with two string properties: email and password. On successful create it will return
status 201. This is the only public endpoint.

`GET /api/userFile`

This endpoint returns all shared files of requested user.

`GET /api/userFile/{id}`

This endpoint is used to download userFile associated with given ID in uri.  
Files should be plaintext.

`POST /api/userFile`

Use this endpoint to upload the userFile and on success it will return userFile ID that can be used with GET endpoint to
download userFile.

`POST /api/share`

This endpoint will be used by userFile owner to share access to the userFile with other users. Only the userFile owner can give the
access to his userFile.
Parameter `name` should contain you current email via you\`re authorized in. 
Parameter `id` should contain id of file that you`re want to share.
#### Security

All endpoint secured by basic auth. User password encrypted with `BCryptPasswordEncoder` .