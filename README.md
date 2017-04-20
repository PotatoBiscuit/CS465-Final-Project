# Bank account distributed network
### Compile instructions (In "CS465-Final-Project" directory)
1. mkdir bin
2. javac -d bin -cp bin -s . ./**/*.java
### Run server (In "bin" directory)
java server.Server ../config/server.properties
### Run clients (In "bin" directory)
java client.Client ../config/server.properties