# Chthonic Project (Maven)

This Maven project implements the task: generate an infinite stream of mythic (chthonic) creatures, collect 500 elements after skipping first N matching a species predicate, filter by years since first mention, group by species, compute attack-power statistics and outlier analysis (IQR).

## How to build

Requires JDK 21+ (tested for JDK 24).

Build:
```
mvn -q -DskipTests package
```

Run:
```
java -cp target/chthonic-project-1.0-SNAPSHOT-jar-with-dependencies.jar com.kpi.App [skipN] [speciesToSkip] [minYears] [maxYears]
```

Defaults:
- skipN = 10
- speciesToSkip = vampire
- minYears = 50
- maxYears = 500
