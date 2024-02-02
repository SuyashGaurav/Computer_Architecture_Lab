	.data
a:
	70
	80
	40
	20
	10
	30
	50
	60
n:
	8
	.text
main:
	load %x0, $n, %x4
	addi %x0, 0, %x21
	addi %x21, 0, %x22
loop:
	beq %x21, %x4, break
	load %x21, $a, %x11
	load %x22, $a, %x6
	bgt %x6, %x11, swap
incrementj:
	addi %x22, 1, %x22
	beq %x22, %x4, nextloop
	jmp loop
nextloop:
	addi %x21, 1, %x21
	addi %x21, 0, %x22
	jmp loop
swap:
	addi %x6, 0, %x30
	addi %x11, 0, %x6
	addi %x30, 0, %x11
	store %x11, $a, %x21
	store %x6, $a, %x22
	jmp incrementj
break:
	end