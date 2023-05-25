<h1 align="center">
  <img alt="hanzikon logo" src="https://github.com/sopra-fs23-group-16/sopra-fs23-HanZikon-server/blob/main/src/main/resources/Images/welcome.png" /><br/>
</h1>

## Introduction

Hanzi (Chinese pinyin) means Chinese character. HanZikon is a Chinese character learning and writing APP.

It has three game modes, Riddle of Oracle Script, Hanzi Imitation and Bit of Both Mode.
1. In Riddle of Oracle Script mode, player needs to choose the right chinese character of current age based on the oracle bone script dated back to 1500BC.
2. In Hanzi Imitation mode, player would draw or paint the Chinese Character with short-term memory. Generally, there would be 15- 30 seconds for players to learn the shape and structure of the Chinese character, then 15 seconds for players to reproduce it. There would be three game modes with different levels (eg, 1 -5 from easy to hard ), players are free to choose the mode and level they prefer.
3. In Bit of Both mode, player will have questions combined in above modes mentioned.

The aim of this application is getting to know Chinese characters in an interesting way. Or one can just simply take it as a game to practice your memory or painting skills with fun.

## Technologies

- [Springboot](https://spring.io/projects/spring-boot) - Framework for creating Java applications
- [Gradle](https://gradle.org/) - Build automation tool
- [Google Cloud](https://cloud.google.com/appengine/docs/flexible) - Deployment
- [JPA](https://www.oracle.com/java/technologies/persistence-jsp.html) - Database for user management
- [RESTful](https://restfulapi.net/) - Web services for user control
- [Websocket](https://spring.io/guides/gs/messaging-stomp-websocket/) -  Real-time bidirectional communication between client and server
- [Mockito](https://site.mockito.org/) - Java framework for unit test

## High-level Components

### Player

The [Player](https://github.com/sopra-fs23-group-16/sopra-fs23-HanZikon-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/MultipleMode/Player.java) class represents real-time users in a game. A player instance is initialized with essentail corresponding user information form the User class, such as userID, playerName and icon. It also serves to manage other properties in the game process, like isReady and isWriting, which represent the player status, and scorboard, which stores updated total score.

### Room 

The [Room](https://github.com/sopra-fs23-group-16/sopra-fs23-HanZikon-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/MultipleMode/Room.java) class is mainly responsible for player management. It has unique roomID and roomCode which allow it to be reached by the roomManager to manage the players accordingly.

### QuestionPacker

The [QuestionPacker](https://github.com/sopra-fs23-group-16/sopra-fs23-HanZikon-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/questionGenerator/QuestionPacker.java) class collaborates with the rest classes in the QuestionGenerator package. It gets the question resources originally form csv files which are built and maintained by us. In the very beginning of a new game, it generates a whole QuestionList in JSON format, which will be passed to the frontend by the websocketController class.

### WebsocketController

The [WebsocketController](https://github.com/sopra-fs23-group-16/sopra-fs23-HanZikon-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/WebSocketController.java) class plays a crucial role in multiplayer games. It enables the rapid transmission of game state updates, such as player status, actions, and scores, ensuring that all players receive the latest data simultaneously. This synchronous communication ensures a smooth and immersive gaming experience.

## Getting started with Spring Boot
-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: https://spring.io/guides/tutorials/rest/

## Setup this Template with your IDE of choice
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

### IntelliJ
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs23` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```

### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

## Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing
Have a look here: https://www.baeldung.com/spring-boot-testing

- You may add or run the project test cases under folder src/test https://github.com/sopra-fs23-group-16/sopra-fs23-HanZikon-server/tree/main/src/test/java/ch/uzh/ifi/hase/soprafs23

## Contributing

Please read [CONTRIBUTIONS.md](https://github.com/sopra-fs23-group-16/sopra-fs23-HanZikon-server/blob/main/contributions.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

If you want to say **thank you** or/and support active development of `HanziKon`:

- Add a [GitHub Star](https://github.com/sopra-fs23-group-16) to the project.
- Contact us [Contributors](https://github.com/sopra-fs23-group-16/sopra-fs23-HanZikon-server/graphs/contributors).

Together, we can make this project **better** every day! 😘

