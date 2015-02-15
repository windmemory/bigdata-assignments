##Assignment 2

####Question 0
My solution is pretty straightforward, I used two jobs to calculate the PMI, the first one count the times that a word appears in a line, use this result as a dictionary for the second job. The second job count the times that a pair of word appears in a line. After that, in the reducer, with P(x,y), P(x) and P(y), calculate the PMI value for each pair and emit the result. I use pairs and Stripes in the second mapreduce job. 

####Question 1
I ran my code on my Mac, so for the pairs implementation, the time is around 70 sec, and the stripes solution is about 20 sec.

####Question 2
The time for pairs is about 120 sec, and the time for stripes implementation is about 30sec.

####Question 3
The total number of output is 233518, so the distinct pairs I extract is 116759.

####Question 4
The highest PMI pair is `(meshach, shadrach)	-1.146128035678238`. These two words might be two names, and they should have a lot of stories in common. As names, they don't usually appear with many different words, so the P(x) and P(y) value might be relative low, hence the PMI get the highest value.

####Question 5
The three words has the highest PMI with "cloud" are:
```
(tabernacle, cloud)	-3.3900868754237656

(glory, cloud)	-3.7176099910219627

(fire, cloud)	-3.7885748898239133
```

The three words has the highest PMI with "love" are:
```
(hermia, love)	-4.3125427704132955

(commandments, love)	-4.35138826655764

(lysander, love)	-4.371847044880222
```
