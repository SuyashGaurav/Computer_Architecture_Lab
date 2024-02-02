    .data
n:
    15
    .text
main:
    load %x0, $n, %x3
    subi %x3, 2, %x3
    add %x0, %x0, %x4
    addi %x0, 1, %x5
    addi %x0, 65535, %x7
    store %x4, 0, %x7
    subi %x7, 1, %x7
    store %x5, 0, %x7
    subi %x7, 1, %x7
loop:
    add %x4, %x5, %x6
    store %x6, 0, %x7
    subi %x7, 1, %x7
    addi %x5, 0, %x4
    addi %x6, 0, %x5
    subi %x3, 1, %x3
    bgt %x3, %x0, loop
    end
