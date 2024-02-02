    .data
a:
    6
    .text
main:
    load %x0, $a, %x3
    addi %x0, 2, %x4
    addi %x0, 1, %x7
    beq %x3, %x4, success
    beq %x3, %x7, endl
loop:
    div %x3, %x4, %x5
    beq %x31, %x0, endl
    addi %x4, 1, %x4
    mul %x4, %x4, %x6
    blt %x6, %x3, loop 
    beq %x6, %x3, loop
    jmp success
success:
    addi %x0, 1, %x10 
    end
endl:
    subi %x0, 1, %x10 
    end