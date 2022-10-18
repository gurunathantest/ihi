FROM maven:3.6.3-jdk-11

WORKDIR ./ihi_auth

COPY . .

RUN mvn clean install -Pstage

CMD mvn spring-boot:run
