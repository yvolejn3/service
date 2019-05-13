### Создание хранилища ключей

`$JAVA_HOME/bin/keytool -genkey -dname <DN> -alias <name> -validity <days> -keyalg RSA -keystore <path> -keypass <keypass> -storepass <storepass> -ext san=<SAN>`

Пример параметров:
* *DN* - `"CN=name, OU=unit, O=org, L=city, ST=state, C=RU"`
* *name* - `test` 
* *days* - `365`
* *path* - `$HOME/keystore.jks`
* *keypass* - `123456`
* *storepass* - `123456`
* *SAN* - `ip:x.x.x.x` или `dns:igtel.ru`
