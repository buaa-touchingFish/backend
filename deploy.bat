call mvn clean compile package
scp target\backend-0.0.1-SNAPSHOT.jar  buaa@101.43.202.84:~/project/tfboys.jar
call ssh buaa@101.43.202.84 "cd project&&./killpid.sh"
call ssh buaa@101.43.202.84 "cd project&&java -jar tfboys.jar"
