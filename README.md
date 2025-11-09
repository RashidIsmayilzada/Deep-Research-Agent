## Setup

1. **Create an OPENAI_API_KEY key in the .env file**
   Example:
```bash
   OPENAI_API_KEY=sk-proj..... 
``` 
3. Clone the repository

4. Configure your OpenAI API key in `src/main/resources/application.properties`:

5. Build the project:

```bash
mvn clean install
```

## Usage

Run the application:

```bash
mvn exec:java -Dexec.mainClass="com.rashid.Main"
```


## Configuration

Edit `src/main/resources/application.properties`:

## Performance
- **Research time**: 60-180 seconds (depends on iterations)
- **Iterations**: adaptive (from 3 to 10 iterations to limit the AI usage)
- **Source validation**: Real HTTP requests per source
- **Memory efficient**: Processes iterations sequentially


