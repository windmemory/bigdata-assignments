##Assignment 5

####Question 1 What is the scalability issue with this particular HBase table design?

In this design, all the docids and values are stored in the same column family which would be a massive number of column qualifiers. As the collection grows bigger and bigger, there might be one row contains millions of column quilifiers. when the user request for one row, hbase will send back a huge array to the user which is hard to process.

####Question 2 How would you fix it? You don't need to actually implement the solution; just outline the design of both the indexer and retrieval engine that would overcome the scalability issues in the previous question.

From the reference website, I learnt that the column families in one row can be distributed across multiple nodes, so to fix the scalbility issue, the table should contains more column families. Each column family contains a range of document ids. Thus even if one row contains millions of docids, the database could still store them in multiple nodes. 

When we want retrive the data, the retrieval engine should get all the column families name first, then access them one by one. 


Reference : https://www.mapr.com/products/product-overview/apache-hbase
