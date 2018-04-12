# Certificate Generation for Systems to authenticate themselves in an Arrowhead Framework Local Cloud
The Arrowhead Framework has security built in from the start. 
The system therefore requires you to have certificates and keys for each system in your system of systems.
For security, safety, and privacy reasons, those certificates and keys remain with your system.
The certificates and keys that you generate here should remain on your system or local repository and not be pushed back up to the cloud repository.

The idea is to have a local Certificate Authority (CA) to certify the authenticity of systems in the local cloud and provide encryption keys for secured communication between the local cloud's systems. The local CA is certified by a higher authority outside the local cloud. This creates a chain of trust.

This implementation uses a a 2048 bit SSL with a 256 bit encryption to be aligned with the [Constrained Application Protocol (CoAP)](https://en.wikipedia.org/wiki/Constrained_Application_Protocol) since we have constrained devices in our system.

Until a Local Certificate Authority system exists, you will have to generate theses certificates and keys manually yourself. Note that directories need to be created where the certificates and keys are stored. For example
```shell
>mkdir root
>mkdir root/private
>mkdir root/certs
```

## Local Certificate  Authority

### Generating the Root CA private key:
To generate an encryption key for the  local cloud Certificate Authority (CA), type
```shell
>openssl ecparam -name secp256r1 -genkey | openssl ec -aes-256-cbc -out root/private/root_private_key.pem
```
The computer will ask for a pass phrase
```
Enter PEM pass phrase: 
```
Use *password* as a pass phrase, which you will have to confirm. It will be requested in following commands.

### Generating the Root CA Certificate:
Type (long command lines can be scrolled horizontally):
```shell
>openssl req -config openssl.cnf -key root/private/root_private_key.pem -new -extensions ext_root -out root/certs/root_cert.pem -x509 -days 7300
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
As all systems in the local cloud, the AAA System needs to have its encryption key and certificate. Type (or copy and paste one line at the time):

```shell
>openssl ecparam -name secp256r1 -genkey | openssl ec -aes-256-cbc -out AAA_Server/private/aaa_server_private_key.pem
>openssl req -config openssl.cnf -new -key AAA_Server/private/aaa_server_private_key.pem -out AAA_Server/csr/aaa_server_csr.pem
>openssl x509 -req -extfile openssl.cnf -extensions ext_server -in AAA_Server/csr/aaa_server_csr.pem -CA root/certs/root_cert.pem -CAkey root/private/root_private_key.pem -CAcreateserial -out AAA_Server/certs/aaa_server_cert.pem -days 3650 -sha256
>openssl pkcs12 -export -in AAA_Server/certs/aaa_server_cert.pem -inkey AAA_Server/private/aaa_server_private_key.pem -chain -CAfile root/certs/root_cert.pem -name "aaa-server" -out aaa_server.p12
>keytool -importkeystore -deststorepass password -destkeystore AAA_Server/certs/aaa_server_keystore.jks -srckeystore aaa_server.p12 -srcstoretype PKCS12
>keytool -list -v -keystore AAA_Server/certs/aaa_server_keystore.jks
>keytool -import -file root/certs/root_cert.pem -alias root -keystore AAA_Server/certs/aaa_server_truststore.jks -storepass password -noprompt
```

keytool -importkeystore -srckeystore AAA_Server/certs/aaa_server_keystore.jks -destkeystore AAA_Server/certs/aaa_server_keystore.jks -deststoretype pkcs12

## Producers:
The producer in this case is the weather station. We shall have the Mulle temperature sensors and valve, but the latter are constrained devices for which we have no security currently.

```shell
>openssl ecparam -name secp256r1 -genkey | openssl ec -aes-256-cbc -out Producer/private/producer_private_key.pem
>openssl req -config openssl.cnf -new -key Producer/private/producer_private_key.pem -out Producer/csr/producer_csr.pem
>openssl x509 -req -extfile openssl.cnf -extensions ext_client -in Producer/csr/producer_csr.pem -CA root/certs/root_cert.pem -CAkey root/private/root_private_key.pem -CAcreateserial -out Producer/certs/producer_cert.pem -days 3650 -sha256
>openssl pkcs12 -export -in Producer/certs/producer_cert.pem -inkey Producer/private/producer_private_key.pem -chain -CAfile root/certs/root_cert.pem -name "producer" -out producer.p12
>keytool -importkeystore -deststorepass password -destkeystore Producer/certs/producer_keystore.jks -srckeystore producer.p12 -srcstoretype PKCS12
>keytool -list -v -keystore Producer\certs\producer_keystore.jks
>keytool -import -file root/certs/root_cert.pem -alias root -keystore Producer/certs/producer_truststore.jks -storepass password -noprompt
>keytool -import -file AAA_Server/certs/aaa_server_cert.pem -alias server-cert -keystore Producer/certs/producer_truststore.jks -storepass password -noprompt
```

## Consumers:
The consumers are to start with: 

- the home owner, 
- the district heating application, and 
- the blinder system .


```shell
>openssl ecparam -name secp256r1 -genkey | openssl ec -aes-256-cbc -out Consumer/private/consumer_private_key.pem
>openssl req -config openssl.cnf -new -key Consumer/private/consumer_private_key.pem -out Consumer/csr/consumer_csr.pem
>openssl x509 -req -extfile openssl.cnf -extensions ext_client -in Consumer/csr/consumer_csr.pem -CA root/certs/root_cert.pem -CAkey root/private/root_private_key.pem -CAcreateserial -out Consumer/certs/consumer_cert.pem -days 3650 -sha256
>openssl pkcs12 -export -in Consumer/certs/consumer_cert.pem -inkey Consumer/private/consumer_private_key.pem -chain -CAfile root/certs/root_cert.pem -name "consumer" -out consumer.p12
>keytool -importkeystore -deststorepass password -destkeystore Consumer/certs/consumer_keystore.jks -srckeystore consumer.p12 -srcstoretype PKCS12
>keytool -list -v -keystore Consumer\certs\consumer_keystore.jks
>keytool -import -file root/certs/root_cert.pem -alias root -keystore Consumer/certs/consumer_truststore.jks -storepass password -noprompt
>keytool -import -file AAA_Server/certs/aaa_server_cert.pem -alias server-cert -keystore Consumer/certs/consumer_truststore.jks -storepass password -noprompt
>keytool -import -file Producer/certs/producer_cert.pem -alias producer ->keystore Consumer/certs/consumer_truststore.jks -storepass password -noprompt
```
