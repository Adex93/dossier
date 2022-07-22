FROM openjdk:17.0.1
ADD /target/dossier-0.0.1-SNAPSHOT.jar dossier.jar
ENTRYPOINT ["java", "-jar", "dossier.jar"]