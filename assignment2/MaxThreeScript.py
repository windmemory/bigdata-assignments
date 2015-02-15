def getKey(item):
	items = item.split('\t')
	return float(items[1])


maxVal = -999999.000
maxStr = None
cloudAar = []
loveAar = []
for i in range(0, 4):
    f = open('wc/part-r-0000' + str(i), 'r')
    for line in f:
        if "cloud)" in line:
        	cloudAar.append(line)
        if "love)" in line:
        	loveAar.append(line)
cloudAar.sort(key = getKey, reverse = True)
i = 0
for line in cloudAar:
	print(line)
	i = i + 1
	if i > 2:
		break
loveAar.sort(key = getKey, reverse = True)
for line in loveAar:
	print(line)
	i = i + 1
	if i > 5:
		break

