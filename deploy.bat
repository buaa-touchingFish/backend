call mvn clean compile package
scp target\backend-0.0.1-SNAPSHOT.jar  buaa@121.36.81.4:~/project/tfboys.jar
call ssh buaa@121.36.81.4 "cd project&&./killpid.sh"
call ssh buaa@121.36.81.4 "cd project&&java -jar tfboys.jar"
