# bsky2rss

Este projeto é uma aplicação Java que interage com a plataforma de mídia social BlueSky via sua API. Ele permite que os usuários façam login, criem postagens e façam upload de blobs (objetos binários grandes) como imagens.
Em algum momento no futuro, ele se tornará um bot RSS para o BlueSky.

A aplicação utiliza as seguintes tecnologias:

- **Java**: A linguagem de programação principal.
- **Gradle**: A ferramenta de automação de build.
- **Jackson**: Para serialização e desserialização de JSON.
- **OkHttp**: Para fazer requisições HTTP.
- **jsoup**: Para analisar HTML.

## Funcionalidades

- **Login**: Autenticação no BlueSky usando um nome de usuário e senha.
- **Criar Postagem**: Postar conteúdo de texto no BlueSky.
- **Upload de Blob**: Fazer upload de dados binários (por exemplo, imagens) para o BlueSky.
- **Criar Postagem com Cartão de Link**: Criar uma postagem que inclui um cartão de link com metadados e uma imagem.

## Configuração

1. **Clonar o repositório**:
    ```sh
    git clone https://github.com/vhogemann/bsky2rss.git
    cd bsky2rss
    ```

2. **Construir o projeto**:
    ```sh
    ./gradlew build
    ```

3. **Executar a aplicação**:
    ```sh
    ./gradlew run
    ```

## Dependências

- `com.fasterxml.jackson.core:jackson-databind`
- `com.fasterxml.jackson.datatype:jackson-datatype-jsr310`
- `com.squareup.okhttp3:okhttp`
- `org.jsoup:jsoup`

## Uso

Modifique o arquivo `App.java` para incluir suas credenciais do BlueSky e execute a aplicação para interagir com a API do BlueSky.

## Licença

Este projeto está licenciado sob a Licença MIT.