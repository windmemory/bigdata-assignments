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
0.38857 9470136
0.09418 7992850
0.08586 7891871
0.08063 10208640
0.06603 9427340
0.06603 8747858
0.03546 8702415
0.03183 8669492
0.02246 7970234
0.01693 8846238

Source: 9300650
0.44688 9300650
0.09108 10765057
0.08888 9074395
0.07597 10687744
0.07597 8832646
0.07597 9621997
0.01556 10448801
0.01511 9785148
0.01511 10369305
0.01511 11890488
```


####Something

I went to Alaska, here is the Aurora Timelapse link, hope you enjoy it. 

https://youtu.be/bC-PqdlqweU
