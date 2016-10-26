// RUN: %tool "%s" > "%t"
// RUN: %diff %CORRECT "%t"

int foo() {

    int x;
    int y;
    x = x + (!y);
    return 0;

}