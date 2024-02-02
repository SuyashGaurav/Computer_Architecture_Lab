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
    div %x3, 2, %x6
    bgt %x4, %x6, success
    jmp loop
success:
    addi %x0, 1, %x10 
    end
endl:
    subi %x0, 1, %x10 
    end