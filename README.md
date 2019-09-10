#GEORENT
Backend for GeoRent project

##Building project
JDK 8 need to be installed

From the root dir of a project run

- Windows: 
```gradlew.bat clean build```
- Mac, Linux: 
```./gradlew clean build```

to skip test phase add `-x test` to the previous command.

##Run project

```java -jar build/libs/{application-jar-name.jar}```
