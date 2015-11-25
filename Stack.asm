.data

Sentence: .asciiz "Das sollte nun auf dem Stack zu sehen sein"

.text

la 	$s0, 	Sentence 	#load beginning of sentence
add 	$t0, 	$zero, 	$zero 	# i:= 0

Loop:	
	lb	$t1,	0($s0)
	lb	$t2,	1($s0)
	lb	$t3,	2($s0)
	lb	$t4,	3($s0)
	addi	$sp,	$sp,	-4
	beqz	$t1,	End
	sb	$t1,	0($sp)
	beqz	$t2,	End
	sb	$t2,	1($sp)
	beqz	$t3,	End
	sb	$t3,	2($sp)
	beqz	$t4,	End
	sb	$t4,	3($sp)
	addi	$s0,	$s0,	4	
j Loop

End:
	li 	$v0, 	10 		#end programm
	syscall  

