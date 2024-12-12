cd docker-entrypoint-initdb.d

sed -i "s/__IDP_USER_PASSWORD__/$IDP_USER_PASSWORD/" 01-java-idp-database.sql