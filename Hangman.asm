.data
	EnterText: .asciiz "Enter a word of a maximum length of 50:"
	GuessLetter: .asciiz "Guess a letter:"
	NewLine: .asciiz "\n"
	CurrentSolution: .asciiz "__________________________________________________________"
	FailureMessage: .asciiz "oh that is so sad, try again!"
	VictoryMessage: .asciiz "Yeah, you got it!"
	Secret: .space 51
	
.text
main:
	li $v0, 4 #syscall print string
	la $a0, EnterText
	syscall
	
	li $v0, 8 #syscall read string
	la $a0, Secret
	li $a1, 50
	syscall
	
	addi $s0, $zero, 100
	ClearLoop:
		beqz $s0, EndClear
		jal printNewLine
		addi $s0, $s0, -1
		j ClearLoop
	EndClear:		
	
	la $a0, Secret #count input string length
	jal countStringLength
	
	la $t0, CurrentSolution #cut current solution to size
	add $t1, $t0, $v0 #find where \0 needs to be
	sb $zero, -1($t1)
	
	add $s7, $zero, $v0 #store secret length for later use
	li $s0, 15 #set turn counter to 15
	addi $s6, $v0, -1 #store number of to be guessed chars 

	InputLoop:	
		beqz $s6, Victory #check for victory
		beqz $s0, Failure #check for failure
		jal printNewLine
		li $v0, 4
		la $a0, CurrentSolution
		syscall
		InputTest:
		jal printNewLine
		li $v0, 4 #prepare challange
		la $a0, GuessLetter
		syscall
	
		li $v0, 12 #read character
		syscall
		beqz $v0, InputTest #test for input!= \0
		li $t1, 10
		beq $v0, $t1, InputTest #test for input != \n
		
		move $s5, $v0 #store char
		jal printNewLine
		
		addi $s0, $s0, -1 #decrease turns left
		add $t0, $zero, $s7 #initialize run variable
		StringLoop:
			la $t1, Secret
			add $t1, $t1, $t0 #pointer to last char
			lb $t2, 0($t1) #load char
			bne $s5, $t2, True #jump on !=
			la $t3, CurrentSolution #load pointer to solution
			add $t3, $t3, $t0 #find coresponding char
			sb $s5, 0($t3) #write char
			li $t5, 255 #get non ascii
			sb $t5, 0($t1) #write in secret
			addi $s6, $s6,  -1 #decrease to be found chars
			True:
			beqz $t0, InputLoop #complete string checked, ask for next char
			addi $t0,$t0, -1 #decrease run variable
			j StringLoop #loop

			 
		

	
Failure: #print lostMessage then end programm
	jal printNewLine
	li $v0, 4
	la $a0, FailureMessage
	syscall
	j End
	
Victory:
	jal printNewLine
	li $v0, 4
	la $a0, VictoryMessage
	syscall
	jal printNewLine
	li $v0, 4
	la $a0, CurrentSolution
	syscall
	j End		
	
End:
	li $v0, 10 #end programm
	syscall
		
countStringLength: #$a0 adress of string to be counted, $v0 returns length
	add $t0, $a0, $zero #string adress to $s0
	add $t1, $zero, $zero #countvariable to zero
	LoopStringLength:
		lb $t2, 0($t0)
		beqz $t2, endStringLength #branch on \0
		addi $t0, $t0, 1 #increase adress by 1
		addi $t1, $t1, 1 #increase count by one
		j  LoopStringLength
	endStringLength:
	add $v0, $t1, $zero
	jr $ra
	
printNewLine:
	li $v0, 4
	la $a0, NewLine
	syscall
	jr $ra
	
	
checkVictory:
	
	
