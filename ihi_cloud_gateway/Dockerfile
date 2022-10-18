FROM maven:3.6.3-jdk-11

WORKDIR ./ihi_cloud_gateway

COPY . .

RUN mvn clean install -Pstage

CMD mvn spring-boot:run
