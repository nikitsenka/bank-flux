FROM openjdk:11

RUN apt-get update && apt-get install -y git

RUN git clone https://github.com/nikitsenka/bank-flux.git

WORKDIR bank-flux
RUN ./gradlew build

CMD java -jar target/bank-flux-0.0.1-SNAPSHOT.jar

