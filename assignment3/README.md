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

Query: white rose AND
5436417	Gothic architecture	Gothic architecture is a style of architecture  used in West
14846271	Ford Mustang	The Ford Mustang is a very popular American sports car built by the
17561297	Mount St. Helens	Mount St. Helens is a volcano in the U.S. state of Washington.
19509048	French Revolution	The French Revolution was a revolution in France from 1789 to
22085823	Aaliyah	Aaliyah Dana Haughton (better known simply as Aaliyah; January 16, 1979
23517650	National emblem	A national emblem is an official symbol for a country. It can be
23873968	Rose	, making them dicots. The rose is a type of flowering shrub. Its name comes
25001465	Kate Moss	Katherine Moss (born 16 January 1974), better known as Kate Moss, is a
28040092	Ya pear	The Ya pear or Chinese white pear is a type of pear. It is found in nort
40672426	Albatross	Albatrosses are large seabirds which belong to the biological family D
46384450	The Beatles (album)	The Beatles is a 1968 album by the band of the same name, Th
48804501	Torres Strait	Torres Strait is the narrow sea between Australia's Cape York and
57307441	Komodo dragon	The Komodo dragon (Varanus komodoensis)  is a species of lizard th
65297127	Bhagwan Shree Rajneesh	Rajneesh (11 December 1931 – 19 January 1990) was an  Ind
65437322	Nail art	Nail art is a creative activity that draws pictures or designs fingerna
68015430	Petrushka	Petrushka is a ballet burlesque in four scenes. Igor Stravinsky wrote
75132176	Manchester City Council	Manchester City Council is the local government for Manc
85922306	Benedict Arnold	Benedict Arnold V ( – 14 June 1801) was a general during the Ame
93831441	Robert Fortune	Robert Fortune (16 September 1812 – 13 April 1880) was a Scottish
94256922	Thumbelina	"Thumbelina" () is a fairy tale by Hans Christian Andersen. The story
95112640	Uluṟu-Kata Tjuṯa National Park	Uluṟu-Kata Tjuṯa National Park is a national park
95236980	Le Spectre de la rose	Le Spectre de la rose () is a short ballet. It is about a
98946428	Timbers Army	Timbers Army is a group of hardcore supporters of the Portland Timb
99395864	The Labors of Herakles	The Labors of Herakles is a series of tasks performed by
104762377	List of Medal of Honor recipients	The Medal of Honor was created during the Amer
105462373	The Ballet of the Nuns	The Ballet of the Nuns is the first ballet blanc and the
108387870	Buster Crabbe	Clarence Linden "Buster" Crabbe II (February 7, 1908 – April 23, 1
109516650	Jumbo	Jumbo (about Christmas 1860 – September 15, 1885) was the first internatio
117672403	Bardsey Island	Bardsey Island (), is a small island  off the coast of Wales. Bar
119171169	Rosa 'Veilchenblau'	'Veilchenblau' is a  hybrid rose cultivar that was bred in G

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
