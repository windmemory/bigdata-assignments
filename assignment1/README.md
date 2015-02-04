#Assignment 1
##Code:

```
hadoop jar target/assignment1-1.0-SNAPSHOT-fatjar.jar edu.umd.windmemory.DemoWordCount \
-input bible+shakes.nopunc -output wc -numReducers 5
```

######Code for Questions:

In the `Cloud9` folder:
```
1. head wc/part-r-00000
2. tail wc/part-r-00004
3. wc -l wc/*
```

In the `Assignment1` folder:

```
4. head wc/part-r-00000
5. tail wc/part-r-00004
6. wc -l wc/*
```

##Answers:
####Question 1: 
The first term in `part-r-00000` is:

```
''but 1
```

####Question 2:
The third to last term in `part-r-00004` was: 

```text
zorah	8
```

####Question 3:
The number of unique terms in each output file:

```text
8604 wc/part-r-00000
8310 wc/part-r-00001
8226 wc/part-r-00002
8397 wc/part-r-00003
8251 wc/part-r-00004
41788 total
```

####Question 4:
The first term in `part-r-00000` is:

```
aaron	416
```

####Question 5:
The third to last term in `part-r-00004` was: 

```text
zorah	8
```

####Question 6:
The number of unique terms in each output file:

```
6611 wc/part-r-00000
6335 wc/part-r-00001
6264 wc/part-r-00002
6432 wc/part-r-00003
6298 wc/part-r-00004
31940 total
```

#Issue
When I was doing the assignment, I encountered an issue that the hadoop instruction cannot be excuted. I find out a solution which is deleting the `LICENSE` file. On stackoverflow, a person said that the `license` folder and `LICENSE` file has conflict in a case-insensitive file system. So here is the code I used to delete the `LICENSE` file in the jar.


```
zip -d target/assignment1-1.0-SNAPSHOT-fatjar.jar META-INF/LICENSE
```

Stackoverflow [link] (http://stackoverflow.com/questions/10522835/hadoop-java-io-ioexception-mkdirs-failed-to-create-some-path/11379938#11379938)
