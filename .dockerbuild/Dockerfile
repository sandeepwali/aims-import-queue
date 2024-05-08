ARG BUILD_REGISTRY_IMAGE
ARG SOURCE_REGISTRY_IMAGE

FROM $BUILD_REGISTRY_IMAGE AS build
COPY / /home/app

RUN echo "Installing dependencies" && \
    echo "Installing 7-Zip (p7zip) and jq" && \
        apk update && \
        apk add \
            jq \
            libxml2-utils \
            curl \
            python3 \
            py3-pip && \
        pip install --upgrade yq pip &&\
    echo "Setting variables" && \
        APP_SRC_DIR='/home/app' && \
        APP_BUILD_DIR='/home/app/build' && \
        APP_ARTIFACT=$(cat ${APP_SRC_DIR}/pom.xml | xq -r '.project.artifactId') && \
        APP_VERSION=$(cat ${APP_SRC_DIR}/pom.xml | xq -r '.project.version') && \
        APP_NAME="${APP_ARTIFACT}-${APP_VERSION}" && \
        APP_JARNAME="${APP_ARTIFACT}-${APP_VERSION}.jar" && \
    echo "Building application ${APP_JARNAME}" && \
        mvn -f ${APP_SRC_DIR}/pom.xml clean package dependency:tree && \
        mkdir -p "${APP_BUILD_DIR}" && \
        cp "${APP_SRC_DIR}/target/${APP_JARNAME}" "${APP_BUILD_DIR}/aims-import-queue.jar" && \
        cp "${APP_SRC_DIR}/src/main/resources/application.properties" "${APP_BUILD_DIR}/application.properties" && \
        cp "${APP_SRC_DIR}/src/main/resources/solace-certificates.jks" "${APP_BUILD_DIR}/solace-certificates.jks" && \
        sed -i -e "s|spring.cloud.stream.binders.solace.environment.solace.java.host=.*|spring.cloud.stream.binders.solace.environment.solace.java.host=tcps://|" "${APP_BUILD_DIR}/application.properties" && \
        sed -i -e "s|spring.cloud.stream.binders.solace.environment.solace.java.msgVpn=.*|spring.cloud.stream.binders.solace.environment.solace.java.msgVpn=|" "${APP_BUILD_DIR}/application.properties" && \
        sed -i -e "s|spring.cloud.stream.binders.solace.environment.solace.java.clientUsername=.*|spring.cloud.stream.binders.solace.environment.solace.java.clientUsername=|" "${APP_BUILD_DIR}/application.properties" && \
        sed -i -e "s|spring.cloud.stream.binders.solace.environment.solace.java.clientPassword=.*|spring.cloud.stream.binders.solace.environment.solace.java.clientPassword=|" "${APP_BUILD_DIR}/application.properties" && \
        sed -i -e "s|solace.message.count.url=.*|solace.message.count.url=|" "${APP_BUILD_DIR}/application.properties" && \
        sed -i -e "s|aims.url.node=.*|aims.url.node=|" "${APP_BUILD_DIR}/application.properties" && \
        sed -i -e "s|aims.apikey=.*|aims.apikey=|" "${APP_BUILD_DIR}/application.properties" && \
    echo "Finished!"

FROM $SOURCE_REGISTRY_IMAGE

ARG MAINTAINER
ARG NAME
ARG COMPONENT
ARG PART_OF
ARG VERSION
ARG TIER
ARG CUSTOMER
ARG CI_COMMIT_AUTHOR
ARG CI_COMMIT_SHORT_SHA

LABEL maintainer="${MAINTAINER}" \
    name="${NAME}" \
    component="${COMPONENT}" \
    part-of="${PART_OF}" \
    version="${VERSION}" \
    tier="${TIER}" \
    customer="${CUSTOMER}" \
    ci-commit-author="${CI_COMMIT_AUTHOR}" \
    ci-commit-short-sha="${CI_COMMIT_SHORT_SHA}"

#COPY --from=build --chown=999 /app /app
COPY --from=build --chown=1010 /home/app/build /app
COPY --from=build --chown=1010 /home/app/.dockerbuild/root /

# Set default user to aims and group to 0
# -> Set default user to aims 999
# -> Set the user group to 0 for remapping user when the docker container starts
# -> https://docs.docker.com/engine/security/userns-remap/#disable-namespace-remapping-for-a-container

RUN chown -R 1010:0 /app &&\
    chmod -R g=u /app &&\
    chmod 775 /docker-entrypoint.sh

WORKDIR /app
USER 1010

ENTRYPOINT [ "/docker-entrypoint.sh" ]
CMD [ "" ]


