    .data
a:
    1221
    .text
main:
    load %x0, $a, %x3
    load %x0, $a, %x4
    add %x0, %x0, %x5
loop:
    muli %x5, 10, %x5
    divi %x4, 10, %x4
    add %x5, %x31, %x5
    bgt %x4, %x0, loop
    beq %x5, %x3, success
    jmp endl
success:
    addi %x0, 1, %x10 
    end
endl:
    subi %x0, 1, %x10 
    end