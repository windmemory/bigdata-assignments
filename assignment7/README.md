##Assignment 7

####Pig Script

######Analysis #1

```
a = load '/shared/tweets2011.txt' as (id: long, name: chararray, create: chararray, content: chararray);
b = foreach a generate ToDate(create, 'EEE MMM dd HH:mm:ss Z yyyy') as date;
c = foreach b generate ToString(date, 'MM/dd HH') as date;
d = group c by date;
f = foreach d generate group as date, COUNT(c) as count;

store f into 'pig-all';
```

######Analysis #2

```
a = load '/shared/tweets2011.txt' as (id: long, name: chararray, create: chararray, content: chararray);
b = filter a BY content MATCHES '.*([Ee][Gg][Yy][Pp][Tt]|[Cc][Aa][Ii][Rr][Oo]).*';
c = foreach b generate ToDate(create, 'EEE MMM dd HH:mm:ss Z yyyy') as date;
d = foreach c generate ToString(date, 'MM/dd HH') as date;
f = group d by date;
e = foreach f generate group as date, COUNT(d) as count;

store e into 'pig-egypt';
```

####Spark

######Self defined function

```
def formatTime(s: Array[String]): String = {
	val strings = s(2).split(' ')
	
	val cast = Map("Jan" -> "01", 
		       "Feb" -> "02", 
		       "Mar" -> "03",
		       "Apr" -> "04",
		       "May" -> "05",
		       "Jun" -> "06",
		       "Jul" -> "07",
		       "Aug" -> "08",
		       "Sep" -> "09",
		       "Oct" -> "10",
		       "Nov" -> "11",
		       "Dec" -> "12")
	var result = ""
	result += cast(strings(1)) + "/"
	result += strings(2) + " "
	result += strings(3).substring(0, 2)
	result
}
```

######Analysis #1

```
val lines = sc.textFile("/shared/tweets2011.txt")
val tweetsCount = lines.map(l => l.split('\t')).filter(l => l.length == 4).map(formatTime).map(time => (time, 1)).reduceByKey(_ + _)
tweetsCount.saveAsTextFile("spark-all")
```

######Analysis #2

```
val lines = sc.textFile("/shared/tweets2011.txt")
val tweetsCount = lines.map(l => l.split('\t')).filter(l => l.length == 4).filter(t => t(3).matches(".*([Ee][Gg][Yy][Pp][Tt]|[Cc][Aa][Ii][Rr][Oo]).*")).map(formatTime).map(time => (time, 1)).reduceByKey(_ + _)
tweetsCount.saveAsTextFile("spark-egypt")
```


####Sorting

I didn't sort the result in pig or spark and also, the results from pig and spark was multiple files, so I use unix command to combine the files and sort them. 

```
cat pig-all/* | sort > hourly-counts-pig-all.txt
cat pig-egypt/* | sort > hourly-counts-pig-egypt.txt
cat spark-all/* | sort > hourly-counts-spark-all.txt
cat spark-egypt/* | sort > hourly-counts-spark-egypt.txt
```
