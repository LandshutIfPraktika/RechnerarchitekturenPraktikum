.text
Main:

	li	$v0,	5
	syscall
	
	add	$a0,	$v0,	$zero
	
	jal	ProcUnnec
	
	add	$a0,	$v0,	$zero
	li	$v0,	1
	syscall
	
	
	li	$v0	10
	syscall


ProcUnnec:
	li 	$t0,	-1
	ble	$a0,	$t0,	EndProc	#<0 |return 23
	
	li	$t0,	2
	div  	$a0,	$t0 		#n%2
	mfhi	$t0

	addi	$sp,	$sp,	-12	
	sw	$t0,	0($sp)		#store remainder
	
	addi	$t0,	$a0,	42	#(n+42)
	sw	$t0,	4($sp)		#store n+42
	sw	$ra,	8($sp)		#store return
	
	addi	$a0,	$a0,	-1	#argument n-1
	jal	ProcUnnec
	lw	$ra,	8($sp)		#load return
	lw	$t0,	4($sp)		#load n+42
	lw	$t1, 	0($sp)		#load %2
	addi	$sp,	$sp,	12
	beqz	$t1,	Plus
	mul	$v0,	$v0,	-1	#proUnnec(n-1) * (-1)**n
	Plus:
	add	$v0, 	$v0,	$t0
	
	jr	$ra
	
	EndProc:
	li	$v0,	23
	jr	$ra
