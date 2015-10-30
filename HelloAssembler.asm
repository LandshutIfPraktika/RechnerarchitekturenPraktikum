.data
	hello: .asciiz "Hello Assembler\n"
	input: .asciiz "Input max 10 Characters: "
.text
	li $v0, 4 #load print string syscall
	la $a0, hello #load hello string adress into right register
	syscall
	
	la $a0, input #load input string adress
	syscall
	
	addi $a0, $zero, 12 #load memmory size to be allocaed
	li $v0, 9 # load sbrk syscall
	syscall
	
	add $a0, $v0, $zero #load adress for buffer into right register
	li $a1, 10 #load maximum read characters
	li $v0, 8 #load read string syscall
	syscall
	
	addi $v0, $v0, -4 #load print string syscall
	syscall 