    .data
a:
    1
    .text
main:
    load %x0, $a, %x4
    andi %x4, 1, %x5
    addi %x0, 1, %x6
    beq %x5, %x6, success
    jmp endl
success:
    addi %x0, 1, %x10 
    end
endl:
    subi %x0, 1, %x10 
    end