FROM openjdk:11-jre-slim
ARG JAR_FILE=target/US4-Back-end-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ./US4-Back-end.jar
ENTRYPOINT ["java","-jar","/US4-Back-end.jar"]
RUN mkdir -p /home/fileUploads