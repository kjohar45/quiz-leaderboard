# Quiz Leaderboard - Backend Integration Task

So this is my solution for the quiz backend task. Took me a while but finally got it working.
The problem: an API gives scores for different rounds and participants, but sometimes the same data comes twice. If you just add everything up, your total will be wrong. So you have to filter out duplicates.

## How It Works

1. I call the API 10 times (poll=0 to 9) with 5 seconds gap between each call.
2. Each response has events with roundId, participant name, and score.
3. I create a unique key = roundId + participant name and store it in a Set.
4. If the key is new, add the score to that participant's total.
5. If the key already exists, ignore it as a duplicate.
6. After all 10 polls, I sort the leaderboard by totalScore (highest first).
7. Then I send one POST request with the final leaderboard.

No double counting, no extra submissions.

## Tech Stack

- Java 11
- Maven
- Jackson (JSON parsing)
- Java HttpClient (no extra libraries needed)

## How To Run

Clone the repo:

git clone https://github.com/kjohar45/quiz-leaderboard.git
cd quiz-leaderboard

Then run:

mvn compile exec:java "-Dexec.mainClass=com.srmc.Main"

It will take about 50 seconds to finish all 10 polls. The final leaderboard and submit response will print at the end.

## Result

Diana - 470
Ethan - 455
Fiona - 440
Total - 1365
