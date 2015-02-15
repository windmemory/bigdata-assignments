maxVal = -999999.000
maxStr = None
for i in range(0, 4):
    f = open('wc/part-r-0000' + str(i), 'r')
    for line in f:
        strs = line.split('\t')
        num = float(strs[1])
        if (num > maxVal):
        	maxStr = line
        	maxVal = num

	print(maxStr)