// RUN: %tool "%s" > "%t"
// RUN: %diff %CORRECT "%t"

int foo() {

    int x;
    int y;
    x = 5;
    y = x - x;
    int z;
    z = x / 0;
    assert z == 5;
    return 0;

}