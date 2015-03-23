##Assignment 4

####Running Code

hadoop jar target/assignment4-1.0-SNAPSHOT-fatjar.jar edu.umd.windmemory.BuildPersonalizedPageRankRecords \
   -input sample-large.txt -output windmemory-PageRankRecords -numNodes 1458 -sources 9470136,9300650

hadoop jar target/assignment4-1.0-SNAPSHOT-fatjar.jar edu.umd.windmemory.PartitionGraph \
   -input windmemory-PageRankRecords -output windmemory-PageRank/iter0000 -numPartitions 5 -numNodes 1458

hadoop jar target/assignment4-1.0-SNAPSHOT-fatjar.jar edu.umd.windmemory.RunPersonalizedPageRankBasic \
   -base windmemory-PageRank -numNodes 1458 -start 0 -end 20 -sources 9470136,9300650

hadoop jar target/assignment4-1.0-SNAPSHOT-fatjar.jar edu.umd.windmemory.Sequentialextract \
-input windmemory-PageRank/iter0020 -top 10 -sources 9470136,9300650



####Status
I've finished the assignment. I chose regular map reduce without in-mapper combiner. I changed the PageRankNode class. I change the pagerank attribute into array and add an attribute with name "curPoint" which indicate that the current source the program is calculating. I didn't change the input and output type of pagerank getter and setter which makes it easier for me to transform from single source to multiple sources. 

Because I set the array of pageranks size of 10, so my program can only calculate multiple nodes that less than ten. 

####Answer

```
Source: 9470136
0.38293 9470136
0.09284 7992850
0.08464 7891871
0.07948 10208640
0.06509 9427340
0.06509 8747858
0.03495 8702415
0.03137 8669492
0.02213 7970234
0.01669 8846238

Source: 9300650
0.44118 9300650
0.08992 10765057
0.08775 9074395
0.07500 10687744
0.07500 8832646
0.07500 9621997
0.01536 10448801
0.01492 9785148
0.01492 10369305
0.01492 11890488
```

####Something

I don't know why, but when I compare my answer with my classmates, I got bigger weight of source node and smaller weight of the others. I checked my code and the fomular, can't find anything wrong with my code. Although the weights are different, but the rank is still the same. 
