FROM openjdk:21-jdk as build

RUN microdnf install findutils

RUN mkdir build

COPY . /build

RUN cd /build && \
    tar --skip-old-files -xzf ${basename}-bin.tar.gz && \
    rm ${basename}-bin.tar.gz && \
    find . -type d -exec chmod 755 {} \;

RUN ls -l /build

FROM openjdk:21-jdk as image

ENV HOME=/idp-server

COPY --from=build /build ${HOME}

WORKDIR ${HOME}

RUN mv idp-0.0.1-SNAPSHOT.jar lib/idp-0.0.1-SNAPSHOT.jar

CMD [ "java", "-cp", "lib/*:.", "com.sourceware.labs.idp.IdpApplication" ]