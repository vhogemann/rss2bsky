# bsky2rss

This project is a Java application that interacts with the BlueSky social media platform via its API. It allows users to log in, create posts, and upload blobs (binary large objects) such as images.
At some point in the future it will become a RSS bot for BlueSky.

The application uses the following technologies:

- **Java**: The primary programming language.
- **Gradle**: The build automation tool.
- **Jackson**: For JSON serialization and deserialization.
- **OkHttp**: For making HTTP requests.
- **jsoup**: For parsing HTML.

## Features

- **Login**: Authenticate with BlueSky using a username and password.
- **Create Post**: Post text content to BlueSky.
- **Upload Blob**: Upload binary data (e.g., images) to BlueSky.
- **Create Post with Link Card**: Create a post that includes a link card with metadata and an image.

## Setup

1. **Clone the repository**:
    ```sh
    git clone https://github.com/vhogemann/bsky2rss.git
    cd bsky2rss
    ```

2. **Build the project**:
    ```sh
    ./gradlew build
    ```

3. **Run the application**:
    ```sh
    ./gradlew run
    ```

## Dependencies

- `com.fasterxml.jackson.core:jackson-databind`
- `com.fasterxml.jackson.datatype:jackson-datatype-jsr310`
- `com.squareup.okhttp3:okhttp`
- `org.jsoup:jsoup`

## Usage

Modify the `App.java` file to include your BlueSky credentials and run the application to interact with the BlueSky API.

## License

This project is licensed under the MIT License.