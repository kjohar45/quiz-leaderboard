# Quiz Leaderboard – Backend Integration Task

So this is my solution for the quiz backend task. took me a while but finally got it working.
the problem: an api gives scores for different rounds and participants, but sometimes the same data comes twice. if you just add everything up, your total will be wrong. so you have to filter out duplicates.

## how it works

1. i call the api 10 times (poll=0 to 9) with 5 seconds gap between each call.
2. each response has events with roundId, participant name, and score.
3. i create a unique key = roundId + participant name and store it in a Set.
4. if the key is new → add the score to that participant's total.
5. if the key already exists → ignore (duplicate).
6. after all 10 polls, i sort the leaderboard by totalScore (highest first).
7. then i send ONE post request with the final leaderboard.

no double counting, no extra submissions.

## tech stack

- Java 11
- Maven
- Jackson (json parsing)
- Java HttpClient (no extra libraries needed)

## how to run

clone the repo:

git clone https://github.com/kjohar45/quiz-leaderboard.git
cd quiz-leaderboard

then run:

mvn compile exec:java "-Dexec.mainClass=com.srmc.Main"

it will take about 50 seconds to finish all 10 polls. the final leaderboard and submit response will print at the end.

## result

Diana - 470
Ethan - 455
Fiona - 440
Total - 1365
