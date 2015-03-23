##Assignment 3

####Description
For compression, I transformed the `PairOfWritables<IntWritable, ArrayListWritable<PairOfInts>>` into a single `ArrayListWritable<VintWritable>`, the first element is the document frequency, and the following numbers are the postings. Numbers in the even positions stands for the word count in the document, and numbers in the odd positions stands for the document number. (PS: Here because array starts with 0, so the second element is in a odd position.) Also, I used d-gap compression, so the numbers in the odd positions are not the absolute document number, instead, it's the relative document number.

For scalability, I used secondary sorting. I change the intermidiate key to `PairsOfStrings`, which the left element is the word, and the second element is the document id. Since I am also using "*" to count the document frequency, so I used string here instead of Int.

####Question 1

The size of my compressed index for `bible+shakes.nopunc` is 3275 bytes. 

####Question 2

The size of my compressed index for 'simplewiki-20141222-pages-articles.txt` is 94463 bytes.

####Question 3

The answer is:

```
Query: outrageous fortune AND
40366757	The Wheel of Fortune	:For other uses, see Wheel of Fortune. The Wheel of Fortune

Query: means deceit AND
19334430	StarCraft	StarCraft is a real-time strategy game for the PC. It was created in 1
111918585	List of figures in Greek mythology	This is a list of gods, goddesses, people and

Query: white red OR rose AND pluck AND

Query: unhappy outrageous OR good your AND OR fortune AND
29136931	Battle of Bannockburn	The Battle of Bannockburn, fought on 23 and 24 June 1314,
40366757	The Wheel of Fortune	:For other uses, see Wheel of Fortune. The Wheel of Fortune
45397900	Arthur (TV series)	Arthur is a Canadian/American animated educational television
70161822	Cecilia (novel)	Cecilia, subtitled "Memoirs of an Heiress", is an 18th century n
121258440	Amulet of Thailand	Amulets of Thailand are something that Thai people have worn
```

####Something
This assignment is relatively easy comparing to the previous one. I think I am more familiar with the structure of hadoop program and also the java syntax.

Here is two things I learnt from the assignment, the first one is that for the `iterator` in java, only calling the `next()` method could make the `iterator` goes to the next, calling the `hasNext()` does not do that. I learnt this from dubugging. 

The second one is that don't use the same variable to form an array. Every time you want to push a new element to the array, just declared one. I used the same one to form the array, because as I remember that using the same one could save running time, but obviously, this is not the case. I got every words following with the same number. Then I found that this is the problem, and fixed it by declaring new element every time I want to push into the array. 
