# Deep Research Agent

A professional AI-powered research agent built with Java 21 that works like a **truly conversational research assistant**. The AI itself decides what questions to ask you (no pre-made choices), understands your needs, researches accordingly, and then has unlimited follow-up conversation with you.
## Setup

1. Clone the repository

2. Configure your OpenAI API key in `src/main/resources/application.properties`:

3. Build the project:

```bash
mvn clean install
```

## Usage

Run the application:

```bash
mvn exec:java -Dexec.mainClass="com.rashid.Main"
```

Or run the compiled JAR:

```bash
java -jar target/Deep-Research-Agent-1.0-SNAPSHOT.jar
```

## Configuration

Edit `src/main/resources/application.properties`:

## Performance
- **Research time**: 60-180 seconds (depends on iterations)
- **Iterations**: adaptive (from 3 to 10 iterations to limit the AI usage)
- **Source validation**: Real HTTP requests per source
- **Memory efficient**: Processes iterations sequentially


