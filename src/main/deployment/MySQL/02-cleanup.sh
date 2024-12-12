cd docker-entrypoint-initdb.d

sed -i "s/$IDP_USER_PASSWORD/__IDP_USER_PASSWORD__/" 01-java-idp-database.sql