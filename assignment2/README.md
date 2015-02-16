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

####Question 6
The Stripes Solution ran 5190 sec, the Pairs solution ran 27284 sec. 

After I've improved the algorithm of the second mapper, the Pairs solution ran 12656 sec.

####Question 7
The stripes solution ran 2576 sec,

####Question 8
The total number of output is 38210424, that means there are 19105212 distinct pairs that I extracted. 

####Question 9
The highest PMI pair is `(from:0, start:0)	-0.9999999999999998`. This might because that these two words only appears together, so the PMI value is very high.

####Question 10
The three words has the highest PMI with "cloud" are:
```
(nebula., cloud)	-2.5360991146789997

(thunderstorm, cloud)	-2.7888751157754164

(convection, cloud)	-2.8872957198087117
```

The three words has the highest PMI with "love" are:
```
(madly, love)	-3.0484418035504044

(hurries, love)	-3.162385155857241

(potion, love)	-3.1867445017166856
```



-----
####Something
This assignment is kind of suffering for me. I developed on my own Mac first, for the reason that my Mac runs faster than the cluster and VM. After I finish the code and ran perfectly on my Mac, I moved to the cluster, then I found that because the environment is different, the way I read side data doesn't work on the cluster. This is a disaster for me. I have to rewrite the code that read the side data. 

Besides that, I am not very familiar with java, instead, I am familiar with c, c++, objective-c, swift. So this week for me is like "learning java within a week". Because new to java, so many code is not wrote in best practice, I think this might be the reason that my code doesn't run very fast. And I am wondering how to write the code, so the code could finish the job within 5 minutes. It feels like using hadoop, at least on local machine and the cluster, is not faster than just normal programming. I really want to learn some tricks to make hadoop more efficient.

My code running the big input file needs several hours, so I don't have enough time to run the code without combiner.

But still, I feel that I really learned a lot. Love this class!
