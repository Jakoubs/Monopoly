FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

RUN apt-get update && apt-get install -y libxrender1 libxtst6 libxi6 libxext6

WORKDIR /monopoly

COPY . .

ENTRYPOINT ["sbt"]
CMD ["run"]