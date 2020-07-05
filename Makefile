SortedArray: 
	@javac -d bin src/part1/*.java
	@read -p "Enter test number: " num; \
	echo "Result:"; \
	java -cp bin part1.SortedArray < data/BinSearchTests/Test$$num.in; \
	echo "Expected:"; \
	cat data/BinSearchTests/Test$$num.ans
SearchTree: 
	@javac -d bin src/part1/*.java
	@read -p "Enter test number: " num; \
	echo "Result:"; \
	java -cp bin part1.SearchTree < data/BinSearchTests/Test$$num.in; \
	echo "Expected:"; \
	cat data/BinSearchTests/Test$$num.ans
OtherArray: 
	@javac -d bin src/part1/*.java
	@read -p "Enter test number: " num; \
	echo "Result:"; \
	java -cp bin part1.OtherArray < data/BinSearchTests/Test$$num.in; \
	echo "Expected:"; \
	cat data/BinSearchTests/Test$$num.ans
RankSelectNaive:
	@javac -d bin src/part2/*.java
	@read -p "Enter test number: " num; \
	echo "Result:"; \
	java -cp bin part2.RankSelectNaive < data/Rank-selectTests/Test$$num.in; \
	echo "Expected:"; \
	cat data/Rank-selectTests/Test$$num.ans 
RankSelectLookUp:
	@javac -d bin src/part2/*.java
	@read -p "Enter test number: " num; \
	echo "Result:"; \
	java -cp bin part2.RankSelectLookUp < data/Rank-selectTests/Test$$num.in; \
	echo "Expected:"; \
	cat data/Rank-selectTests/Test$$num.ans
RankSelectSpaceEfficient:
	@javac -d bin src/part2/*.java
	@read -p "Enter test number: " num; \
	echo "Result:"; \
	java -cp bin part2.RankSelectSpaceEfficient < data/Rank-selectTests/Test$$num.in; \
	echo "Expected:"; \
	cat data/Rank-selectTests/Test$$num.ans
p1_test: 
	@javac -d bin src/part1/*.java
	@read -p "algo(s): " arg; \
	sh src/part1/BstTest.sh $$arg
p1_exp:
	@javac -d bin src/part1/*.java
	@java -cp bin part1.Experiment
p2_test:
	@javac -d bin src/part2/*.java
	java -cp bin part2.Testing
p2_exp:
	@javac -d bin src/part2/*.java
	@java -cp bin part2.Experiment
clean:
	@find bin -type f -name '*.class' -delete