# Certificate Generation for Systems to authenticate themselves in an Arrowhead Framework Local Cloud
The certificates that you generate here should remain on your system or local repository and not be pushed back up to the cloud repository.

## root CA private key:
To generate a local cloud Certificate Authority (CA), (which will be a system?)
```shell
>openssl ecparam -name secp256r1 -genkey | openssl ec -aes-256-cbc -out root/private/root_private_key.pem
Enter PEM pass phrase: password
```

For root cert:
--------------
```
>openssl req -config openssl.cnf -key root/private/root_private_key.pem -new -extensions ext_root -out root/certs/root_cert.pem -x509 -days 7300
Enter pass phrase for root/private/root_private_key.pem: password
```

You are about to be asked to enter information that will be incorporated into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.

```
Country Name (2 letter code) []:SE
State or Province Name []:LLA
Locality Name []:LLA
Organization Name []:AAA_NGAC
Organizational Unit Name []:Root certificate authority
Common Name []:Root_CA
Email Address []:a@b.com
```


## AAA System:

```
openssl ecparam -name secp256r1 -genkey | openssl ec -aes-256-cbc -out AAA_Server/private/aaa_server_private_key.pem
openssl req -config openssl.cnf -new -key AAA_Server/private/aaa_server_private_key.pem -out AAA_Server/csr/aaa_server_csr.pem
openssl x509 -req -extfile openssl.cnf -extensions ext_server -in AAA_Server/csr/aaa_server_csr.pem -CA root/certs/root_cert.pem -CAkey root/private/root_private_key.pem -CAcreateserial -out AAA_Server/certs/aaa_server_cert.pem -days 3650 -sha256
openssl pkcs12 -export -in AAA_Server/certs/aaa_server_cert.pem -inkey AAA_Server/private/aaa_server_private_key.pem -chain -CAfile root/certs/root_cert.pem -name "aaa-server" -out aaa_server.p12
keytool -importkeystore -deststorepass password -destkeystore AAA_Server/certs/aaa_server_keystore.jks -srckeystore aaa_server.p12 -srcstoretype PKCS12
keytool -list -v -keystore AAA_Server\certs\aaa_server_keystore.jks
keytool -import -file root/certs/root_cert.pem -alias root -keystore AAA_Server/certs/aaa_server_truststore.jks -storepass password -noprompt
```

## Producers:
The producer in this case is the weather station. We shall have the Mulle temperature sensors and valve, but the latter are constrained devices for which we have no security currently.

```
openssl ecparam -name secp256r1 -genkey | openssl ec -aes-256-cbc -out Producer/private/producer_private_key.pem
openssl req -config openssl.cnf -new -key Producer/private/producer_private_key.pem -out Producer/csr/producer_csr.pem
openssl x509 -req -extfile openssl.cnf -extensions ext_client -in Producer/csr/producer_csr.pem -CA root/certs/root_cert.pem -CAkey root/private/root_private_key.pem -CAcreateserial -out Producer/certs/producer_cert.pem -days 3650 -sha256
openssl pkcs12 -export -in Producer/certs/producer_cert.pem -inkey Producer/private/producer_private_key.pem -chain -CAfile root/certs/root_cert.pem -name "producer" -out producer.p12
keytool -importkeystore -deststorepass password -destkeystore Producer/certs/producer_keystore.jks -srckeystore producer.p12 -srcstoretype PKCS12
keytool -list -v -keystore Producer\certs\producer_keystore.jks
keytool -import -file root/certs/root_cert.pem -alias root -keystore Producer/certs/producer_truststore.jks -storepass password -noprompt
keytool -import -file AAA_Server/certs/aaa_server_cert.pem -alias server-cert -keystore Producer/certs/producer_truststore.jks -storepass password -noprompt
```

### Consumers:
The consumers are to start with: 

- the home owner, 
- the district heating application, and 
- the blinder system .


```
openssl ecparam -name secp256r1 -genkey | openssl ec -aes-256-cbc -out Consumer/private/consumer_private_key.pem
openssl req -config openssl.cnf -new -key Consumer/private/consumer_private_key.pem -out Consumer/csr/consumer_csr.pem
openssl x509 -req -extfile openssl.cnf -extensions ext_client -in Consumer/csr/consumer_csr.pem -CA root/certs/root_cert.pem -CAkey root/private/root_private_key.pem -CAcreateserial -out Consumer/certs/consumer_cert.pem -days 3650 -sha256
openssl pkcs12 -export -in Consumer/certs/consumer_cert.pem -inkey Consumer/private/consumer_private_key.pem -chain -CAfile root/certs/root_cert.pem -name "consumer" -out consumer.p12
keytool -importkeystore -deststorepass password -destkeystore Consumer/certs/consumer_keystore.jks -srckeystore consumer.p12 -srcstoretype PKCS12
keytool -list -v -keystore Consumer\certs\consumer_keystore.jks
keytool -import -file root/certs/root_cert.pem -alias root -keystore Consumer/certs/consumer_truststore.jks -storepass password -noprompt
keytool -import -file AAA_Server/certs/aaa_server_cert.pem -alias server-cert -keystore Consumer/certs/consumer_truststore.jks -storepass password -noprompt
keytool -import -file Producer/certs/producer_cert.pem -alias producer -keystore Consumer/certs/consumer_truststore.jks -storepass password -noprompt
```
