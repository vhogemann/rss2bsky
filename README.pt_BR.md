# rss2bsky

rss2bsky é uma aplicação Java que busca itens de feed RSS e os publica no BlueSky.

## Funcionalidades

- Busca itens de feed RSS do YouTube.
- Publica itens de feed no BlueSky com cartões de link.

## Requisitos

- Java 11 ou superior
- Gradle
- Conta no BlueSky

## Configuração

1. Clone o repositório:
    ```sh
    git clone https://github.com/vhogemann/rss2bsky.git
    cd rss2bsky
    ```

2. Construa o projeto usando Gradle:
    ```sh
    ./gradlew build
    ```

3. Configure as variáveis de ambiente para as credenciais do BlueSky:
    ```sh
    export BSKY_IDENTITY=sua_identidade
    export BSKY_PASSWORD=sua_senha
    ```

## Uso

Execute a aplicação:
```sh
./gradlew run