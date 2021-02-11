FROM openjdk:11

COPY litecd-0.2.jar litecd.jar

CMD java -jar litecd.jar
