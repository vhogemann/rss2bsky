![img.png](img.png)

# rss2bsky

rss2bsky é uma aplicação Java que busca itens de feed RSS e os publica no BlueSky.

## Funcionalidades

- Busca itens de feed RSS do YouTube.
- Publica itens de feed no BlueSky com cartões de link.

## Requisitos

- Java 21 or later
- Gradle 8.10.1 or later

## Execução

Esta aplicação é configurada para ser executada dentro de um contêiner **Docker**. É a maneira mais fácil e recomendada de executá-la.

### Configuração

Antes de executar a aplicação, você precisa configurar um arquivo `source.json` no diretório `/json`.
O arquivo deve conter a seguinte estrutura:

```json
[
  {
    "feedId": "example_feed",
    "name": "Example Feed",
    "rssUrl": "https://www.youtube.com/feeds/videos.xml?playlist_id=PLAYLIST_ID",
    "feedExtractor": "YOUTUBE",
    "bskyIdentity": "example.identity.com",
    "bskyPassword": "example-app-password"
  }
]
```

O arquivo define um array JSON de objetos, onde cada objeto representa um feed a ser processado e publicado no BlueSky.
Aqui está uma breve descrição dos campos de configuração:

| Field          | Description                                                                                 |
|----------------|---------------------------------------------------------------------------------------------|
| `feedId`       | Um identificador único para o feed.                                                         |
| `name`         | Nome do feed.                                                                               |
| `rssUrl`       | URL para o feed RSS ou Atom.                                                                |
| `feedExtractor`| Algoritmo usado pra extrair conteúdo do feed. `YOUTUBE` e `VANILLA` são os valores válidos. |
| `bskyIdentity` | Nome de usuário no BlueSky.                                                                 |
| `bskyPassword` | BlueSky App password.                                                                       |

#### Extractors

Extractor define how the feed items are handled, and the fields that will be used to fetch the BlueSky post
content and link embeds. Currently, the following extractors are available:

Um extrator define como os itens do feed são manipulados e os campos que serão usados para buscar o conteúdo da postagem
do BlueSky e os links incorporados. Atualmente, os seguintes extratores estão disponíveis:

- `YOUTUBE`: Extrai itens de feed do YouTube.
- `VANILLA`: Extrai itens de feed com base em tags específicas.

#### Execução

Para executar a aplicação, você pode usar o script `run.sh`:

```bash
./run.sh
```

O script irá construir a imagem Docker e executar o contêiner. A aplicação começará a buscar itens de feed
e publicá-los no BlueSky.

Para saber o que foi postado, a aplicação criará um arquivo `.ndjson` com o nome do feed ID no diretório `/json`.
Este arquivo vai conter os itens do feed que foram postados no BlueSky.

A ideia é executar esta aplicação periodicamente, para que ela possa buscar novos itens de feed e publicá-los no BlueSky.